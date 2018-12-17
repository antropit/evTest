package ru.fry.evtest

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.ContactsContract
import android.support.annotation.IdRes
import android.support.v7.app.AppCompatActivity
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.text.Editable
import android.widget.TextView

const val CONST_NAME = "NAME"
const val CONST_EMAIL = "EMAIL"
const val CONST_PHONE = "PHONE"

class MainActivity : AppCompatActivity() {

    val userAPI = UserAPI()
    var textUpdated = false

    val h: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what != userAPI.SUCCESS) {
                btnAuth.isEnabled = true
                btnLogin.isEnabled = true

                Toast.makeText(this@MainActivity, "Error: "+msg.what, Toast.LENGTH_SHORT).show()
            }

            setProfile()
        }
    }
    private val etName: EditText by bind(R.id.etName)
    private val etEmail: EditText by bind(R.id.etEmail)
    private val etPhone: EditText by bind(R.id.etPhone)

    private val btnAuth: Button by bind(R.id.btnAuth)
    private val btnLogin: Button by bind(R.id.btnLogin)
    
    private val textProfileCap: TextView by bind(R.id.textProfileCap)
    private val textProfile: TextView by bind(R.id.textProfile)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textWatcher = object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                btnLogin.isEnabled = true
                btnAuth.isEnabled = true

                textUpdated = true
            }
        }

        etName.addTextChangedListener(textWatcher)
        etEmail.addTextChangedListener(textWatcher)
        etPhone.addTextChangedListener(textWatcher)

        loadText()
        setProfile()
    }

    override fun onDestroy() {
        super.onDestroy()

        if (textUpdated) saveText()
    }

    fun onClick(v: View) {
        if (textUpdated) saveText()

        when(v.id) {
            R.id.btnAuth -> userAPI.auth(
                etName.text.toString(),
                etEmail.text.toString(),
                etPhone.text.toString(),
                h)
            R.id.btnLogin -> {
                btnLogin.isEnabled = false
                btnAuth.isEnabled = false
                userAPI.login(etPhone.text.toString(), h)
            }
            R.id.btnProfile -> userAPI.getProfile(h)
            else -> Toast.makeText(this, "No such button", Toast.LENGTH_SHORT).show()
        }
    }

    fun <T : View> Activity.bind(@IdRes res : Int) : Lazy<T> {
        @Suppress("UNCHECKED_CAST")
        return lazy(LazyThreadSafetyMode.NONE) { findViewById(res) as T }
    }

    private fun loadText() {
        val sPref = getPreferences(Context.MODE_PRIVATE)

        etName.setText(sPref.getString(CONST_NAME, ""))
        etEmail.setText(sPref.getString(CONST_EMAIL, ""))
        etPhone.setText(sPref.getString(CONST_PHONE, ""))

        textUpdated = false
    }

    private fun saveText() {
        val ed: SharedPreferences.Editor = getPreferences(Context.MODE_PRIVATE).edit()
        ed.putString(CONST_NAME, etName.getText().toString())
        ed.putString(CONST_EMAIL, etEmail.getText().toString())
        ed.putString(CONST_PHONE, etPhone.getText().toString())
        ed.apply()

        textUpdated = false
    }

    private fun setProfile() {
        if (userAPI.profile.count() > 0) {
            textProfileCap.visibility = View.VISIBLE
            textProfile.visibility = View.VISIBLE
        } else {
            textProfileCap.visibility = View.INVISIBLE
            textProfile.visibility = View.INVISIBLE
        }
        textProfile.text = userAPI.profile
    }

}
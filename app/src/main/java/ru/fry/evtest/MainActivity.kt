package ru.fry.evtest

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Message
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
                findViewById<Button>(R.id.btnAuth).isEnabled = true
                findViewById<Button>(R.id.btnLogin).isEnabled = true

                Toast.makeText(this@MainActivity, "Error: "+msg.what, Toast.LENGTH_SHORT).show()
            }

            setProfile()
        }
    }

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
                findViewById<Button>(R.id.btnLogin).isEnabled = true
                findViewById<Button>(R.id.btnAuth).isEnabled = true

                textUpdated = true
            }
        }

        findViewById<EditText>(R.id.etName).addTextChangedListener(textWatcher)
        findViewById<EditText>(R.id.etEmail).addTextChangedListener(textWatcher)
        findViewById<EditText>(R.id.etPhone).addTextChangedListener(textWatcher)

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
                findViewById<EditText>(R.id.etName).text.toString(),
                findViewById<EditText>(R.id.etEmail).text.toString(),
                findViewById<EditText>(R.id.etPhone).text.toString(),
                h)
            R.id.btnLogin -> {
                findViewById<Button>(R.id.btnLogin).isEnabled = false
                findViewById<Button>(R.id.btnAuth).isEnabled = false
                userAPI.login(findViewById<EditText>(R.id.etPhone).text.toString(), h)
            }
            R.id.btnProfile -> userAPI.getProfile(h)
            else -> Toast.makeText(this, "No such button", Toast.LENGTH_SHORT).show()
        }
    }


    private fun loadText() {
        val sPref = getPreferences(Context.MODE_PRIVATE)

        findViewById<EditText>(R.id.etName).setText(sPref.getString(CONST_NAME, ""))
        findViewById<EditText>(R.id.etEmail).setText(sPref.getString(CONST_EMAIL, ""))
        findViewById<EditText>(R.id.etPhone).setText(sPref.getString(CONST_PHONE, ""))

        textUpdated = false
    }

    private fun saveText() {
        val ed: SharedPreferences.Editor = getPreferences(Context.MODE_PRIVATE).edit()
        ed.putString(CONST_NAME, findViewById<EditText>(R.id.etName).getText().toString())
        ed.putString(CONST_EMAIL, findViewById<EditText>(R.id.etEmail).getText().toString())
        ed.putString(CONST_PHONE, findViewById<EditText>(R.id.etPhone).getText().toString())
        ed.apply()

        textUpdated = false
    }

    private fun setProfile() {
        if (userAPI.profile.count() > 0) {
            findViewById<TextView>(R.id.textProfileCap).visibility = View.VISIBLE
            findViewById<TextView>(R.id.textProfile).visibility = View.VISIBLE
        } else {
            findViewById<TextView>(R.id.textProfileCap).visibility = View.INVISIBLE
            findViewById<TextView>(R.id.textProfile).visibility = View.INVISIBLE
        }
        findViewById<TextView>(R.id.textProfile).text = userAPI.profile
    }

}
package ru.fry.evtest

import android.os.Handler
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class UserAPI {

    private val BASE_URL: String = "http://api.tau.green:3099/v1"
    private val AUTH_CODE = "0000"

    val SUCCESS: Int = 200
    val FAIL: Int = 404

    var token: String = ""
    var profile: String = ""

    fun auth(name: String, email: String, phone: String, h: Handler) {
        val t = Thread( Runnable {
            val urlObj = URL(BASE_URL + "/ev/user/register")
            val jsonData = JSONObject()
            jsonData.put("name", name)
            jsonData.put("email", email)
            jsonData.put("phone", phone)

            val urlConnection = urlObj.openConnection() as HttpURLConnection
            try {
                val jsonBytes = jsonData.toString().toByteArray()

                urlConnection.doOutput = true
                urlConnection.requestMethod = "POST"
                urlConnection.setRequestProperty("Content-Type", "application/json")
                urlConnection.setRequestProperty("Content-Length", jsonBytes.size.toString())
                urlConnection.outputStream.write(jsonBytes)

                val status = urlConnection.responseCode
                h.sendMessage(h.obtainMessage(status, urlConnection.responseMessage))
            } catch (e: Exception) {
                h.sendMessage(h.obtainMessage(FAIL, e))
            } finally {
                urlConnection.disconnect()
            }
        })
        t.start()
    }

    fun login(phone: String, h: Handler) {
        val t = Thread( Runnable {
            val urlObj = URL(BASE_URL + "/ev/user/login")
            val jsonData = JSONObject()
            jsonData.put("phone", phone)
            jsonData.put("code", AUTH_CODE)

            val urlConnection = urlObj.openConnection() as HttpURLConnection
            try {
                val jsonBytes = jsonData.toString().toByteArray()

                urlConnection.doOutput = true
                urlConnection.requestMethod = "POST"
                urlConnection.setRequestProperty("Content-Type", "application/json")
                urlConnection.setRequestProperty("Content-Length", jsonBytes.size.toString())
                urlConnection.outputStream.write(jsonBytes)

                val resJSON = JSONObject(streamToString(urlConnection.inputStream))
                token = resJSON.getString("token")

                val status = urlConnection.responseCode
                h.sendMessage(h.obtainMessage(status, urlConnection.responseMessage))
            } catch (e: Exception) {
                h.sendMessage(h.obtainMessage(FAIL, e))
            } finally {
                urlConnection.disconnect()
            }
        })
        t.start()
    }

    fun getProfile(h: Handler) {
        val t = Thread( Runnable {
            val urlObj = URL(BASE_URL + "/ev/user/current")

            try {
                with(urlObj.openConnection() as HttpURLConnection) {
                    requestMethod = "GET"
                    setRequestProperty("Authorization", "Bearer " + token)
                    connect()

                    profile = streamToString(inputStream)

                    h.sendMessage(h.obtainMessage(responseCode, profile))
                }
            } catch (e: Exception) {
                h.sendMessage(h.obtainMessage(FAIL, e))
            }
        })
        t.start()
    }

    private fun streamToString(inputStream: InputStream): String {
//        val bufferedReader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
//        val sb = StringBuilder()
//        try {
//            while (true) {
//                val line = bufferedReader.readLine() ?: break
//                sb.append(line)
//            }
//        } finally {
//            bufferedReader.close()
//        }
//        return sb.toString()
        return inputStream.bufferedReader().use { it.readText() }
    }
}
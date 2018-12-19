package ru.fry.evtest

import com.tngtech.java.junit.dataprovider.DataProvider
import com.tngtech.java.junit.dataprovider.DataProviderRunner
import com.tngtech.java.junit.dataprovider.UseDataProvider

import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

@RunWith(DataProviderRunner::class)
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    private val BASE_ENDPOINT = "http://api.tau.green:3099/v1"

    companion object {
        @DataProvider
        @JvmStatic
        fun endpoints(): Array<String> {
            return arrayOf("/ev/user/register", "/ev/user/login", "/ev/user/login/phone", "/ev/user/current")
    }}

    @Test
    @UseDataProvider("endpoints")
    @Throws(IOException::class)
    fun baseUrlReturns200(endpoint: String) {
        val urlObj = URL(BASE_ENDPOINT + endpoint)

        val urlConnection = urlObj.openConnection() as HttpURLConnection
        val inputStream = urlConnection.inputStream

        val status = urlConnection.responseCode
        assertEquals(status.toLong(), 200)
    }
}

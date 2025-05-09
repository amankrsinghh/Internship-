package com.example.task1

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private var etCityName: EditText? = null
    private var btnFetchWeather: Button? = null
    private var tvWeatherResult: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        setContentView(R.layout.activity_main)


        // Initialize UI components
        etCityName = findViewById(R.id.etCityName)
        btnFetchWeather = findViewById(R.id.btnFetchWeather)
        tvWeatherResult = findViewById(R.id.tvWeatherResult)

        btnFetchWeather?.setOnClickListener(View.OnClickListener { fetchWeatherData() })


        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { v: View, insets: WindowInsetsCompat ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


    private fun fetchWeatherData() {
        val cityName = etCityName!!.text.toString().trim { it <= ' ' }
        if (cityName.isEmpty()) {
            tvWeatherResult!!.text = "Please enter a city name"
            return
        }
        //        6c9fd2bc97171b2de86ce0cd7a07f050
        val apiKey = "6c9fd2bc97171b2de86ce0cd7a07f050" // Replace with your OpenWeather API key
        val url =
            "https://api.openweathermap.org/data/2.5/weather?q=$cityName&appid=$apiKey"

        val requestQueue = Volley.newRequestQueue(this)
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response: String ->
                // Handle API response
                tvWeatherResult!!.text = parseWeatherResponse(response)
            },
            { error: VolleyError? ->
                // Handle errors
                tvWeatherResult!!.text = "Failed to fetch weather. Please try again."
            })

        requestQueue.add(stringRequest)
    }


    private fun parseWeatherResponse(response: String): String {
        try {
            val jsonObject = JSONObject(response)
            val cityName = jsonObject.getString("name")
            val main = jsonObject.getJSONObject("main")
            val temperature = main.getDouble("temp") - 273.15 // Convert Kelvin to Celsius
            val humidity = main.getDouble("humidity")

            val weather = jsonObject.getJSONArray("weather").getJSONObject(0)
            val description = weather.getString("description")

            return """
                City: $cityName
                Temperature: ${String.format("%.2f", temperature)}°C
                Humidity: $humidity%
                Description: $description
                """.trimIndent()
        } catch (e: Exception) {
            return "Failed to parse weather data"
        }
    }
}
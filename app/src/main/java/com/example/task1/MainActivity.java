package com.example.task1;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private EditText etCityName;
    private Button btnFetchWeather;
    private TextView tvWeatherResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        // Initialize UI components
        etCityName = findViewById(R.id.etCityName);
        btnFetchWeather = findViewById(R.id.btnFetchWeather);
        tvWeatherResult = findViewById(R.id.tvWeatherResult);


        btnFetchWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchWeatherData();
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    private void fetchWeatherData() {
        String cityName = etCityName.getText().toString().trim();
        if (cityName.isEmpty()) {
            tvWeatherResult.setText("Please enter a city name");
            return;
        }
//        6c9fd2bc97171b2de86ce0cd7a07f050
        String apiKey = "";  // Replace with your OpenWeather API key
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=" + apiKey;

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    // Handle API response
                    tvWeatherResult.setText(parseWeatherResponse(response));
                },
                error -> {
                    // Handle errors
                    tvWeatherResult.setText("Failed to fetch weather. Please try again.");
                });

        requestQueue.add(stringRequest);
    }


    private String parseWeatherResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String cityName = jsonObject.getString("name");
            JSONObject main = jsonObject.getJSONObject("main");
            double temperature = main.getDouble("temp") - 273.15;  // Convert Kelvin to Celsius
            double humidity = main.getDouble("humidity");

            JSONObject weather = jsonObject.getJSONArray("weather").getJSONObject(0);
            String description = weather.getString("description");

            return "City: " + cityName + "\n" +
                    "Temperature: " + String.format("%.2f", temperature) + "°C\n" +
                    "Humidity: " + humidity + "%\n" +
                    "Description: " + description;
        } catch (Exception e) {
            return "Failed to parse weather data";
        }
    }
}
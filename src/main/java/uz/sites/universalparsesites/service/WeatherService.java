package uz.sites.universalparsesites.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import uz.sites.universalparsesites.entity.Weather;
import uz.sites.universalparsesites.repository.WeatherRepository;

@Service
public class WeatherService {

    private final WeatherRepository weatherRepository;

    public WeatherService(WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
    }

    @Scheduled(cron = "0 0 8 * * ?")  // Har kuni soat 08:00 da ishga tushadi
    public void fetchWeatherData() {
        String API_KEY = "1dcb2323a60be0d379e9a5c031daac52"; // OpenWeatherMap API kaliti
        String CITY = "Tashkent";
        String urlString = "https://api.openweathermap.org/data/2.5/weather?q=" + CITY + "&appid=" + API_KEY + "&units=metric";

        try {
            JSONObject json = getJsonObject(urlString);
            double temp_min = json.getJSONObject("main").getDouble("temp_min");
            double temp_max = json.getJSONObject("main").getDouble("temp_max");
            JSONArray weatherArray = json.getJSONArray("weather");
            String weatherNameFirst = weatherArray.getJSONObject(0).getString("main");
            String weatherNameSecond;
            try {
                weatherNameSecond = weatherArray.getJSONObject(1).getString("main");
            } catch (Exception exception) {
                weatherNameSecond = weatherNameFirst;
            }
            List<Weather> weatherList = weatherRepository.findAll();
            Weather weather = weatherList.get(0);
            weather.setTempMin(temp_min);
            weather.setTempMax(temp_max);
            weather.setWeatherNameFirst(weatherNameFirst);
            weather.setWeatherNameSecond(weatherNameSecond);
            weather.setUpdateDate(new Timestamp(System.currentTimeMillis()));
            weatherRepository.save(weather);
        } catch (Exception ignored) {

        }
    }

    @NotNull
    private static JSONObject getJsonObject(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json");

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        return new JSONObject(response.toString());
    }

    public Weather getCurrentWeather() {
        List<Weather> weatherList = weatherRepository.findAll();
        return weatherList.get(0);
    }
}


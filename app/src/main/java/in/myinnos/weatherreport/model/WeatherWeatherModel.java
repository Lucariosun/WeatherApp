package in.myinnos.weatherreport.model;

import com.google.gson.annotations.SerializedName;


/**
 * Final Project-543.
 * Authors=Rahul, Manvitha, sharwari.
 */

public class WeatherWeatherModel {

    @SerializedName("id")
    String weatherCode;
    @SerializedName("description")
    String description;
    @SerializedName("main")
    String main;
    @SerializedName("icon")
    String weatherIcon;


    public WeatherWeatherModel(String weatherCode, String description, String main, String weatherIcon) {
        this.weatherCode = weatherCode;
        this.description = description;
        this.main = main;
        this.weatherIcon = weatherIcon;
    }

    public String getWeatherCode() {
        return weatherCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public String getWeatherIcon() {
        return weatherIcon;
    }
}

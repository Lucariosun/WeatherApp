package in.myinnos.weatherreport.model;

/**
 * Final Project-543.
 * Authors=Rahul, Manvitha, sharwari.
 */
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class ForecastMainModel {

    @SerializedName("main")
    WeatherMainModel weatherMainModel;
    @SerializedName("weather")
    List<WeatherWeatherModel> weatherWeatherModel;
    @SerializedName("wind")
    WeatherWindModel weatherWindModel;
    @SerializedName("dt_txt")
    String dateTime;
    @SerializedName("dt")
    long unixUTC;

    String localTime;

    public ForecastMainModel(WeatherMainModel weatherMainModel,
                            List<WeatherWeatherModel> weatherWeatherModel, WeatherWindModel weatherWindModel,
                             String dateTime, long unixUTC) {
        this.weatherMainModel = weatherMainModel;
        this.weatherWeatherModel = weatherWeatherModel;
        this.weatherWindModel = weatherWindModel;
        this.dateTime = dateTime;
        this.unixUTC = unixUTC;
        this.localTime = "";
    }

    public WeatherMainModel getWeatherMainModel() {
        return weatherMainModel;
    }

    public void setWeatherMainModel(WeatherMainModel weatherMainModel) {
        this.weatherMainModel = weatherMainModel;
    }

    public List<WeatherWeatherModel> getWeatherWeatherModel() {
        return weatherWeatherModel;
    }

    public WeatherWindModel getWeatherWindModel() {
        return weatherWindModel;
    }

    public String getDateTime() { return dateTime; }

    public long getUnixUTC() { return unixUTC; }

    public String getLocalTime() { return localTime; }

    public void setLocalTime(String time) {
        this.localTime = time;
    }
}

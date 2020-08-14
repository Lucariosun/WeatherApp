package in.myinnos.weatherreport.model;

/**
 * Final Project-543.
 * Authors=Rahul, Manvitha, sharwari.
 */
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class WeatherBaseModel {

   /* @SerializedName("coord")
    WeatherCoordModel weatherCoordModel;*/
    @SerializedName("main")
    WeatherMainModel weatherMainModel;
    @SerializedName("weather")
    List<WeatherWeatherModel> weatherWeatherModel;
    @SerializedName("wind")
    WeatherWindModel weatherWindModel;
    @SerializedName("timezone")
    int timezone;

    public WeatherBaseModel(WeatherMainModel weatherMainModel,
                            List<WeatherWeatherModel> weatherWeatherModel, WeatherWindModel weatherWindModel,
                            int timezone) {
        this.weatherMainModel = weatherMainModel;
        this.weatherWeatherModel = weatherWeatherModel;
        this.weatherWindModel = weatherWindModel;
        this.timezone = timezone;
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

    public void setWeatherWeatherModel(List<WeatherWeatherModel> weatherWeatherModel) {
        this.weatherWeatherModel = weatherWeatherModel;
    }

    public WeatherWindModel getWeatherWindModel() {
        return weatherWindModel;
    }

    public void setWeatherWindModel(WeatherWindModel weatherWindModel) {
        this.weatherWindModel = weatherWindModel;
    }

    public int getTimezone() {
        return timezone;
    }

}

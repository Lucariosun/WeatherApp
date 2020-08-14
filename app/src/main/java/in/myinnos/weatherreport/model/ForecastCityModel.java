package in.myinnos.weatherreport.model;

/**
 * Final Project-543.
 * Authors=Rahul, Manvitha, sharwari.
 */
import com.google.gson.annotations.SerializedName;

public class ForecastCityModel {

    @SerializedName("timezone")
    int timezone;

    @SerializedName("sunrise")
    int sunrise;

    @SerializedName("sunset")
    int sunset;

    public ForecastCityModel(int timezone, int sunrise, int sunset) {
        this.timezone = timezone;
        this.sunrise = sunrise;
        this.sunset = sunset;
    }

    public int getTimeZone() {
        return timezone;
    }

    public int getSunRise() {
        return sunrise;
    }

    public int getSunSet() {
        return sunset;
    }



}

package in.myinnos.weatherreport.model;

import com.google.gson.annotations.SerializedName;


/**
 * Final Project-543.
 * Authors=Rahul, Manvitha, sharwari.
 */

public class WeatherWindModel {

    @SerializedName("speed")
    Double speed;

    public WeatherWindModel(Double speed) {
        this.speed = speed;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }
}

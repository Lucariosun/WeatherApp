package in.myinnos.weatherreport.model;

import com.google.gson.annotations.SerializedName;


/**
 * Final Project-543.
 * Authors=Rahul, Manvitha, sharwari.
 */

public class WeatherCoordModel {

    @SerializedName("lon")
    Long lon;
    @SerializedName("lat")
    Long lat;

    public WeatherCoordModel(Long lon, Long lat) {
        this.lon = lon;
        this.lat = lat;
    }

    public Long getLon() {
        return lon;
    }

    public void setLon(Long lon) {
        this.lon = lon;
    }

    public Long getLat() {
        return lat;
    }

    public void setLat(Long lat) {
        this.lat = lat;
    }
}

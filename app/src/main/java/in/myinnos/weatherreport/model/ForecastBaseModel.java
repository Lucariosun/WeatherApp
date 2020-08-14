package in.myinnos.weatherreport.model;

/**
 * Final Project-543.
 * Authors=Rahul, Manvitha, sharwari.
 */
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ForecastBaseModel {

    @SerializedName("list")
    List<ForecastMainModel> forecastMainModel;
    @SerializedName("city")
    ForecastCityModel forecastCityModel;

    public ForecastBaseModel(List<ForecastMainModel> forecastMainModel,  ForecastCityModel forecastCityModel) {
        this.forecastMainModel = forecastMainModel;
        this.forecastCityModel = forecastCityModel;
    }

    public List<ForecastMainModel> getForecastMainModel() {
        return forecastMainModel;
    }

    public  ForecastCityModel getForecastCityModel() { return forecastCityModel; }
}

package in.myinnos.weatherreport.apiInterface;

import in.myinnos.weatherreport.model.ForecastBaseModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiForecastInterface {

    ////////////////////////////////////////////////////////////////////////////////

    @GET("forecast")
    Call<ForecastBaseModel> getWeatherData(
            @Query("appid") String appId,
            @Query("q") String query,
            @Query("units") String units
    );
}

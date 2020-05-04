package iooojik.app.klass.api;

import iooojik.app.klass.models.weather.WeatherData;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApi {

    //получение погоды
    @GET("weather?")
    Call<WeatherData> getWeather(@Query("lat") String lat,
                                 @Query("lon") String lon,
                                 @Query("appid") String api_key);
}

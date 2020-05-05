package iooojik.app.klass.api;

import iooojik.app.klass.models.ServerResponse;
import iooojik.app.klass.models.bonusCrate.CratesData;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface MapsApi {
    //получение маршрута
    @GET("api/directions/json?")
    Call<ServerResponse<CratesData>> getCrates(@Header("X-API-KEY") String api_key,
                                               @Header("X-TOKEN") String token,
                                               @Query("field") String field, @Query("filter") String filter);
}

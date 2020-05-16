package iooojik.app.klass.api;

import iooojik.app.klass.models.translation.TranslationResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TranslateApi {

    //перевод текста
    @GET("api/v1.5/tr.json/translate?")
    Call<TranslationResponse> translate(@Query("key") String key, @Query("text") String text, @Query("lang") String lang,
                                        @Query("format") String format);

}

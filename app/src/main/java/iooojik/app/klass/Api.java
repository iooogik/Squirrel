package iooojik.app.klass;

import java.util.HashMap;

import iooojik.app.klass.models.ServerResponse;
import iooojik.app.klass.models.authorization.SignUpResult;
import iooojik.app.klass.models.getToken.DataToken;
import iooojik.app.klass.models.matesList.DataUsersToGroup;
import iooojik.app.klass.models.profileData.ProfileData;
import iooojik.app.klass.models.pupil.DataPupilList;
import iooojik.app.klass.models.teacher.AddGroupResult;
import iooojik.app.klass.models.teacher.DataGroup;
import iooojik.app.klass.models.userData.Data;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Api {
    @Headers({
            "X-API-KEY: " + AppСonstants.X_API_KEY,
            "Content-Type: application/x-www-form-urlencoded"
    })

    //авторизация
    @FormUrlEncoded
    @POST("api/user/login")
    Call<ServerResponse<Data>> UserLogin(@FieldMap HashMap<String, String> map);

    //распределение по классам
    @FormUrlEncoded
    @POST("api/users_to_group/add")
    Call<ServerResponse<PostResult>> addUserToGroup(@Header("X-API-KEY") String api_key,
                                                    @Header("X-TOKEN") String token,
                                                    @FieldMap HashMap<String, String> map);
    //регистрация
    @FormUrlEncoded
    @POST("api/user/add")
    Call<SignUpResult> userRegistration(@Header("X-API-KEY") String api_key,
                                        @Header("X-TOKEN") String token,
                                        @FieldMap HashMap<String, String> map,
                                        @Field("group") String group);

    @FormUrlEncoded
    @POST("api/groups/add")
    Call<ServerResponse<AddGroupResult>> addGroup(@Header("X-API-KEY") String api_key,
                                                  @Header("X-TOKEN") String token,
                                                  @FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("api/groups/update")
    Call<ServerResponse<PostResult>> updateTest(@Header("X-API-KEY") String api_key,
                                                    @Header("X-TOKEN") String token,
                                                    @FieldMap HashMap<String, String> map);
    //получение админского токена
    @FormUrlEncoded
    @POST("api/user/request_token")
    Call<DataToken> request_token(@Header("X-API-KEY") String api_key,
                                  @FieldMap HashMap<String, String> map);

    @GET("api/groups/all?")
    Call<ServerResponse<DataGroup>> getGroups(@Header("X-API-KEY") String api_key,
                                              @Query("field") String field, @Query("filter") String email);

    @GET("api/user/detail")
    Call<ServerResponse<ProfileData>> getUserDetail(@Header("X-API-KEY") String api_key,
                                                    @Header("X-TOKEN") String token,
                                                    @Query("id") int id);

    @GET("api/users_to_group/all?")
    Call<ServerResponse<DataUsersToGroup>> getMatesList(@Header("X-API-KEY") String api_key,
                                                        @Query("field") String field, @Query("filter") String group_id);

    @GET("api/users_to_group/all?")
    Call<ServerResponse<DataPupilList>> getPupilActiveGroups(@Header("X-API-KEY") String api_key,
                                                             @Header("X-TOKEN") String token,
                                                             @Query("field") String field,
                                                             @Query("filter") String email);

    @GET("api/groups/detail?")
    Call<ServerResponse<iooojik.app.klass.groupProfile.DataGroup>> groupDetail(@Header("X-API-KEY") String api_key,
                                                                               @Header("X-TOKEN") String token,
                                                                               @Query("_id") int id);

}

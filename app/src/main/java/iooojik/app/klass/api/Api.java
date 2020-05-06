package iooojik.app.klass.api;

import java.util.HashMap;

import iooojik.app.klass.AppСonstants;
import iooojik.app.klass.models.PostResult;
import iooojik.app.klass.models.ServerResponse;
import iooojik.app.klass.models.test_results.DataTestResult;
import iooojik.app.klass.models.achievements.AchievementsData;
import iooojik.app.klass.models.authorization.SignUpResult;
import iooojik.app.klass.models.bonusCrate.CratesData;
import iooojik.app.klass.models.caseData.CaseData;
import iooojik.app.klass.models.getToken.DataToken;
import iooojik.app.klass.models.groups_messages.DataMessage;
import iooojik.app.klass.models.matesList.DataUsersToGroup;
import iooojik.app.klass.models.notesData.NotesData;
import iooojik.app.klass.models.paramUsers.ParamData;
import iooojik.app.klass.models.profileData.ProfileData;
import iooojik.app.klass.models.promocode.PromoData;
import iooojik.app.klass.models.pupil.DataPupilList;
import iooojik.app.klass.models.shop.ShopData;
import iooojik.app.klass.models.teacher.AddGroupResult;
import iooojik.app.klass.models.teacher.DataGroup;
import iooojik.app.klass.models.userData.UserData;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

public interface Api {
    @Headers({"X-API-KEY: " + AppСonstants.X_API_KEY,
            "Content-Type: application/x-www-form-urlencoded"})

    //авторизация
    @FormUrlEncoded
    @POST("api/user/login")
    Call<ServerResponse<UserData>> UserLogin(@FieldMap HashMap<String, String> map);

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

    //обновлени пользовательской информации
    @Multipart
    @POST("api/user/update")
    Call<ServerResponse<PostResult>> userUpdateAvatar(@Header("X-API-KEY") String api_key,
                                                      @Header("X-TOKEN") String token,
                                                      @PartMap HashMap<String, RequestBody> map);
    //добавление группы(класса)
    @FormUrlEncoded
    @POST("api/groups/add")
    Call<ServerResponse<AddGroupResult>> addGroup(@Header("X-API-KEY") String api_key,
                                                  @Header("X-TOKEN") String token,
                                                  @FieldMap HashMap<String, String> map);
    //обновление тестов
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

    //жобавление пользовательских заметок в беху
    @FormUrlEncoded
    @POST("api/notes/add")
    Call<ServerResponse<PostResult>> uploadNotes(@Header("X-API-KEY") String api_key, @Header("X-TOKEN") String token,
                                  @FieldMap HashMap<String, String> map);

    //добавление результата о выполнении теста
    @FormUrlEncoded
    @POST("api/tests_result/add")
    Call<ServerResponse<PostResult>> addResult(@Header("X-API-KEY") String api_key,
                                                @Header("X-TOKEN") String token,
                                                @FieldMap HashMap<String, String> map);

    //логирование получения "достижения"
    @FormUrlEncoded
    @POST("api/achievements_changes/add")
    Call<ServerResponse<PostResult>> logAchievement(@Header("X-API-KEY") String api_key,
                                               @Header("X-TOKEN") String token,
                                               @FieldMap HashMap<String, String> map);

    //обновление "достижений"
    @FormUrlEncoded
    @POST("api/achievements_to_users/update")
    Call<ServerResponse<PostResult>> updateAchievement(@Header("X-API-KEY") String api_key,
                                                @Header("X-TOKEN") String token,
                                                @FieldMap HashMap<String, String> map);

    //обновление промо-кода для лас
    @FormUrlEncoded
    @POST("api/las_promo/update")
    Call<ServerResponse<PostResult>> changeStatePromo(@Header("X-API-KEY") String api_key,
                                                       @Header("X-TOKEN") String token,
                                                       @FieldMap HashMap<String, String> map);

    //удаление всех пользовательских заметок из базы
    @FormUrlEncoded
    @POST("api/notes/delete")
    Call<ServerResponse<PostResult>> removeNotes(@Header("X-API-KEY") String api_key, @Field("_id") String id);

    //логирование покупки в магазине
    @FormUrlEncoded
    @POST("api/shop_logs/add")
    Call<ServerResponse<PostResult>> logBuying(@Header("X-API-KEY") String api_key,
                                               @Header("X-TOKEN") String token,
                                               @FieldMap HashMap<String, String> map);

    //добавление "достижения"
    @FormUrlEncoded
    @POST("api/achievements_to_users/add")
    Call<ServerResponse<PostResult>> addAchievement(@Header("X-API-KEY") String api_key,
                                               @Header("X-TOKEN") String token,
                                               @FieldMap HashMap<String, String> map);

    //добавление сообщения для группы
    @FormUrlEncoded
    @POST("api/messages_to_groups/add")
    Call<ServerResponse<PostResult>> addGroupMessage(@Header("X-API-KEY") String api_key,
                                                    @Header("X-TOKEN") String token,
                                                    @FieldMap HashMap<String, String> map);
    //удаление пользователя из списка
    @FormUrlEncoded
    @POST("api/users_to_group/delete")
    Call<ServerResponse<PostResult>> removeMate(@Header("X-API-KEY") String api_key, @Field("_id") String id);

    //удаление результатов пользователя
    @FormUrlEncoded
    @POST("api/tests_result/delete")
    Call<ServerResponse<PostResult>> removeResult(@Header("X-API-KEY") String api_key, @Field("_id") String id);

    //удаление группы
    @FormUrlEncoded
    @POST("api/groups/delete")
    Call<ServerResponse<PostResult>> removeGroup(@Header("X-API-KEY") String api_key, @Field("_id") String id);


    //добавление кейса
    @FormUrlEncoded
    @POST("api/bonus_crates_to_users/add")
    Call<ServerResponse<PostResult>> addCrate(@Header("X-API-KEY") String api_key,
                                              @Header("X-TOKEN") String token,
                                              @FieldMap HashMap<String, String> map);

    //обновление информации о кейсах
    @FormUrlEncoded
    @POST("api/bonus_crates_to_users/update")
    Call<ServerResponse<PostResult>> updateCrateInfo(@Header("X-API-KEY") String api_key,
                                                     @Header("X-TOKEN") String token,
                                                     @FieldMap HashMap<String, String> map);

    //добавление координат кейса
    @FormUrlEncoded
    @POST("api/cases_to_users/add")
    Call<ServerResponse<PostResult>> addCaseCoordinates(@Header("X-API-KEY") String api_key,
                                              @Header("X-TOKEN") String token,
                                              @FieldMap HashMap<String, String> map);
    @FormUrlEncoded
    @POST("api/passed_tests/add")
    Call<ServerResponse<PostResult>> addTestResult(@Header("X-API-KEY") String api_key,
                                                        @Header("X-TOKEN") String token,
                                                        @FieldMap HashMap<String, String> map);

    //удаление кейса
    @FormUrlEncoded
    @POST("api/cases_to_users/delete")
    Call<ServerResponse<PostResult>> removeCase(@Header("X-API-KEY") String api_key, @Field("_id") String id);



    //получение item-ов в магазине
    @GET("api/shop/all")
    Call<ServerResponse<ShopData>> getShopItems(@Header("X-API-KEY") String api_key,
                                                @Header("X-TOKEN") String token);

    @GET("api/passed_tests/all")
    Call<ServerResponse<ShopData>> getPassedTestResult(@Header("X-API-KEY") String api_key,
                                                @Header("X-TOKEN") String token);
    //получение информации о кейсах
    @GET("api/bonus_crates_to_users/all?")
    Call<ServerResponse<CratesData>> getCrates(@Header("X-API-KEY") String api_key,
                                               @Header("X-TOKEN") String token,
                                               @Query("field") String field, @Query("filter") String filter);

    //получение промо-кодов
    @GET("api/las_promo/all?")
    Call<ServerResponse<PromoData>> getPromo(@Header("X-API-KEY") String api_key,
                                                @Header("X-TOKEN") String token,
                                             @Query("field") String field, @Query("filter") String filter);

    //получение кейса
    @GET("api/cases_to_users/all?")
    Call<ServerResponse<CaseData>> getCase(@Header("X-API-KEY") String api_key,
                                           @Header("X-TOKEN") String token,
                                           @Query("field") String field, @Query("filter") String filter);

    //получение всех "достижений" пользователя
    @GET("api/achievements_to_users/all?")
    Call<ServerResponse<AchievementsData>> getAchievements(@Header("X-API-KEY") String api_key,
                                                           @Query("field") String field, @Query("filter") String email);
    //получение списка групп
    @GET("api/groups/all?")
    Call<ServerResponse<DataGroup>> getGroups(@Header("X-API-KEY") String api_key,
                                              @Query("field") String field, @Query("filter") String email);

    //получение списка заметок
    @GET("api/notes/all?")
    Call<ServerResponse<NotesData>> getNotes(@Header("X-API-KEY") String api_key,
                                             @Query("field") String field, @Query("filter") String filter);

    //получение пользовательских параметров
    @GET("api/user/all")
    Call<ServerResponse<ParamData>> getParamUser(@Header("X-API-KEY") String api_key,
                                                 @Header("X-TOKEN") String token,
                                                 @Query("field") String field, @Query("filter") String email);
    //запрос на детализацию профиля
    @GET("api/user/detail")
    Call<ServerResponse<ProfileData>> getUserDetail(@Header("X-API-KEY") String api_key,
                                                    @Header("X-TOKEN") String token,
                                                    @Query("id") int id);

    //получение "одноклассников"
    @GET("api/users_to_group/all?")
    Call<ServerResponse<DataUsersToGroup>> getMatesList(@Header("X-API-KEY") String api_key,
                                                        @Query("field") String field, @Query("filter") String group_id);

    //получени
    @GET("api/users_to_group/all?")
    Call<ServerResponse<DataPupilList>> getPupilActiveGroups(@Header("X-API-KEY") String api_key,
                                                             @Header("X-TOKEN") String token,
                                                             @Query("field") String field,
                                                             @Query("filter") String email);
    //детализация группы
    @GET("api/groups/detail?")
    Call<ServerResponse<iooojik.app.klass.groupProfile.DataGroup>> groupDetail(@Header("X-API-KEY") String api_key,
                                                                               @Header("X-TOKEN") String token,
                                                                               @Query("_id") int id);

    //получение результатов теста
    @GET("api/tests_result/all?")
    Call<ServerResponse<DataTestResult>> getTestResults(@Header("X-API-KEY") String api_key,
                                                       @Header("X-TOKEN") String token,
                                                       @Query("field") String field,
                                                       @Query("filter") String filter);

    //получение сообщений для группы
    @GET("api/messages_to_groups/all?")
    Call<ServerResponse<DataMessage>> getGroupMessage(@Header("X-API-KEY") String api_key,
                                                      @Header("X-TOKEN") String token,
                                                      @Query("field") String field,
                                                      @Query("filter") String filter);

}

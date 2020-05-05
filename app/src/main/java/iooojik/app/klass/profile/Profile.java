package iooojik.app.klass.profile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import iooojik.app.klass.AppСonstants;
import iooojik.app.klass.R;
import iooojik.app.klass.api.Api;
import iooojik.app.klass.api.WeatherApi;
import iooojik.app.klass.models.ServerResponse;
import iooojik.app.klass.models.achievements.AchievementsData;
import iooojik.app.klass.models.achievements.AchievementsToUser;
import iooojik.app.klass.models.profileData.Group;
import iooojik.app.klass.models.profileData.ProfileData;
import iooojik.app.klass.models.profileData.User;
import iooojik.app.klass.models.pupil.DataPupilList;
import iooojik.app.klass.models.pupil.PupilGroups;
import iooojik.app.klass.models.teacher.AddGroupResult;
import iooojik.app.klass.models.teacher.DataGroup;
import iooojik.app.klass.models.teacher.GroupInfo;
import iooojik.app.klass.models.weather.Weather;
import iooojik.app.klass.models.weather.WeatherData;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Profile extends Fragment implements View.OnClickListener {
    public Profile() {
    }

    private View view;
    private FloatingActionButton fab;
    private String userRole = "";
    private GroupsAdapter groupsAdapter;
    private Context context;
    private Fragment fragment;
    private SharedPreferences preferences;
    private Api api;
    private String email, fullName;
    private View header;
    private LocationManager locationManager;
    private LocationListener locationListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
        header = navigationView.getHeaderView(0);
        header.setPadding(0, 110, 0, 80);

        //получение текущего фрагмента, чтобы использовать его в адаптере
        fragment = this;
        //контекст
        context = getContext();
        //получаем fab и ставим слушатель на него
        fab = getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(this);
        preferences = getActivity().getSharedPreferences(AppСonstants.APP_PREFERENCES, Context.MODE_PRIVATE);

        //получаем координаты пользователя и показываем погоду, основываясь на координатах
        setLocationManager();

        ImageView main_avatar = view.findViewById(R.id.avatar);
        main_avatar.setOnClickListener(this);

        //запускаем поток получения/обновления данных
        getActivity().runOnUiThread(() -> {
            getUserProfile();
            getCoins(preferences.getString(AppСonstants.USER_EMAIL, ""));
        });
        return view;
    }

    private void setLocationManager() {
        //проверяем наличие разрешения на использование геолокации пользователя
        int permissionStatus = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        //если нет разрешения, то запрашиваем его, иначе показываем погоду
        if (!(permissionStatus == PackageManager.PERMISSION_GRANTED)) ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        else {
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    showWeather(location);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    }
                    showWeather(locationManager.getLastKnownLocation(provider));
                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };

            //получаем координаты из GPS или из сети
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
                    0, locationListener);

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,
                    0, locationListener);
        }
    }

    private void showWeather(Location location) {
        // Текущее время
        Date currentDate = new Date();
        //день.месяц.год
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String dateText = dateFormat.format(currentDate);

        preferences.edit().putString(AppСonstants.USER_LAT, String.valueOf(location.getLatitude())).apply();
        preferences.edit().putString(AppСonstants.USER_LON, String.valueOf(location.getLongitude())).apply();

        //проверяем, показывалось ли сегодня уведомление с погодой
        if ((!preferences.getString(AppСonstants.CURRENT_DATE, "").equals(dateText)
                && (preferences.getInt(AppСonstants.SHOW_WEATHER_NOTIF, 1) == 1))
                || preferences.getInt(AppСonstants.SHOW_WEATHER_NOTIF_ALWAYS, 0) == 1) {
            //заносим текущую дату, для последующих проверок
            preferences.edit().putString(AppСonstants.CURRENT_DATE, dateText).apply();
            //ретрофит
            WeatherApi weatherApi;
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(AppСonstants.WEATHER_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            weatherApi = retrofit.create(WeatherApi.class);
            //запрос
            Call<WeatherData> getWeatherCall = weatherApi.getWeather(String.valueOf(location.getLatitude()),
                    String.valueOf(location.getLongitude()), AppСonstants.WEATHER_API_KEY);

            getWeatherCall.enqueue(new Callback<WeatherData>() {
                @Override
                public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {

                    if (response.code() == 200) {
                        //показываем уведомление
                        showWeatherDialog(response.body());
                    } else Log.e("GETTING WEATHER", String.valueOf(response.raw()));
                }

                @Override
                public void onFailure(Call<WeatherData> call, Throwable t) {
                    Log.e("GETTING WEATHER", String.valueOf(t));
                }
            });
            //убираем слушатель, чтобы лишний раз не нагружать телефон
            locationManager.removeUpdates(locationListener);
       }
    }

    @SuppressLint("InflateParams")
    private void showWeatherDialog(WeatherData weatherData){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
        //получаем готовую view
        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.weather_notification, null, false);
        //картинка с погодой
        ImageView conditionImage = dialogView.findViewById(R.id.condition);
        //сообщение с краткой информацией о погоде
        TextView message = dialogView.findViewById(R.id.message);
        //подсказка для пользователя
        TextView secondMessage = dialogView.findViewById(R.id.secondary_message);
        //получаем информацию о погоде
        List<Weather> weathers = weatherData.getWeather();

        Weather weather = weathers.get(0);
        String condition = weather.getIcon();
        String messageText = "";
        //сопоставляем погоду и иконки
        switch (condition){
            case "01d":
                conditionImage.setImageResource(R.drawable.x01d_clear_sky);
                messageText = "Сегодня ясно, можно пойти погулять, но сначала надо сделать все тесты!";
                break;
            case "01n":
                conditionImage.setImageResource(R.drawable.x01n_clear_sky);
                messageText = "Сегодня ясно, можно пойти погулять, но сначала надо сделать все тесты!";
                break;
            case "02d":
                conditionImage.setImageResource(R.drawable.x02d_few_clouds);
                messageText = "Сегодня немного облачно, но можно пойти погулять. \n Не забудьте сделать все тесты!";
                break;
            case "02n":
                conditionImage.setImageResource(R.drawable.x02n_few_clouds);
                messageText = "Сегодня немного облачно, но можно пойти погулять. \n Не забудьте сделать все тесты!";
                break;
            case "03d":
                conditionImage.setImageResource(R.drawable.x03d_scattered_clouds);
                messageText = "Сегодня облачно. Если вы пойдёте гулять, прихватите с собой курточку, но не забудьте сделать все тесты!";
                break;
            case "03n":
                conditionImage.setImageResource(R.drawable.x03n_scattered_clouds);
                messageText = "Сегодня облачно. Если вы пойдёте гулять, прихватите с собой курточку, но не забудьте сделать все тесты!";
                break;
            case "04d":
                conditionImage.setImageResource(R.drawable.x04d_broken_clouds);
                messageText = "Сегодня облачно. Если вы пойдёте гулять, прихватите с собой курточку, но не забудьте сделать все тесты!";
                break;
            case "04n":
                conditionImage.setImageResource(R.drawable.x04n_broken_clouds);
                messageText = "Сегодня облачно. Если вы пойдёте гулять, прихватите с собой курточку, но не забудьте сделать все тесты!";
                break;
            case "09d":
                conditionImage.setImageResource(R.drawable.x09d_shower_rain);
                messageText = "Сегодня идёт сильный дождь. Лучше останьтесь дома и подготовьтесь к урокам или займитесь домашними делами!";
                break;
            case "09n":
                conditionImage.setImageResource(R.drawable.x09n_shower_rain);
                messageText = "Сегодня идёт сильный дождь. Лучше останьтесь дома и подготовьтесь к урокам или займитесь домашними делами!";
                break;
            case "10d":
                conditionImage.setImageResource(R.drawable.x10d_rain);
                messageText = "Сегодня дождливо. Лучше останьтесь дома и подготовьтесь к урокам или займитесь домашними делами!";
                break;
            case "10n":
                conditionImage.setImageResource(R.drawable.x10n_rain);
                messageText = "Сегодня дождливо. Лучше останьтесь дома и подготовьтесь к урокам или займитесь домашними делами!";
                break;
            case "11d":
                conditionImage.setImageResource(R.drawable.x11d_thunderstorm);
                messageText = "Сегодня штормит. Останьтесь дома и подготовьтесь к урокам или займитесь домашними делами!";
                break;
            case "11n":
                conditionImage.setImageResource(R.drawable.x11n_thunderstorm);
                messageText = "Сегодня штормит. Останьтесь дома и подготовьтесь к урокам или займитесь домашними делами!";
                break;
            case "13d":
                conditionImage.setImageResource(R.drawable.x13d_snow);
                messageText = "Сегодня идёт снег, самое время пойти и сделать парочку красивых фото!";
                break;
            case "13n":
                conditionImage.setImageResource(R.drawable.x13n_snow);
                messageText = "Сегодня идёт снег, самое время пойти и сделать парочку красивых фото!";
                break;
            case "50d":
                conditionImage.setImageResource(R.drawable.x50d_mist);
                messageText = "Сегодня туманно";
                break;
            case "50n":
                conditionImage.setImageResource(R.drawable.x50n_mist);
                messageText = "Сегодня туманно";
                break;
        }

        //устанавливаем информацию и показываем уведомление
        message.setText(messageText);
        secondMessage.setText("Вы можете посмотреть актуальную погоду в настроках, нажав кнопку 'Показать погоду'.");
        builder.setNegativeButton("Хорошо", (dialog, which) -> dialog.cancel());
        builder.setView(dialogView);
        builder.create().show();
    }

    @Override
    public void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    private void getCoins(String userEmail) {
        Call<ServerResponse<AchievementsData>> call = api.getAchievements(AppСonstants.X_API_KEY,
                "user_email", userEmail);

        call.enqueue(new Callback<ServerResponse<AchievementsData>>() {
            @Override
            public void onResponse(Call<ServerResponse<AchievementsData>> call, Response<ServerResponse<AchievementsData>> response) {
                if (response.code() == 200){
                    AchievementsData data = response.body().getData();
                    AchievementsToUser achievements = data.getAchievementsToUsers().get(0);
                    preferences.edit().putInt(AppСonstants.USER_COINS, Integer.parseInt(achievements.getCoins())).apply();
                    preferences.edit().putInt(AppСonstants.ACHIEVEMENTS_ID, Integer.parseInt(achievements.getId())).apply();
                    TextView coins = view.findViewById(R.id.coins);
                    coins.setText(String.valueOf(achievements.getCoins()));
                }
                else Log.e("GET ACHIEVEMENTS", String.valueOf(response.raw()));
            }

            @Override
            public void onFailure(Call<ServerResponse<AchievementsData>> call, Throwable t) {
                Log.e("GET ACHIEVEMENTS", String.valueOf(t));
            }
        });
    }

    private void getUserProfile() {

        doRetrofit();

        Call<ServerResponse<ProfileData>> call = api.getUserDetail(AppСonstants.X_API_KEY,
                preferences.getString(AppСonstants.AUTH_SAVED_TOKEN, ""),
                Integer.parseInt(preferences.getString(AppСonstants.USER_ID, "")));

        call.enqueue(new Callback<ServerResponse<ProfileData>>() {
            @Override
            public void onResponse(Call<ServerResponse<ProfileData>> call, Response<ServerResponse<ProfileData>> response) {
                if (response.code() == 200){
                    ProfileData profileData = response.body().getData();
                    User user = profileData.getUser();

                    email = user.getEmail();
                    fullName = user.getFullName();

                    preferences.edit().putString(AppСonstants.USER_EMAIL, email).apply();
                    preferences.edit().putString(AppСonstants.USER_ID, user.getId()).apply();
                    preferences.edit().putString(AppСonstants.USER_FULL_NAME, fullName).apply();

                    TextView name = view.findViewById(R.id.name);
                    TextView emailText = view.findViewById(R.id.email);
                    name.setText(fullName);
                    emailText.setText(email);

                    ImageView avatar = header.findViewById(R.id.side_avatar);
                    ImageView main_avatar = view.findViewById(R.id.avatar);

                    if (!user.getAvatar().isEmpty()) {
                        preferences.edit().putString(AppСonstants.USER_AVATAR,
                                AppСonstants.IMAGE_URL + user.getAvatar()).apply();

                        Picasso.get().load(AppСonstants.IMAGE_URL + user.getAvatar())
                                .resize(100, 100)
                                .transform(new RoundedCornersTransformation(30, 5)).into(avatar);

                        Picasso.get().load(AppСonstants.IMAGE_URL + user.getAvatar())
                                .resize(100, 100)
                                .transform(new RoundedCornersTransformation(30, 5)).into(main_avatar);

                    } else {
                        avatar.setImageResource(R.drawable.baseline_account_circle_24);
                        main_avatar.setImageResource(R.drawable.baseline_account_circle_24);
                    }

                    TextView nameHeader = header.findViewById(R.id.textView);
                    TextView email_text = header.findViewById(R.id.textView2);

                    nameHeader.setText(preferences.getString(AppСonstants.USER_FULL_NAME, ""));
                    email_text.setText(preferences.getString(AppСonstants.USER_EMAIL, ""));

                    Group group = user.getGroup().get(user.getGroup().size() - 1);
                    switch (group.getName().toLowerCase()){
                        case "teacher":
                            preferences.edit().putString(AppСonstants.USER_ROLE, "teacher").apply();
                            getGroups();
                            break;
                        case "pupil":
                            preferences.edit().putString(AppСonstants.USER_ROLE, "pupil").apply();
                            getPupilGroups();
                            break;
                    }

                    userRole = preferences.getString(AppСonstants.USER_ROLE, "").toLowerCase();

                }
            }

            @Override
            public void onFailure(Call<ServerResponse<ProfileData>> call, Throwable t) {

            }
        });


    }

    private void getPupilGroups() {
        fab.hide();
        //получаем к каким группам относится пользователь
        doRetrofit();
        Call<ServerResponse<DataPupilList>> responseCall = api.getPupilActiveGroups(AppСonstants.X_API_KEY,
                preferences.getString(AppСonstants.AUTH_SAVED_TOKEN, ""),
                "email", email);

        responseCall.enqueue(new Callback<ServerResponse<DataPupilList>>() {
            @Override
            public void onResponse(Call<ServerResponse<DataPupilList>> call, Response<ServerResponse<DataPupilList>> response) {
                if (response.code() == 200){
                    DataPupilList dataPupilList = response.body().getData();
                    List<PupilGroups> pupilGroups = dataPupilList.getPupilGroups();
                    if (pupilGroups.size() == 0){
                        TextView notif = view.findViewById(R.id.notif_text);
                        notif.setVisibility(View.VISIBLE);
                        notif.setText("Вы ещё не присодинились ни к одной группе");
                    } else {
                        PupilGroupsAdapter groupsAdapter = new PupilGroupsAdapter(pupilGroups, fragment, context);
                        RecyclerView recyclerView = view.findViewById(R.id.classes);
                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                        recyclerView.setAdapter(groupsAdapter);
                    }
                }else {
                    Log.e("GET PUPIL GROUPS", String.valueOf(response.raw()));
                }
            }

            @Override
            public void onFailure(Call<ServerResponse<DataPupilList>> call, Throwable t) {
                Log.e("GET PUPIL GROUPS", String.valueOf(t));
            }
        });
    }

    private void doRetrofit(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppСonstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(Api.class);
    }

    private void getGroups(){
        fab.show();
        fab.setImageResource(R.drawable.baseline_add_24);
        doRetrofit();
        Call<ServerResponse<DataGroup>> response = api.getGroups(AppСonstants.X_API_KEY, "author_email", email);

        response.enqueue(new Callback<ServerResponse<DataGroup>>() {
            @Override
            public void onResponse(Call<ServerResponse<DataGroup>> call, Response<ServerResponse<DataGroup>> response) {
                if(response.code() == 200) {
                    ServerResponse<DataGroup> result = response.body();
                    List<GroupInfo> groupInforms = result.getData().getGroupInfos();
                    if (groupInforms.size() == 0) {
                        TextView notif = view.findViewById(R.id.notif_text);
                        notif.setVisibility(View.VISIBLE);
                        notif.setText("Вы ещё не создали ни одну группу");
                    } else {
                        TextView warn = view.findViewById(R.id.notif_text);
                        warn.setVisibility(View.GONE);
                        groupsAdapter = new GroupsAdapter(context, groupInforms, fragment);
                        RecyclerView recyclerView = view.findViewById(R.id.classes);
                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                        recyclerView.setAdapter(groupsAdapter);
                    }

                } else {
                    Log.e("GETTING GROUPS", response.raw() + "");
                }
            }
            @Override
            public void onFailure(Call<ServerResponse<DataGroup>> call, Throwable t) {
                Log.e("GETTING GROUPS",t.toString());
            }
        });
    }

    @Override
    @SuppressLint("InflateParams")
    public void onClick(View v) {
        String teacherRole = "teacher";
        if (v.getId() == R.id.fab) {//добавление класса в учительский профиль
            if (userRole.equals(teacherRole)) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
                View view1 = getLayoutInflater().inflate(R.layout.edit_text, null);
                TextInputLayout textInputLayout = view1.findViewById(R.id.text_input_layout);
                textInputLayout.setHint("Название группы");
                textInputLayout.setCounterEnabled(false);
                textInputLayout.setHelperTextEnabled(false);
                EditText name = view1.findViewById(R.id.edit_text);

                builder.setView(view1);

                builder.setPositiveButton("Добавить", (dialog, which) -> {
                    //заносим в базу данных
                    doRetrofit();
                    String nameGroup = name.getText().toString();

                    HashMap<String, String> post = new HashMap<>();
                    post.put("author_email", email);
                    post.put("name", nameGroup);
                    post.put("test", "null");
                    post.put("author_name", fullName);

                    Call<ServerResponse<AddGroupResult>> responseCall = api.addGroup(
                            AppСonstants.X_API_KEY, preferences.getString(AppСonstants.AUTH_SAVED_TOKEN, ""),
                            post);

                    responseCall.enqueue(new Callback<ServerResponse<AddGroupResult>>() {
                        @Override
                        public void onResponse(Call<ServerResponse<AddGroupResult>> call, Response<ServerResponse<AddGroupResult>> response) {
                            if (response.code() != 200) {
                                Log.e("Add Group", String.valueOf(response.raw()));
                            } else {
                                getGroups();
                            }
                        }

                        @Override
                        public void onFailure(Call<ServerResponse<AddGroupResult>> call, Throwable t) {
                            Log.e("Add Group", String.valueOf(t));
                        }
                    });

                });
                builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());
                builder.create().show();
            }
        }
    }

}

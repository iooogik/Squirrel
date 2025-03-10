package iooojik.app.klass.profile;

import android.Manifest;
import android.annotation.SuppressLint;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;

import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import iooojik.app.klass.AppСonstants;
import iooojik.app.klass.Database;
import iooojik.app.klass.MainActivity;
import iooojik.app.klass.R;
import iooojik.app.klass.api.Api;
import iooojik.app.klass.api.WeatherApi;
import iooojik.app.klass.models.PostResult;
import iooojik.app.klass.models.ServerResponse;
import iooojik.app.klass.models.achievements.AchievementsData;
import iooojik.app.klass.models.achievements.AchievementsToUser;
import iooojik.app.klass.models.bonusCrate.CratesData;
import iooojik.app.klass.models.passed_test_result.DataPassedTest;
import iooojik.app.klass.models.passed_test_result.PassedTest;
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
import iooojik.app.klass.room_models.AppDatabase;
import iooojik.app.klass.room_models.profile.ProfileEntity;
import iooojik.app.klass.room_models.pupil_groups.GroupPupilEntity;
import iooojik.app.klass.room_models.statistic.StatisticDao;
import iooojik.app.klass.room_models.statistic.StatisticEntity;
import iooojik.app.klass.room_models.tests_results.TestResultEntity;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static iooojik.app.klass.AppСonstants.DATABASE_USER_ID;
import static iooojik.app.klass.AppСonstants.PICK_IMAGE_AVATAR;
import static iooojik.app.klass.AppСonstants.database;

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
    private View header;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private BottomSheetDialog statisticsDialog;
    private int groupCount = 0;
    private int resultsCount = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);


        preferences = getActivity().getSharedPreferences(AppСonstants.APP_PREFERENCES, Context.MODE_PRIVATE);

        if (database.profileDao().getAll().size() == 0){
            OnBackPressedCallback callback = new OnBackPressedCallback(true) {
                @Override public void handleOnBackPressed() {}};
            requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
            preferences.edit().clear().apply();

            getActivity().setTheme(R.style.AppThemeLight);

            database.notesDao().deleteAll();
            database.testDao().deleteAll();
            database.todoDao().deleteAll();
            database.groupPupilDao().deleteAll();
            database.matesDao().deleteAll();

            MaterialToolbar materialToolbar = getActivity().findViewById(R.id.bar);
            materialToolbar.setVisibility(View.GONE);
            DrawerLayout mDrawerLayout = getActivity().findViewById(R.id.drawer_layout);
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

            startActivity(new Intent(getContext(), MainActivity.class));
        } else {

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
            fab.show();


            //получаем координаты пользователя и показываем погоду, основываясь на координатах
            setLocationManager();

            ImageView main_avatar = view.findViewById(R.id.avatar);
            main_avatar.setOnClickListener(this);
            setHasOptionsMenu(true);
            userRole = database.profileDao().getById(DATABASE_USER_ID).getProfile_type();
            //запускаем поток получения/обновления данных
            new Thread(this::loadProfileData).start();
            new Thread(this::getUserProfile).start();
            new Thread(this::loadResultsFromDataBase).start();
            if (!userRole.isEmpty())
                getActivity().runOnUiThread(this::getDataByProfile);


            OnBackPressedCallback callback = new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                }
            };
            requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        }
        return view;
    }

    private void loadProfileData(){
        if (database.profileDao().getAll().size() > 0) {
            String email, fullName, avatarLink;
            int coins;
            ProfileEntity profileData = database.profileDao().getById(DATABASE_USER_ID);
            email = preferences.getString(AppСonstants.USER_EMAIL, "");
            fullName = profileData.getFull_name();
            avatarLink = AppСonstants.IMAGE_URL + profileData.getAvatar();
            coins = preferences.getInt(AppСonstants.USER_COINS, 0);

            TextView name = view.findViewById(R.id.name);
            TextView emailText = view.findViewById(R.id.email);
            TextView nameHeader = header.findViewById(R.id.textView);
            TextView email_text = header.findViewById(R.id.textView2);
            TextView coinsText = view.findViewById(R.id.coins);

            getActivity().runOnUiThread(() -> {
                name.setText(fullName);
                emailText.setText(email);
                nameHeader.setText(fullName);
                email_text.setText(email);
                coinsText.setText(String.valueOf(coins));
            });

            ImageView avatar = view.findViewById(R.id.avatar);
            ImageView main_avatar = header.findViewById(R.id.side_avatar);

            //если есть пользовательская автарка, то показываем её, иначе показываем стандартный значок
            if (profileData.getAvatar().trim().isEmpty()){
                avatar.setImageResource(R.drawable.baseline_account_circle_24);
                main_avatar.setImageResource(R.drawable.baseline_account_circle_24);
            }else {
                getActivity().runOnUiThread(() -> {
                    Picasso.with(context).load(avatarLink)
                            .resize(100, 100)
                            .transform(new RoundedCornersTransformation(30, 5)).into(avatar);

                    Picasso.with(context).load(avatarLink)
                            .resize(100, 100)
                            .transform(new RoundedCornersTransformation(30, 5)).into(main_avatar);
                });

            }
        }

    }

    private void setLocationManager() {
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
        try {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
                    0, locationListener);

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,
                    0, locationListener);
        } catch (Exception e) {
            Log.e("GPS LOCATION", String.valueOf(e));
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
        if (!preferences.getString(AppСonstants.CURRENT_DATE, "").equals(dateText)
                && (preferences.getInt(AppСonstants.SHOW_WEATHER_NOTIF, 0) == 1)) {
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
        int messageText = 0;
        //сопоставляем погоду и иконки
        switch (condition){
            case "01d":
                conditionImage.setImageResource(R.drawable.x01d_clear_sky);
                messageText = R.string.weather1;
                break;
            case "01n":
                conditionImage.setImageResource(R.drawable.x01n_clear_sky);
                messageText = R.string.weather1;
                break;
            case "02d":
                conditionImage.setImageResource(R.drawable.x02d_few_clouds);
                messageText = R.string.weather2;
                break;
            case "02n":
                conditionImage.setImageResource(R.drawable.x02n_few_clouds);
                messageText = R.string.weather2;
                break;
            case "03d":
                conditionImage.setImageResource(R.drawable.x03d_scattered_clouds);
                messageText = R.string.weather3;
                break;
            case "03n":
                conditionImage.setImageResource(R.drawable.x03n_scattered_clouds);
                messageText = R.string.weather3;
                break;
            case "04d":
                conditionImage.setImageResource(R.drawable.x04d_broken_clouds);
                messageText = R.string.weather3;
                break;
            case "04n":
                conditionImage.setImageResource(R.drawable.x04n_broken_clouds);
                messageText = R.string.weather3;
                break;
            case "09d":
                conditionImage.setImageResource(R.drawable.x09d_shower_rain);
                messageText = R.string.weather4;
                break;
            case "09n":
                conditionImage.setImageResource(R.drawable.x09n_shower_rain);
                messageText = R.string.weather4;
                break;
            case "10d":
                conditionImage.setImageResource(R.drawable.x10d_rain);
                messageText = R.string.weather5;
                break;
            case "10n":
                conditionImage.setImageResource(R.drawable.x10n_rain);
                messageText = R.string.weather5;
                break;
            case "11d":
                conditionImage.setImageResource(R.drawable.x11d_thunderstorm);
                messageText = R.string.weather6;
                break;
            case "11n":
                conditionImage.setImageResource(R.drawable.x11n_thunderstorm);
                messageText = R.string.weather6;
                break;
            case "13d":
                conditionImage.setImageResource(R.drawable.x13d_snow);
                messageText = R.string.weather7;
                break;
            case "13n":
                conditionImage.setImageResource(R.drawable.x13n_snow);
                messageText = R.string.weather7;
                break;
            case "50d":
                conditionImage.setImageResource(R.drawable.x50d_mist);
                messageText = R.string.weather8;
                break;
            case "50n":
                conditionImage.setImageResource(R.drawable.x50n_mist);
                messageText = R.string.weather8;
                break;
        }

        //устанавливаем информацию и показываем уведомление
        message.setText(getContext().getResources().getText(messageText));
        secondMessage.setText("Вы можете посмотреть актуальную погоду в настроках, нажав кнопку 'Показать погоду'.");
        builder.setNegativeButton(R.string.ok_ru, (dialog, which) -> dialog.cancel());
        builder.setView(dialogView);
        builder.create().show();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (locationListener != null) {
            //останавливаем навигацию
            locationManager.removeUpdates(locationListener);
        }
    }

    private void getCoins(String userEmail) {
        //получаем монетки пользователя
        Call<ServerResponse<AchievementsData>> call = api.getAchievements(AppСonstants.X_API_KEY,
                AppСonstants.USER_EMAIL_FIELD, userEmail);

        call.enqueue(new Callback<ServerResponse<AchievementsData>>() {
            @Override
            public void onResponse(Call<ServerResponse<AchievementsData>> call, Response<ServerResponse<AchievementsData>> response) {
                if (response.code() == 200){
                    AchievementsData data = response.body().getData();
                    if (data.getAchievementsToUsers().size() > 0) {
                        AchievementsToUser achievements = data.getAchievementsToUsers().get(0);
                        preferences.edit().putInt(AppСonstants.USER_COINS, Integer.parseInt(achievements.getCoins())).apply();
                        preferences.edit().putInt(AppСonstants.ACHIEVEMENTS_ID, Integer.parseInt(achievements.getId())).apply();
                        int coins = Integer.valueOf(achievements.getCoins());
                        if (coins != database.profileDao().getById(DATABASE_USER_ID).getCoins()){
                            new Thread(Profile.this::loadProfileData).start();
                        }
                    }
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
        //получаем пользовательскую информацию
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

                    preferences.edit().putString(AppСonstants.USER_EMAIL, user.getEmail()).apply();
                    preferences.edit().putString(AppСonstants.USER_ID, user.getId()).apply();
                    preferences.edit().putString(AppСonstants.USER_FULL_NAME, user.getFullName()).apply();

                    Group group = user.getGroup().get(user.getGroup().size() - 1);
                    preferences.edit().putString(AppСonstants.USER_ROLE, group.getName().toLowerCase()).apply();
                    userRole = preferences.getString(AppСonstants.USER_ROLE, "").toLowerCase();

                    //если данные не отличаются от предыдущих, то оставляем их и подгружаем остальные данные,
                    //иначе заменяем данные и в потоке подгружаем остальные данные
                    ProfileEntity profileEntity = database.profileDao().getById(DATABASE_USER_ID);
                    if (!(profileEntity.getFull_name().equals(user.getFullName())
                            || profileEntity.getAvatar().equals(user.getAvatar())
                            || profileEntity.getProfile_type().equals(group.getName().toLowerCase())))
                    {

                        ProfileEntity entity = database.profileDao().getById(DATABASE_USER_ID);

                        entity.setAvatar(user.getAvatar());
                        entity.setFull_name(user.getFullName());
                        entity.setProfile_type(group.getName().toLowerCase());

                        database.profileDao().update(entity);
                        new Thread(Profile.this::loadProfileData).start();
                    }

                }
            }

            @Override
            public void onFailure(Call<ServerResponse<ProfileData>> call, Throwable t) {

            }
        });

        getCoins(preferences.getString(AppСonstants.USER_EMAIL, ""));
        getBonusCases();

    }

    private void getDataByProfile(){
        switch (userRole){
            case "teacher":
                getGroups();
                break;
            case "pupil":
                Thread threadLoadLocalGroups = new Thread(this::loadGroupsPupil);
                Thread threadLoadGroups = new Thread(this::getPupilGroups);
                threadLoadLocalGroups.start();
                try {
                    threadLoadLocalGroups.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                threadLoadGroups.start();
               // loadLocalResults();
                break;
        }
    }

    private void loadLocalResults(){
        new Thread(this::showStatistic).start();
    }

    private void loadResultsFromDataBase(){
        doRetrofit();
        Call<ServerResponse<DataPassedTest>> responseCall = api.getPassedTestResult(AppСonstants.X_API_KEY,
                preferences.getString(AppСonstants.AUTH_SAVED_TOKEN, ""),
                AppСonstants.USER_EMAIL_FIELD, preferences.getString(AppСonstants.USER_EMAIL, ""));
        responseCall.enqueue(new Callback<ServerResponse<DataPassedTest>>() {
            @Override
            public void onResponse(Call<ServerResponse<DataPassedTest>> call, Response<ServerResponse<DataPassedTest>> response) {
                if (response.code() == 200){
                    if (response.body().getData().getPassedTests() != null) {
                        if (response.body().getData().getPassedTests().size() != resultsCount) {
                            database.testResultDao().deleteAll();
                            for (PassedTest result : response.body().getData().getPassedTests()) {
                                TestResultEntity entity = new TestResultEntity();
                                entity.setTest_name(result.getTestName());
                                entity.setResult(Integer.valueOf(result.getResult()));
                                database.testResultDao().insert(entity);
                            }
                            loadLocalResults();
                        }
                    }
                } else Log.e("GETTING RESULTS", String.valueOf(response.raw()));
            }

            @Override
            public void onFailure(Call<ServerResponse<DataPassedTest>> call, Throwable t) {
                Log.e("GETTING RESULTS", String.valueOf(t));
            }
        });
    }

    private void loadGroupsPupil(){
        List<GroupPupilEntity> groups = database.groupPupilDao().getAll();
        groupCount = groups.size();
        TextView notif = view.findViewById(R.id.notif_text);
        if (groupCount == 0){

            notif.setVisibility(View.VISIBLE);
            notif.setText("Вы ещё не присодинились ни к одной группе");
        }else {
            getActivity().runOnUiThread(() -> {
                notif.setVisibility(View.GONE);
                PupilGroupsAdapter groupsAdapter = new PupilGroupsAdapter(groups, fragment, context);
                RecyclerView recyclerView = view.findViewById(R.id.classes);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                recyclerView.setAdapter(groupsAdapter);
            });


        }

    }

    private void getPupilGroups() {
        //получаем к каким группам относится пользователь
        doRetrofit();
        Call<ServerResponse<DataPupilList>> responseCall = api.getPupilActiveGroups(AppСonstants.X_API_KEY,
                preferences.getString(AppСonstants.AUTH_SAVED_TOKEN, ""),
                AppСonstants.EMAIL_FIELD, preferences.getString(AppСonstants.USER_EMAIL, ""));

        responseCall.enqueue(new Callback<ServerResponse<DataPupilList>>() {
            @Override
            public void onResponse(Call<ServerResponse<DataPupilList>> call, Response<ServerResponse<DataPupilList>> response) {
                if (response.code() == 200){
                    DataPupilList dataPupilList = response.body().getData();
                    List<PupilGroups> pupilGroups = dataPupilList.getPupilGroups();
                    //если количество групп разное, то обновляем список,
                    // иначе подгружаем списки, сохранённые локально
                    if (pupilGroups.size() != groupCount){
                        database.groupPupilDao().deleteAll();
                        for (PupilGroups group : pupilGroups){
                            GroupPupilEntity entity = new GroupPupilEntity();
                            entity.setGroup_id(Integer.valueOf(group.getGroupId()));
                            entity.setGroup_name(group.getGroup_name());
                            entity.setAuthor_email(group.getEmail());
                            entity.setAuthor_name(group.getFullName());
                            database.groupPupilDao().insert(entity);
                        }
                        new Thread(Profile.this::loadGroupsPupil).start();
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
                .client(new OkHttpClient().newBuilder()
                        .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                        .build())
                .build();
        api = retrofit.create(Api.class);
    }

    private void getGroups(){
        fab.show();
        fab.setImageResource(R.drawable.baseline_add_24);
        doRetrofit();
        Call<ServerResponse<DataGroup>> response = api.getGroups(AppСonstants.X_API_KEY,
                AppСonstants.AUTHOR_EMAIL_FIELD, preferences.getString(AppСonstants.USER_EMAIL, ""));

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

    private void getBonusCases(){
        doRetrofit();
        Call<ServerResponse<CratesData>> getCrates = api.getCrates(AppСonstants.X_API_KEY,
            preferences.getString(AppСonstants.AUTH_SAVED_TOKEN, ""), AppСonstants.USER_EMAIL_FIELD,
                preferences.getString(AppСonstants.USER_EMAIL, ""));
        getCrates.enqueue(new Callback<ServerResponse<CratesData>>() {
            @Override
            public void onResponse(Call<ServerResponse<CratesData>> call1, Response<ServerResponse<CratesData>> response) {
                if (response.code() == 200){
                    CratesData data = response.body().getData();
                    if (data.getBonusCratesToUsers().size() != 0) {
                        preferences.edit().putString(AppСonstants.CASES,
                                data.getBonusCratesToUsers().get(0).getCount()).apply();
                    }
                }
            }

            @Override
            public void onFailure(Call<ServerResponse<CratesData>> call1, Throwable t) {

            }
        });
    }

    @Override
    @SuppressLint("InflateParams")
    public void onClick(View v) {
        String teacherRole = "teacher";
        switch (v.getId()){//добавление класса в учительский профиль
            case R.id.fab:
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
                        post.put("author_email", preferences.getString(AppСonstants.USER_EMAIL, ""));
                        post.put("name", nameGroup);
                        post.put("author_name", preferences.getString(AppСonstants.FULL_NAME_FIELD, ""));
                        post.put("count_questions", "0");
                        post.put("attachments", "null");
                        post.put("test", "null");


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
                break;
            case R.id.avatar:
                //запрос на разрешение использование памяти
                int permissionStatus = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
                if (!(permissionStatus == PackageManager.PERMISSION_GRANTED)) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]
                            {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
                if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_PICK);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_AVATAR);
                }
                break;
        }
    }

    @SuppressLint("Recycle")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_AVATAR) {
            if (data != null) {

                getActivity().runOnUiThread(() -> {
                    Uri selectedImage = data.getData();
                    File file = new File(getRealPathFromURI(context, selectedImage));

                    RequestBody requestFile =
                            RequestBody.create(MediaType.parse("multipart/form-data"), file);

                    MultipartBody.Part body =
                            MultipartBody.Part.createFormData("avatar", file.getName(), requestFile);

                    doRetrofit();

                    RequestBody requestBody;

                    HashMap<String, RequestBody> map = new HashMap<>();
                    requestBody = RequestBody.create(MediaType.parse("text/plain"),
                            preferences.getString(AppСonstants.USER_EMAIL, ""));

                    map.put("email", requestBody);

                    requestBody = RequestBody.create(MediaType.parse("text/plain"),
                            preferences.getString(AppСonstants.USER_PASSWORD, ""));
                    map.put("password", requestBody);

                    requestBody = RequestBody.create(MediaType.parse("text/plain"),
                            preferences.getString(AppСonstants.USER_FULL_NAME, ""));
                    map.put("full_name", requestBody);

                    requestBody = RequestBody.create(MediaType.parse("text/plain"),
                            preferences.getString(AppСonstants.USER_ID, ""));
                    map.put("id", requestBody);

                    Call<ServerResponse<PostResult>> postCall = api.userUpdateAvatar(AppСonstants.X_API_KEY,
                            preferences.getString(AppСonstants.AUTH_SAVED_TOKEN, ""), map, body );

                    Log.e("UPDATE AVATAR", map.toString() + " ");

                    postCall.enqueue(new Callback<ServerResponse<PostResult>>() {
                        @Override
                        public void onResponse(Call<ServerResponse<PostResult>> call,
                                               Response<ServerResponse<PostResult>> response) {

                            Log.e("UPDATE AVATAR", response.raw() + " " + file.getName());

                            if (response.code() == 200) {
                                getUserProfile();
                            } else
                                Log.e("UPDATE AVATAR", response.raw() + " " + file.getName());
                        }

                        @Override
                        public void onFailure(Call<ServerResponse<PostResult>> call, Throwable t) {
                            Log.e("UPDATE AVATAR", String.valueOf(t));
                        }
                    });



                    Call<ServerResponse<DataPupilList>> serverResponseCall = api.getPupilActiveGroups(AppСonstants.X_API_KEY,
                            preferences.getString(AppСonstants.AUTH_SAVED_TOKEN, ""), AppСonstants.EMAIL_FIELD,
                            preferences.getString(AppСonstants.USER_EMAIL, ""));
                    serverResponseCall.enqueue(new Callback<ServerResponse<DataPupilList>>() {
                        @Override
                        public void onResponse(Call<ServerResponse<DataPupilList>> call, Response<ServerResponse<DataPupilList>> response) {
                            if (response.code() == 200){
                                DataPupilList dataPupilList = response.body().getData();
                                List<PupilGroups> pupilGroups = dataPupilList.getPupilGroups();
                                for (PupilGroups pupil : pupilGroups){

                                    HashMap<String, String> map2 = new HashMap<>();

                                    map2.put("avatar", file.getName());
                                    map2.put("email", pupil.getEmail());
                                    map2.put("full_name", pupil.getFullName());
                                    map2.put("_id", pupil.getId());
                                    map2.put("group_id", pupil.getGroupId());
                                    map2.put("group_name", pupil.getGroup_name());

                                    Call<ServerResponse<PostResult>> call2 = api.updateUserToGroup(AppСonstants.X_API_KEY,
                                            preferences.getString(AppСonstants.AUTH_SAVED_TOKEN, ""), map2);

                                    call2.enqueue(new Callback<ServerResponse<PostResult>>() {
                                        @Override
                                        public void onResponse(Call<ServerResponse<PostResult>> call, Response<ServerResponse<PostResult>> response) {

                                        }

                                        @Override
                                        public void onFailure(Call<ServerResponse<PostResult>> call, Throwable t) {

                                        }
                                    });
                                }
                            } else Log.e("ttttt", String.valueOf(response.raw()));
                        }

                        @Override
                        public void onFailure(Call<ServerResponse<DataPupilList>> call, Throwable t) {

                        }
                    });
                });

                }
        }
    }

    @SuppressLint("Recycle")
    private static String getRealPathFromURI(Context context, Uri contentURI) {

        String result = null;
        Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            if(idx >= 0) {
                result = cursor.getString(idx);
            }
            cursor.close();
        }
        return result;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale", "InflateParams"})
    private void showStatistic(){
        getActivity().runOnUiThread(() -> {
            fab.setImageResource(R.drawable.round_keyboard_arrow_up_24);
            fab.show();
            fab.setOnClickListener(v -> statisticsDialog.show());
            StatisticDao statisticDao = AppСonstants.database.statisticDao();
            List<StatisticEntity> statisticEntities = statisticDao.getAll();
            List<TestResultEntity> testResultEntities = database.testResultDao().getAll();
            //показываем нижнее меню
            statisticsDialog = new BottomSheetDialog(getActivity());
            View bottomDialogView = getActivity().getLayoutInflater().inflate(R.layout.bottom_sheet_statistics, null);
            statisticsDialog.setContentView(bottomDialogView);

            if (statisticEntities.size() > 0 || testResultEntities.size() > 0) {
                //инициализируем переменные
                //общее время прохождения тестов
                int testTimeSeconds = 0;
                //количество вопросов
                int testCount = 0;
                //сумма результатов
                int totalScore = 0;
                //средний балл
                int averageScore = 0;
                int averageTime = 0;
                //все очки/баллы

                resultsCount = testResultEntities.size();

                //список времени прохождения (в секундах)
                List<Integer> times = new ArrayList<>();
                //инициализируем базу данных


                //получаем все необходимые переменные
                for (StatisticEntity object : statisticEntities) {
                    testTimeSeconds += object.getTest_time();
                    testCount++;
                    totalScore += object.getScore();
                    times.add(object.getTest_time());
                }
                for (TestResultEntity entity : testResultEntities){
                    totalScore+=entity.getResult();
                }
                //считаем средний балл
                if (testResultEntities.size() > 0)
                    averageScore = totalScore / testResultEntities.size();

                //считаем среднее время
                if (statisticEntities.size() > 0)
                    averageTime = testTimeSeconds / statisticEntities.size();



                //показываем статистику с результами
                LineChart lineChart = bottomDialogView.findViewById(R.id.results);
                List<Entry> resultsEntries = new ArrayList<>();
                for (int i = 0; i < testResultEntities.size(); i++)
                    resultsEntries.add(new Entry(i, testResultEntities.get(i).getResult()));
                LineDataSet resultDataSet = new LineDataSet(resultsEntries, "Результаты тестов");
                resultDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                resultDataSet.setCubicIntensity(0.2f);
                resultDataSet.setDrawFilled(true);
                resultDataSet.setDrawCircles(false);
                resultDataSet.setLineWidth(1.8f);
                resultDataSet.setCircleRadius(4f);
                resultDataSet.setCircleColor(Color.MAGENTA);
                resultDataSet.setHighLightColor(Color.rgb(244, 117, 117));
                resultDataSet.setColor(Color.MAGENTA);
                resultDataSet.setFillColor(Color.MAGENTA);
                resultDataSet.setFillAlpha(100);
                resultDataSet.setDrawHorizontalHighlightIndicator(false);

                // resultDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                LineData resultData = new LineData(resultDataSet);
                YAxis resultYAxis = lineChart.getAxisRight();
                resultYAxis.setEnabled(false);
                XAxis resultXAxis = lineChart.getXAxis();
                resultXAxis.setEnabled(false);
                lineChart.setTouchEnabled(false);
                lineChart.setDoubleTapToZoomEnabled(false);
                lineChart.getDescription().setEnabled(false);
                lineChart.setData(resultData);
                lineChart.invalidate();

                //Показываем средий балл
                TextView averageScoreText = bottomDialogView.findViewById(R.id.averageScoreText);
                averageScoreText.setText(averageScoreText.getText().toString() + averageScore);

                //показываем статистику с временем прохождения
                BarChart timeChart = bottomDialogView.findViewById(R.id.timeTests);
                List<BarEntry> timeEntries = new ArrayList<>();
                for (int i = 0; i < times.size(); i++) timeEntries.add(new BarEntry(i, times.get(i)));
                BarDataSet timeDataSet = new BarDataSet(timeEntries, "Время прохождения");
                timeDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                BarData timeData = new BarData(timeDataSet);
                YAxis timeYAxis = timeChart.getAxisRight();
                timeYAxis.setEnabled(false);
                XAxis timeXAxis = timeChart.getXAxis();
                timeXAxis.setEnabled(false);
                timeChart.setClickable(false);
                timeChart.setTouchEnabled(false);
                timeChart.getDescription().setEnabled(false);
                timeChart.setData(timeData);
                timeChart.invalidate();

                //Показываем среднее время прохождения тестов
                TextView averageTimeText = bottomDialogView.findViewById(R.id.averageTimeText);
                averageTimeText.setText(averageTimeText.getText().toString() + " " + msToTime(averageTime * 1000));
            } else {
                LinearLayout statsLayout = bottomDialogView.findViewById(R.id.statsLayout);
                statsLayout.setVisibility(View.INVISIBLE);
            }
        });

    }

    private String msToTime(long ms){
        String timeText = "";
        String secondsText = "";
        int hours = (int) (ms / (1000 * 60 * 60));
        int minutes = (int) (ms % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((ms % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        if (hours > 0) timeText = String.valueOf(hours) + ':';
        if (seconds < 10)secondsText = "0" + seconds; else secondsText = String.valueOf(seconds);

        timeText = timeText + minutes + ":" + secondsText;

        return timeText;
    }

}

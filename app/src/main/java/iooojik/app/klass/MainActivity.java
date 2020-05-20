package iooojik.app.klass;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.room.Room;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;

import iooojik.app.klass.api.Api;
import iooojik.app.klass.models.ServerResponse;
import iooojik.app.klass.models.getToken.DataToken;
import iooojik.app.klass.models.profileData.Group;
import iooojik.app.klass.models.profileData.ProfileData;
import iooojik.app.klass.models.profileData.User;
import iooojik.app.klass.models.userData.UserData;
import iooojik.app.klass.room_models.AppDatabase;
import iooojik.app.klass.room_models.profile.ProfileEntity;
import iooojik.app.klass.settings.Settings;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static iooojik.app.klass.AppСonstants.APP_PREFERENCES;
import static iooojik.app.klass.AppСonstants.APP_PREFERENCES_THEME;
import static iooojik.app.klass.AppСonstants.database;

public class MainActivity extends AppCompatActivity {

    public MaterialToolbar materialToolbar;

    // переменная с настройками приложения
    public SharedPreferences preferences;
    //контроллер
    private NavController navController;
    //апи для работы с серверной бд
    private Api api;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppСonstants.database =  Room.databaseBuilder(getApplicationContext(), AppDatabase.class, AppСonstants.LOCAL_DATABASE_NAME)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        // получение настроек
        preferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        // изменение темы
        setTheme(preferences.getInt(APP_PREFERENCES_THEME, R.style.AppThemeLight));
        //получение админского токена
        getAdminToken();

        setContentView(R.layout.activity_main);
        //получение "верхнего" тул-бара
        materialToolbar = findViewById(R.id.bar);
        //название, отображаемое на главное странице
        materialToolbar.setTitle(R.string.main);
        //контроллер, чтобы перемещаться между фрагментами
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //метод проверки на аутентификацию пользователя
        isUserAuth();
        //создание тул-бара
        createToolbar();

        String[] perms = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        //проверяем наличие разрешения на использование геолокации пользователя
        int permissionStatus = PackageManager.PERMISSION_GRANTED;

        for (String perm : perms){
            if (ContextCompat.checkSelfPermission(getApplicationContext(), perm) == PackageManager.PERMISSION_DENIED){
                permissionStatus = PackageManager.PERMISSION_DENIED;
                break;
            }
        }
        //если нет разрешения, то запрашиваем его, иначе показываем погоду
        if (!(permissionStatus == PackageManager.PERMISSION_GRANTED))
            ActivityCompat.requestPermissions(this, perms, 1);


    }

    private void doRetrofit(){
        //базовый метод для работы с retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppСonstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(Api.class);
    }

    private void getAdminToken() {
        doRetrofit();
        //HashMap, в который передаём админские параметры для получения админского токена,
        // который необходим, чтобы зайти пользователю или зарегистрировть его
        HashMap<String, String> map = new HashMap<>();
        map.put("username", AppСonstants.adminEmail);
        map.put("password", AppСonstants.adminPassword);
        //создание запроса
        Call<DataToken> authResponse = api.request_token(AppСonstants.X_API_KEY, map);
        authResponse.enqueue(new Callback<DataToken>() {
            @SuppressLint("CommitPrefEdits")
            @Override
            public void onResponse(Call<DataToken> call, Response<DataToken> response) {
                if (response.code() == 200) {
                    //получаем данные с сервера
                    DataToken dataToken = response.body();
                    //сохраняем админский токен
                    preferences.edit().putString(AppСonstants.STANDART_TOKEN,
                            dataToken.getToken().getToken()).apply();
                }
                else {
                    Log.e("GET TOKEN", String.valueOf(response.raw()));
                }
            }

            @Override
            public void onFailure(Call<DataToken> call, Throwable t) {
                Log.e("GET TOKEN", String.valueOf(t));
            }
        });
    }

    private void isUserAuth(){
        //получаем токен пользователя
        //если токен пустой, то переходим на фрагмент авторизации
        String token = preferences.getString(AppСonstants.AUTH_SAVED_TOKEN, "");

        if(token.isEmpty()){
            navController.navigate(R.id.nav_signIn);
            materialToolbar.setVisibility(View.GONE);
            //убираем боковое меню
            DrawerLayout mDrawerLayout = findViewById(R.id.drawer_layout);
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        } else {
            if (database.profileDao().getAll().size() == 0){
                profileUser();
            }
            //если токен не пустой, то проводим аторизацию, чтобы получить актуальные данные о пользователе
            signIN(preferences.getString(AppСonstants.USER_EMAIL, ""),
                    preferences.getString(AppСonstants.USER_PASSWORD, ""));

        }
    }

    private void createToolbar(){
        //устанавливаем стандартный ActionBar
        setSupportActionBar(materialToolbar);
        //инициализируем NavigationView
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // определение "домашнего" фрагмента и установка навигации
        AppBarConfiguration mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_profile).setDrawerLayout(drawer).build();
        // получение nav-контроллера
        NavigationUI.setupWithNavController(materialToolbar, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    private void signIN(String uEmail, String uPassword){
        //авторизация пользователя
        doRetrofit();
        HashMap<String, String> uCredi = new HashMap<>();
        uCredi.put("username", uEmail);
        uCredi.put("password", uPassword);
        //выполняем запрос
        Call<ServerResponse<UserData>> authResponse = api.UserLogin(uCredi);
        authResponse.enqueue(new Callback<ServerResponse<UserData>>() {
            @SuppressLint("CommitPrefEdits")
            @Override
            public void onResponse(Call<ServerResponse<UserData>> call, Response<ServerResponse<UserData>> response) {
                if (response.code() == 200) {
                    //получаем данные с сервера
                    ServerResponse<UserData> dataAuth = response.body();
                    UserData result = dataAuth.getData();
                    preferences.edit().putString(AppСonstants.USER_ID, result.getId()).apply();
                    //сохраняем пользовательские данные
                    //токен
                    preferences.edit().putString(AppСonstants.AUTH_SAVED_TOKEN, dataAuth.getToken()).apply();
                    //пароль, что проводить необходимые операции
                    preferences.edit().putString(AppСonstants.USER_PASSWORD, uPassword).apply();
                    //email
                    preferences.edit().putString(AppСonstants.USER_EMAIL, result.getEmail()).apply();
                }
                else {
                    Log.e("Sign In", String.valueOf(response.raw()));
                }
            }

            @Override
            public void onFailure(Call<ServerResponse<UserData>> call, Throwable t) {
                Log.e("Sign In", String.valueOf(t));
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //обработка нажатий в верхнем меню
        int id = item.getItemId();
        switch (id){
            case R.id.action_save:
            case R.id.action_read_qr:
            case R.id.action_notif:
            case R.id.action_translate:
                return false;
        }
        return false;
    }

    private void internetCheck(){
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (!isNetworkAvailable()){
                    Toast.makeText(getApplicationContext(),
                            "Нет подключения к интернету!", Toast.LENGTH_SHORT).show();
                }
                handler.postDelayed(this, 5000);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        internetCheck();
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    private void profileUser(){
        Call<ServerResponse<ProfileData>> call = api.getUserDetail(AppСonstants.X_API_KEY,
                preferences.getString(AppСonstants.AUTH_SAVED_TOKEN, ""),
                Integer.parseInt(preferences.getString(AppСonstants.USER_ID, "")));
        call.enqueue(new Callback<ServerResponse<ProfileData>>() {
            @Override
            public void onResponse(Call<ServerResponse<ProfileData>> call, Response<ServerResponse<ProfileData>> response) {
                if (response.code() == 200){
                    ProfileData profileData = response.body().getData();
                    User user = profileData.getUser();
                    Group group = user.getGroup().get(user.getGroup().size() - 1);

                    ProfileEntity newUser = new ProfileEntity();
                    newUser.setAvatar(user.getAvatar());
                    newUser.setFull_name(user.getFullName());
                    newUser.setProfile_type(group.getName().toLowerCase());

                    database.profileDao().insert(newUser);
                    //переходим на "главный" фрагмент
                    navController.navigate(R.id.nav_profile);
                }
            }

            @Override
            public void onFailure(Call<ServerResponse<ProfileData>> call, Throwable t) {

            }
        });
    }


}

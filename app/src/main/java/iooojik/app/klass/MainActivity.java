package iooojik.app.klass;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import java.util.HashMap;

import iooojik.app.klass.models.ServerResponse;
import iooojik.app.klass.models.getToken.DataToken;
import iooojik.app.klass.models.userData.UserData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static iooojik.app.klass.AppСonstants.APP_PREFERENCES;
import static iooojik.app.klass.AppСonstants.APP_PREFERENCES_THEME;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
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
        // получение настроек
        preferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        // изменение темы
        switch (preferences.getInt(APP_PREFERENCES_THEME, 0)) {
            case 0:
                setTheme(R.style.AppThemeLight); // Стандартная
                break;
            case 1:
                setTheme(R.style.AppThemeDark); // Тёмная
                break;
        }
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
        if(token.isEmpty() || preferences.getString(AppСonstants.USER_EMAIL, "").isEmpty()){
            navController.navigate(R.id.nav_signIn);
            materialToolbar.setVisibility(View.GONE);
            //убираем боковое меню
            DrawerLayout mDrawerLayout = findViewById(R.id.drawer_layout);
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        } else {
            //если токен не пустой, то проводим аторизацию, чтобы получить актуальные данные о пользователе
            signIN(preferences.getString(AppСonstants.USER_EMAIL, ""),
                    preferences.getString(AppСonstants.USER_PASSWORD, ""));
            //переходим на "главный" фрагмент
            navController.navigate(R.id.nav_profile);
        }
    }

    private void createToolbar(){
        //устанавливаем стандартный ActionBar
        setSupportActionBar(materialToolbar);
        //инициализируем боковое меню и NavigationView
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // определение "домашнего" фрагмента и установка навигации
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_profile).setDrawerLayout(drawer).build();
        // получение nav-контроллера
        NavigationUI.setupWithNavController(materialToolbar, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        //переход "вверх"
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
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
                return false;
        }
        return false;
    }
}

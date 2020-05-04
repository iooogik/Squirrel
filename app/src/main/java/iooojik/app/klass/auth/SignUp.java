package iooojik.app.klass.auth;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;

import iooojik.app.klass.AppСonstants;
import iooojik.app.klass.R;
import iooojik.app.klass.api.Api;
import iooojik.app.klass.models.PostResult;
import iooojik.app.klass.models.ServerResponse;
import iooojik.app.klass.models.achievements.AchievementsData;
import iooojik.app.klass.models.achievements.AchievementsToUser;
import iooojik.app.klass.models.authorization.SignUpResult;
import iooojik.app.klass.models.userData.UserData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class SignUp extends Fragment implements View.OnClickListener{
    //регистрация пользователя
    public SignUp() {}

    private View view;
    private String accountType = "";
    private Api api;
    private SharedPreferences preferences;
    private NavController navController;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        //получение настроек и контроллера
        navController = NavHostFragment.findNavController(this);
        preferences = getActivity().getSharedPreferences(AppСonstants.APP_PREFERENCES, Context.MODE_PRIVATE);
        //ещё одна проверка на авторизацию
        String token = preferences.getString(AppСonstants.AUTH_SAVED_TOKEN, "");
        if (!(token.isEmpty())) navController.navigate(R.id.nav_profile);

        //инициализация кнопок
        Button signIn = view.findViewById(R.id.signIn);
        Button signUp = view.findViewById(R.id.sign_up);
        //слушатели на кнопки
        signIn.setOnClickListener(this);
        signUp.setOnClickListener(this);


        //слушатель, чтобы получить тип аккаунта
        RadioButton radioButton1 = view.findViewById(R.id.teacher);
        RadioButton radioButton2 = view.findViewById(R.id.pupil);
        radioButton1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                //учительский профиль
                accountType = "Teacher";
                radioButton2.setChecked(false);
            }
        });

        radioButton2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                //ученический профиль
                accountType = "Pupil";
                radioButton1.setChecked(false);
            }
        });
        return view;
    }

    private void doRetrofit(){
        //базовый метод для работы с retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppСonstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(Api.class);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sign_up:
                //инициализация заполненный полей
                EditText email = view.findViewById(R.id.email);
                EditText password = view.findViewById(R.id.password);
                EditText name = view.findViewById(R.id.name);
                EditText surname = view.findViewById(R.id.surname);

                if (password.getText().toString().trim().length() < 6)
                    Snackbar.make(view, "Минимальная длина пароля - 6 символов", Snackbar.LENGTH_SHORT).show();

                else {

                    doRetrofit();


                    /*
                     * 1. проверяем, не пустые ли поля, если не все поля заполнены,
                     * то выводим сообщение: "Не все поля заполнены"
                     * 2. проводим регистрацию, в случае неудачи выводим сообщение:
                     *  "Что-то пошло не так. Попробуйте снова."
                     */

                    //заполняем переменные значениями из соотвествующих полей
                    String uEmail = email.getText().toString().trim(); //email
                    String uPassword = password.getText().toString().trim(); //password
                    String uFullName = name.getText().toString().trim() + " "
                            + surname.getText().toString().trim(); //full name
                    //собираем логин
                    int id = uEmail.indexOf("@");
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < id; i++) {
                        builder.append(uEmail.charAt(i));
                    } //получение логина
                    String login = builder.toString(); // login

                    HashMap<String, String> map = new HashMap<>();
                    map.put("username", login);
                    map.put("email", uEmail);
                    map.put("password", uPassword);
                    map.put("full_name", uFullName);

                    String group = "[4]";
                    if (accountType.equals("Teacher")) group = "[5]";
                    else group = "[6]";  //id группы (типа аккаунта)

                    //запрос на регистрацию
                    Call<SignUpResult> authResponse = api.userRegistration(AppСonstants.X_API_KEY,
                            preferences.getString(AppСonstants.STANDART_TOKEN, ""),
                            map, group);
                    authResponse.enqueue(new Callback<SignUpResult>() {
                        @SuppressLint("CommitPrefEdits")
                        @Override
                        public void onResponse(Call<SignUpResult> call, Response<SignUpResult> response) {
                            if (response.code() == 200) {
                                SignUpResult dataAuth = response.body();
                                preferences.edit().putString(AppСonstants.USER_LOGIN, login);

                                if (dataAuth.getStatus()) signIN(uEmail, uPassword);
                            } else Log.e("Sign Up", String.valueOf(response.raw()));
                        }

                        @Override
                        public void onFailure(Call<SignUpResult> call, Throwable t) {
                            Log.e("Sign Up", String.valueOf(t));
                        }
                    });
                }
               break;
            case R.id.signIn:
                navController.navigate(R.id.nav_signIn);
        }
    }

    private void signIN(String uEmail, String uPassword){
        //авторизация пользователя
        doRetrofit();

        HashMap<String, String> uCredi = new HashMap<>();
        uCredi.put("username", uEmail);
        uCredi.put("password", uPassword);

        Call<ServerResponse<UserData>> authResponse = api.UserLogin(uCredi);

        authResponse.enqueue(new Callback<ServerResponse<UserData>>() {
            @SuppressLint("CommitPrefEdits")
            @Override
            public void onResponse(Call<ServerResponse<UserData>> call, Response<ServerResponse<UserData>> response) {
                if (response.code() == 200) {
                    //получаем данные с сервера
                    ServerResponse<UserData> dataAuth = response.body();
                    UserData result = dataAuth.getData();

                    //сохраняем пользовательский токен
                    preferences.edit().putString(AppСonstants.AUTH_SAVED_TOKEN, dataAuth.getToken()).apply();
                    preferences.edit().putString(AppСonstants.USER_ID, result.getId()).apply();
                    preferences.edit().putString(AppСonstants.USER_PASSWORD, uPassword).apply();
                    preferences.edit().putString(AppСonstants.USER_EMAIL, result.getEmail()).apply();
                    //получение достижений пользователя
                    getUserAchievements(uEmail);

                    DrawerLayout mDrawerLayout = getActivity().findViewById(R.id.drawer_layout);
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    navController.navigate(R.id.nav_profile);
                    Log.i("Sign In", String.valueOf(dataAuth.getToken()));

                    MaterialToolbar materialToolbar = getActivity().findViewById(R.id.bar);
                    materialToolbar.setVisibility(View.VISIBLE);

                }
                else {
                    Log.e("Sign In", String.valueOf(response.raw()));
                    Snackbar.make(getView(), "Что-то пошло не так. Попробуйте снова.", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse<UserData>> call, Throwable t) {
                Log.e("Sign In", String.valueOf(t));
                Snackbar.make(getView(), "Что-то пошло не так. Попробуйте снова.", Snackbar.LENGTH_LONG).show();
            }
        });

    }

    private void getUserAchievements(String userEmail) {
        Call<ServerResponse<AchievementsData>> call = api.getAchievements(AppСonstants.X_API_KEY,
                "user_email", userEmail);
        call.enqueue(new Callback<ServerResponse<AchievementsData>>() {
            @Override
            public void onResponse(Call<ServerResponse<AchievementsData>> call, Response<ServerResponse<AchievementsData>> response) {
                if (response.code() == 200){
                    AchievementsData data = response.body().getData();
                    if(data.getAchievementsToUsers().size() != 0) {
                        AchievementsToUser achievements = data.getAchievementsToUsers().get(0);
                        preferences.edit().putInt(AppСonstants.USER_COINS, Integer.parseInt(achievements.getCoins())).apply();
                        preferences.edit().putInt(AppСonstants.ACHIEVEMENTS_ID, Integer.parseInt(achievements.getId())).apply();
                    } else {
                        addFirstAchievement();
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

    private void addFirstAchievement() {
        HashMap<String, String> map = new HashMap<>();
        map.put("user_email", preferences.getString(AppСonstants.USER_EMAIL, ""));
        map.put("coins", "0");
        Call<ServerResponse<PostResult>> addAchievement = api.addAchievement(AppСonstants.X_API_KEY,
                preferences.getString(AppСonstants.STANDART_TOKEN, ""), map);

        addAchievement.enqueue(new Callback<ServerResponse<PostResult>>() {
            @Override
            public void onResponse(Call<ServerResponse<PostResult>> call, Response<ServerResponse<PostResult>> response) {
                if (response.code() != 200) Log.e("error", response.raw() +
                        preferences.getString(AppСonstants.USER_EMAIL, ""));
            }

            @Override
            public void onFailure(Call<ServerResponse<PostResult>> call, Throwable t) {
                Log.e("error", String.valueOf(t));
            }
        });
    }

}

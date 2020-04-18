package iooojik.app.klass.auth;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;

import iooojik.app.klass.Api;
import iooojik.app.klass.AppСonstants;
import iooojik.app.klass.Database;
import iooojik.app.klass.R;
import iooojik.app.klass.models.ServerResponse;
import iooojik.app.klass.models.authorization.SignUpResult;
import iooojik.app.klass.models.userData.Data;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class SignUp extends Fragment implements View.OnClickListener{

    public SignUp() {}

    private View view;
    private String accountType = "";
    private Api api;
    private SharedPreferences preferences;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        Button signIn = view.findViewById(R.id.signIn);
        signIn.setOnClickListener(this);
        preferences = getActivity().getSharedPreferences(AppСonstants.APP_PREFERENCES, Context.MODE_PRIVATE);
        Button signUp = view.findViewById(R.id.sign_up);
        signUp.setOnClickListener(this);
        //слушатель, чтобы получить тип аккаунта

        RadioButton radioButton1 = view.findViewById(R.id.teacher);
        RadioButton radioButton2 = view.findViewById(R.id.pupil);
        radioButton1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    accountType = "Teacher";
                    radioButton2.setChecked(false);
                }
            }
        });

        radioButton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    accountType = "Pupil";
                    radioButton1.setChecked(false);
                }
            }
        });
        return view;
    }

    private void doBase(){
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
                EditText email = view.findViewById(R.id.email);
                EditText password = view.findViewById(R.id.password);
                EditText name = view.findViewById(R.id.name);
                EditText surname = view.findViewById(R.id.surname);
                doBase();

                SharedPreferences pref = getActivity().getSharedPreferences(AppСonstants.APP_PREFERENCES, Context.MODE_PRIVATE);


                /**
                 * 1. проверяем, не пустые ли поля, если не все поля заполнены, то выводим сообщение: "Не все поля заполнены"
                 * 2. проводим регистрацию, в случае неудачи выводим сообщение: "Что-то пошло не так. Попробуйте снова."
                 */

                String uEmail = email.getText().toString().trim(); //email
                String uPassword = password.getText().toString().trim(); //password
                String uFullName = name.getText().toString().trim() + " "
                        + surname.getText().toString().trim(); //full name

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
                Call<SignUpResult> authResponse = api.userRegistration(AppСonstants.X_API_KEY,
                                pref.getString(AppСonstants.STANDART_TOKEN, ""),
                                map, group);

                String finalGroup = group;
                authResponse.enqueue(new Callback<SignUpResult>() {
                    @SuppressLint("CommitPrefEdits")
                    @Override
                    public void onResponse(Call<SignUpResult> call, Response<SignUpResult> response) {
                        if (response.code() == 200) {
                            SignUpResult dataAuth = response.body();
                            preferences.edit().putString(AppСonstants.USER_LOGIN, login);
                            String type = "";
                            if (finalGroup.equals("[5]")) type = "teacher";
                            else type = "pupil";

                            if (dataAuth.getStatus()) signIN(uEmail, uPassword, type);
                        } else Log.e("Sign Up", String.valueOf(response.raw()));
                    }

                    @Override
                    public void onFailure(Call<SignUpResult> call, Throwable t) {
                        Log.e("Sign Up", String.valueOf(t));
                    }
                });

               break;
            case R.id.signIn:
                NavController navController = NavHostFragment.findNavController(this);
                navController.navigate(R.id.nav_signIn);
        }
    }

    private void signIN(String uEmail, String uPassword, String type){

        NavController navController = NavHostFragment.findNavController(this);

        doBase();

        HashMap<String, String> uCredi = new HashMap<>();
        uCredi.put("username", uEmail);
        uCredi.put("password", uPassword);

        Call<ServerResponse<Data>> authResponse = api.UserLogin(uCredi);

        authResponse.enqueue(new Callback<ServerResponse<Data>>() {
            @SuppressLint("CommitPrefEdits")
            @Override
            public void onResponse(Call<ServerResponse<Data>> call, Response<ServerResponse<Data>> response) {
                if (response.code() == 200) {
                    //получаем данные с сервера
                    ServerResponse<Data> dataAuth = response.body();
                    Data result = dataAuth.getData();

                    //сохраняем пользовательский токен
                    preferences.edit().putString(AppСonstants.AUTH_SAVED_TOKEN, dataAuth.getToken()).apply();
                    preferences.edit().putString(AppСonstants.USER_ID, result.getId()).apply();
                    preferences.edit().putString(AppСonstants.USER_PASSWORD, uPassword).apply();
                    preferences.edit().putString(AppСonstants.USER_EMAIL, result.getEmail()).apply();
                    //сохраняем данные в бд
                    Database mDBHelper = new Database(getContext());
                    SQLiteDatabase mDb;
                    mDBHelper = new Database(getContext());
                    mDBHelper.openDataBase();
                    mDBHelper.updateDataBase();

                    mDb = mDBHelper.getWritableDatabase();

                    ContentValues cv = new ContentValues();
                    cv.put("email", result.getEmail());
                    cv.put("username", result.getUsername());
                    cv.put("full_name", result.getFullName());
                    cv.put("id", result.getId());
                    cv.put("type", type);

                    mDb.update("Profile", cv, "_id=0", null);

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
            public void onFailure(Call<ServerResponse<Data>> call, Throwable t) {
                Log.e("Sign In", String.valueOf(t));
                Snackbar.make(getView(), "Что-то пошло не так. Попробуйте снова.", Snackbar.LENGTH_LONG).show();
            }
        });

    }
}

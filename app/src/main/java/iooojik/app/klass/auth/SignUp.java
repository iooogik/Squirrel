package iooojik.app.klass.auth;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;

import iooojik.app.klass.Api;
import iooojik.app.klass.AppСonstants;
import iooojik.app.klass.Database;
import iooojik.app.klass.R;
import iooojik.app.klass.models.DataAuth;
import iooojik.app.klass.models.ServerResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class SignUp extends Fragment implements View.OnClickListener{

    public SignUp() {}

    private View view;

    private String accountType = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        Button signIn = view.findViewById(R.id.login);
        signIn.setOnClickListener(this);

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

        //поле email с слушателем, чтобы после изменения поля показывать пароль (аналогично для последующих полей)
        EditText email = view.findViewById(R.id.email);
        TextInputLayout password = view.findViewById(R.id.text_input_pass3);
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count != 0){
                    password.setVisibility(View.VISIBLE);
                } else {
                    password.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        TextInputLayout textInputLayout = view.findViewById(R.id.text_input_pass4);
        EditText editPass = view.findViewById(R.id.password);
        editPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count != 0){
                    textInputLayout.setVisibility(View.VISIBLE);
                } else {
                    textInputLayout.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        EditText name = view.findViewById(R.id.name);
        TextInputLayout textInputLayout2 = view.findViewById(R.id.text_input_pass5);
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count != 0){
                    textInputLayout2.setVisibility(View.VISIBLE);
                } else {
                    textInputLayout2.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login:
                EditText email = view.findViewById(R.id.email);
                EditText password = view.findViewById(R.id.password);
                EditText name = view.findViewById(R.id.name);
                EditText surname = view.findViewById(R.id.surname);
                Toast.makeText(getContext(), AppСonstants.STANDART_TOKEN, Toast.LENGTH_LONG).show();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(AppСonstants.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                Api api = retrofit.create(Api.class);

                SharedPreferences pref = getActivity().getSharedPreferences(AppСonstants.APP_PREFERENCES, Context.MODE_PRIVATE);


                /**
                 * 1. проверяем, не пустые ли поля, если не все поля заполнены, то выводим сообщение: "Не все поля заполнены"
                 * 2. проводим регистрацию, в случае неудачи выводим сообщение: "Что-то пошло не так. Попробуйте снова."
                 */

                String uEmail = email.getText().toString();
                String uPassword = password.getText().toString();
                String uFullName = name.getText().toString() + " " + surname.getText().toString();
                StringBuilder userName = new StringBuilder();

                int id = uEmail.indexOf("@");
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < id; i++) {
                    builder.append(uEmail.charAt(i));
                }

                String login = builder.toString();

                HashMap<String, String> map = new HashMap<>();
                map.put("username", login);
                map.put("email", uEmail);
                map.put("password", uPassword);
                map.put("full_name", uFullName);

                Call<ServerResponse<SignUpResult>> authResponse = api.UserRegistration(
                        AppСonstants.X_API_KEY, AppСonstants.STANDART_TOKEN, map);

                authResponse.enqueue(new Callback<ServerResponse<SignUpResult>>() {
                    @Override
                    public void onResponse(Call<ServerResponse<SignUpResult>> call, Response<ServerResponse<SignUpResult>> response) {
                        if (response.code() == 200) {
                            ServerResponse<SignUpResult> dataAuth = response.body();
                            if (dataAuth.getStatus()) {
                                signIN(uEmail, uPassword, accountType.toString());
                            }
                        } else Log.e("Sign Up", String.valueOf(response.raw()));
                    }

                    @Override
                    public void onFailure(Call<ServerResponse<SignUpResult>> call, Throwable t) {
                        Log.e("Sign Up", String.valueOf(t));
                    }
                });



               break;
        }
    }

    private void signIN(String uEmail, String uPassword, String type){

        NavController navController = NavHostFragment.findNavController(this);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppСonstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Api api = retrofit.create(Api.class);

        HashMap<String, String> uCredi = new HashMap<>();
        uCredi.put("username", uEmail);
        uCredi.put("password", uPassword);

        SharedPreferences preferences = getActivity().
                getSharedPreferences(AppСonstants.APP_PREFERENCES, Context.MODE_PRIVATE);

        Call<ServerResponse<DataAuth>> authResponse = api.UserLogin(uCredi);

        authResponse.enqueue(new Callback<ServerResponse<DataAuth>>() {
            @SuppressLint("CommitPrefEdits")
            @Override
            public void onResponse(Call<ServerResponse<DataAuth>> call, Response<ServerResponse<DataAuth>> response) {
                if (response.code() == 200) {
                    //получаем данные с сервера
                    ServerResponse<DataAuth> dataAuth = response.body();
                    DataAuth result = dataAuth.getData();
                    //сохраняем пользовательский токен
                    preferences.edit().putString(AppСonstants.AUTH_SAVED_TOKEN, dataAuth.getToken()).apply();
                    preferences.edit().putString(AppСonstants.password, uPassword).apply();
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
                    cv.put("type", type.toLowerCase());

                    mDb.update("Profile", cv, "_id=0", null);

                    DrawerLayout mDrawerLayout = getActivity().findViewById(R.id.drawer_layout);
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    navController.navigate(R.id.nav_profile);
                    Log.i("Sign In", String.valueOf(dataAuth.getToken()));

                    BottomAppBar bottomAppBar = getActivity().findViewById(R.id.bar);
                    bottomAppBar.setVisibility(View.VISIBLE);

                }
                else {
                    Log.e("Sign In", String.valueOf(response.raw()));
                    Snackbar.make(getView(), "Что-то пошло не так. Попробуйте снова.", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse<DataAuth>> call, Throwable t) {
                Log.e("Sign In", String.valueOf(t));
                Snackbar.make(getView(), "Что-то пошло не так. Попробуйте снова.", Snackbar.LENGTH_LONG).show();
            }
        });

    }
}

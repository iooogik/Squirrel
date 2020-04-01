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
import android.widget.EditText;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

public class SignIn extends Fragment implements View.OnClickListener {

    public SignIn() {}

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        //кнопка входа
        Button signIn = view.findViewById(R.id.login);
        signIn.setOnClickListener(this);
        //кнопка перехода на регистрационную форму
        Button reg = view.findViewById(R.id.registr);
        reg.setOnClickListener(this);
        //скрываем fab
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.hide();
        //слушатель для EditText (EditText для ввода пароля
        // будет появляться после изменения EditText с email-ом)
        EditText email = view.findViewById(R.id.email);
        TextInputLayout password = view.findViewById(R.id.text_input_pass);
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

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login:
                /**
                 * Обработка нажатия кнопки "Войти"
                 * 1. проверяем, не пустые ли поля с email и password
                 * 2. выполняем авторизацию, если она не удалась, то вызываем Snackbar c сообщением "Что-то пошло не так. Попробуйте снова."
                 * если авторизация прошла успешно, то переходим на главный фрагмент, показываем нижний toolbar и разблокируем шторку
                 */
                EditText email = view.findViewById(R.id.email);
                EditText password = view.findViewById(R.id.password);

                NavController navController = NavHostFragment.findNavController(this);
                String uEmail = email.getText().toString();
                String uPassword = password.getText().toString();

                if (!(uEmail.isEmpty() && uPassword.isEmpty())) {
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
                                cv.put("type", "pupil");
                                cv.put("id", result.getId());

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

                break;
            case R.id.registr:
                //переход на регистрационную форму
                NavController navController2 = NavHostFragment.findNavController(this);
                navController2.navigate(R.id.nav_signUp);
                break;
        }
    }
}

package iooojik.app.klass.settings;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.HashMap;

import iooojik.app.klass.AppСonstants;
import iooojik.app.klass.Database;
import iooojik.app.klass.MainActivity;
import iooojik.app.klass.R;
import iooojik.app.klass.api.Api;
import iooojik.app.klass.api.TranslateApi;
import iooojik.app.klass.api.WeatherApi;
import iooojik.app.klass.models.PostResult;
import iooojik.app.klass.models.ServerResponse;
import iooojik.app.klass.models.translation.TranslationResponse;
import iooojik.app.klass.models.weather.Main;
import iooojik.app.klass.models.weather.Weather;
import iooojik.app.klass.models.weather.WeatherData;
import iooojik.app.klass.models.weather.Wind;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static iooojik.app.klass.AppСonstants.APP_PREFERENCES_SHOW_BOOK_MATERIALS;
import static iooojik.app.klass.AppСonstants.APP_PREFERENCES_THEME;
import static iooojik.app.klass.AppСonstants.PICK_IMAGE_AVATAR;

public class Settings extends Fragment implements View.OnClickListener{

    public Settings() {}

    private View view;
    private PackageInfo packageInfo;
    private SharedPreferences preferences;
    private Context context;
    private Api api;
    private Database mDBHelper;
    private SQLiteDatabase mDb;
    private BottomSheetDialog weather;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        FloatingActionButton floatingActionButton = getActivity().findViewById(R.id.fab);
        floatingActionButton.hide();
        context = getContext();
        //получаем настройки
        preferences = getActivity().getSharedPreferences(AppСonstants.APP_PREFERENCES, Context.MODE_PRIVATE);
        //получаем packageInfo, чтобы узнать версию установленного приложения
        try {
            packageInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Button deleteTests = view.findViewById(R.id.delete_tests);
        deleteTests.setOnClickListener(this);

        Button deleteNotes = view.findViewById(R.id.delete_notes);
        deleteNotes.setOnClickListener(this);

        Button showBottomWeather = view.findViewById(R.id.showWeather);
        showBottomWeather.setOnClickListener(this);

        TextView policy = view.findViewById(R.id.policy);
        policy.setOnClickListener(this);

        getActivity().runOnUiThread(this::load);

        return view;
    }

    private void load(){
        //установка тем
        setDarkTheme();
        //"чек" для того, чтобы убрать справочные материалы из заметок
        setShowBookMaterials();
        //установка текущей версии
        setCurrentVersion();
        deAuth();
        contacts();
        showGroupID();
        showWeather();
        mDBHelper = new Database(getContext());
        mDBHelper.openDataBase();
        mDBHelper.updateDataBase();
        setHasOptionsMenu(true);

    }

    private void setDarkTheme() {
        Switch darkTheme = view.findViewById(R.id.darkTheme);

        if (preferences.contains(APP_PREFERENCES_THEME)) {
            // Получаем число из настроек
            int val = preferences.getInt(APP_PREFERENCES_THEME, 0);

            if(val == 1){
                darkTheme.setChecked(true);
            } else if (val == 0){
                darkTheme.setChecked(false);
            }
        }

        Intent intent = new Intent(getContext(), MainActivity.class);

        darkTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                preferences.edit().putInt(APP_PREFERENCES_THEME, 1).apply();
            }else {
                preferences.edit().putInt(APP_PREFERENCES_THEME, 0).apply();
            }
            startActivity(intent);
        });
    }

    private void setShowBookMaterials() {
        //убираем справочные материалы из заметок
        Switch show_book_mat = view.findViewById(R.id.book_items);

        if (preferences.contains(APP_PREFERENCES_SHOW_BOOK_MATERIALS)) {
            // Получаем число из настроек
            int val = preferences.getInt(APP_PREFERENCES_SHOW_BOOK_MATERIALS, 0);

            if(val == 1){
                show_book_mat.setChecked(true);
            } else if (val == 0){
                show_book_mat.setChecked(false);
            }
        }

        show_book_mat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                SharedPreferences.Editor SettingsEditor = preferences.edit();
                SettingsEditor.putInt(APP_PREFERENCES_SHOW_BOOK_MATERIALS, 1);
                SettingsEditor.apply();
            } else {
                SharedPreferences.Editor SettingsEditor = preferences.edit();
                SettingsEditor.putInt(APP_PREFERENCES_SHOW_BOOK_MATERIALS, 0);
                SettingsEditor.apply();
            }
        });

    }

    private void setCurrentVersion(){
        //установка версии
        TextView version = view.findViewById(R.id.version);
        version.setText(String.format("%s%s", version.getText() + " ", packageInfo.versionName));
    }

    private void deAuth(){
        Button exit = view.findViewById(R.id.exit);
        exit.setOnClickListener(v -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());

            builder.setTitle("Важное сообщение!");
            builder.setMessage("При выходе все ваши заметки будут удалены, результаты тестов сброшены." +
                    "Вы действительно хотите выйти?");

            builder.setPositiveButton("Выйти", (dialog, which) -> {
                preferences.edit().clear().apply();

                startActivity(new Intent(getContext(), MainActivity.class));
            });

            builder.setNegativeButton("Нет", (dialog, which) -> dialog.cancel());

            builder.create().show();
        });

    }

    private void contacts(){
        ImageView telegram = view.findViewById(R.id.telegram);
        ImageView gmail = view.findViewById(R.id.gmail);
        ImageView discord = view.findViewById(R.id.discord);
        ImageView vk = view.findViewById(R.id.vk);
        ImageView instagram = view.findViewById(R.id.instagram);

        telegram.setOnClickListener(this);
        gmail.setOnClickListener(this);
        discord.setOnClickListener(this);
        vk.setOnClickListener(this);
        instagram.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.telegram:
                Uri address = Uri.parse("https://t.me/iooojik");
                Intent openLink = new Intent(Intent.ACTION_VIEW, address);
                startActivity(openLink);
                break;
            case R.id.gmail:
                ClipboardManager clipboard = (ClipboardManager)
                        requireContext().
                                getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("", "iooogikdev@gmail.com");
                assert clipboard != null;
                clipboard.setPrimaryClip(clip);
                Snackbar.make(view, "Адрес электронной почты был скопирован в буфер обмена.",
                        Snackbar.LENGTH_LONG).show();
                break;
            case R.id.discord:
                ClipboardManager clipboardDiscord = (ClipboardManager)
                        requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipDiscord = ClipData.newPlainText("", "Стасян#6249");
                clipboardDiscord.setPrimaryClip(clipDiscord);
                Snackbar.make(view, "Тег дискорда был скопирован в буфер обмена.",
                        Snackbar.LENGTH_LONG).show();
                break;
            case R.id.vk:
                Uri addressVK = Uri.parse("https://vk.com/iooojikdev");
                Intent openVk = new Intent(Intent.ACTION_VIEW, addressVK);
                startActivity(openVk);
                break;
            case R.id.instagram:
                Uri addressInst = Uri.parse("https://www.instagram.com/iooojik/?r=nametag");
                Intent openInst = new Intent(Intent.ACTION_VIEW, addressInst);
                startActivity(openInst);
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
            case R.id.delete_tests:
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
                builder.setMessage("Вы действительно хотите удалить все тесты? \n" +
                        "При очистке будут удалены и тесты, и результаты!");

                builder.setNegativeButton("Нет", (dialog, which) -> dialog.cancel());
                builder.setPositiveButton("Удалить", (dialog, which) -> {
                    mDb = mDBHelper.getWritableDatabase();
                    mDb.execSQL("DELETE FROM Tests");
                });
                builder.create().show();
                break;
            case R.id.showWeather:
                weather.show();
                break;
            case R.id.delete_notes:
                MaterialAlertDialogBuilder builder2 = new MaterialAlertDialogBuilder(getContext());
                builder2.setMessage("Вы действительно хотите удалить все заметки? \n" +
                        "При очистке будут удалены и обычные заметки, и справочники!");

                builder2.setNegativeButton("Нет", (dialog, which) -> dialog.cancel());
                builder2.setPositiveButton("Удалить", (dialog, which) -> {
                    mDb = mDBHelper.getWritableDatabase();
                    mDb.execSQL("DELETE FROM Notes");
                });
                builder2.create().show();
                break;
            case R.id.policy:
                NavController navHostFragment = NavHostFragment.findNavController(this);
                navHostFragment.navigate(R.id.nav_policy);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("Recycle")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_AVATAR) {
            if (data != null) {
                Uri selectedImage = data.getData();
                Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                        null, null, null);
                cursor.moveToFirst();
                File file = new File(getRealPathFromURI(context, selectedImage));

                if (file.getAbsoluteFile() != null) {
                    doRetrofit();

                    RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);

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


                    map.put("Avatar", fileReqBody);

                    //MultipartBody.Part part = MultipartBody.Part.createFormData("Avatar",
                           // preferences.getString(AppСonstants.USER_EMAIL, "avatar"), fileReqBody);

                    //RequestBody description = RequestBody.create(MediaType.parse("text/plain"), "Avatar");

                    Call<ServerResponse<PostResult>> postCall = api.userUpdateAvatar(AppСonstants.X_API_KEY,
                            preferences.getString(AppСonstants.AUTH_SAVED_TOKEN, ""), map);

                    postCall.enqueue(new Callback<ServerResponse<PostResult>>() {
                        @Override
                        public void onResponse(Call<ServerResponse<PostResult>> call,
                                               Response<ServerResponse<PostResult>> response) {

                            if (response.code() == 200) {
                                ImageView avatar = view.findViewById(R.id.avatar);
                                avatar.setImageURI(selectedImage);
                            } else
                                Log.e("UPDATE AVATAR", response.raw() + " " + file.getName());
                        }

                        @Override
                        public void onFailure(Call<ServerResponse<PostResult>> call, Throwable t) {
                            Log.e("UPDATE AVATAR", String.valueOf(t));
                        }
                    });
                }
            }
        }
    }

    private void doRetrofit(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppСonstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(Api.class);
    }

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

    private void showGroupID(){
        Switch showID = view.findViewById(R.id.showGroupID);

        if (preferences.getInt(AppСonstants.SHOW_GROUP_ID, 0) == 1) showID.setChecked(true);

        showID.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) preferences.edit().putInt(AppСonstants.SHOW_GROUP_ID, 1).apply();
            else preferences.edit().putInt(AppСonstants.SHOW_GROUP_ID, 0).apply();
        });

    }

    @SuppressLint("InflateParams")
    private void showWeather(){

        //переключатель
        Switch show = view.findViewById(R.id.showWeatherSwitchPerDay);
        Switch showAlways = view.findViewById(R.id.showWeatherSwitchAlways);
        if (preferences.getInt(AppСonstants.SHOW_WEATHER_NOTIF, 1) == 1){
            show.setChecked(true);
            showAlways.setChecked(false);
        } else if (preferences.getInt(AppСonstants.SHOW_WEATHER_NOTIF, 1) == 0
        && preferences.getInt(AppСonstants.SHOW_WEATHER_NOTIF_ALWAYS, 0) == 0){
            show.setChecked(false);
            showAlways.setChecked(false);
        } else if (preferences.getInt(AppСonstants.SHOW_WEATHER_NOTIF, 1) == 0 &&
        preferences.getInt(AppСonstants.SHOW_WEATHER_NOTIF_ALWAYS, 0) == 1){
            show.setChecked(false);
            showAlways.setChecked(true);
        }

        show.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    showAlways.setChecked(false);
                    preferences.edit().putInt(AppСonstants.SHOW_WEATHER_NOTIF_ALWAYS, 0).apply();
                    preferences.edit().putInt(AppСonstants.SHOW_WEATHER_NOTIF, 1).apply();
                }else {
                    preferences.edit().putInt(AppСonstants.SHOW_WEATHER_NOTIF, 0).apply();
                }
            }
        });

        showAlways.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    show.setChecked(false);
                    preferences.edit().putInt(AppСonstants.SHOW_WEATHER_NOTIF_ALWAYS, 1).apply();
                    preferences.edit().putInt(AppСonstants.SHOW_WEATHER_NOTIF, 0).apply();
                }else {
                    preferences.edit().putInt(AppСonstants.SHOW_WEATHER_NOTIF_ALWAYS, 0).apply();
                }
            }
        });

        //получение погоды
        weather = new BottomSheetDialog(getActivity());
        View bottomSheet = getActivity().getLayoutInflater().inflate(R.layout.bottom_sheet_weather, null);

        //координаты
        String lat = preferences.getString(AppСonstants.USER_LAT, "");
        String lon = preferences.getString(AppСonstants.USER_LON, "");

        WeatherApi weatherApi;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppСonstants.WEATHER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        weatherApi = retrofit.create(WeatherApi.class);

        //запрос
        Call<WeatherData> getWeatherCall = weatherApi.getWeather(lat, lon, AppСonstants.WEATHER_API_KEY);

        getWeatherCall.enqueue(new Callback<WeatherData>() {
            @Override
            public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {

                if (response.code() == 200) {
                    setWeatherInfo(bottomSheet, response.body());
                } else Log.e("GETTING WEATHER", String.valueOf(response.raw()));
            }

            @Override
            public void onFailure(Call<WeatherData> call, Throwable t) {
                Log.e("GETTING WEATHER", String.valueOf(t));
            }
        });

        weather.setContentView(bottomSheet);
    }

    @SuppressLint("DefaultLocale")
    private void setWeatherInfo(View bottomSheet, WeatherData weatherData) {
        TextView town = bottomSheet.findViewById(R.id.town); //город
        TextView condition = bottomSheet.findViewById(R.id.condition); //условия
        TextView pressure = bottomSheet.findViewById(R.id.pressure); //давление
        TextView humidity = bottomSheet.findViewById(R.id.humidity); //влажность
        TextView windInfo = bottomSheet.findViewById(R.id.wind); //ветер
        TextView temp = bottomSheet.findViewById(R.id.temperature); //температура

        //устанавливаем информацию
        Main mainInfo = weatherData.getMain();
        temp.setText(String.format("%s%s", String.valueOf(Math.round(mainInfo.getTemp() - 273)), temp.getText().toString()));
        pressure.setText(String.format("%s мм рт. ст.", String.valueOf(Math.round(mainInfo.getPressure() / 1.33))));
        humidity.setText(String.format("%d%%", mainInfo.getHumidity()));
        Wind wind = weatherData.getWind();
        windInfo.setText(String.format("%s м/с", String.valueOf(wind.getSpeed())));

        Weather weather = weatherData.getWeather().get(0);
        //переводим погодные условия и показываем перевод
        TranslateApi translateApi;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppСonstants.YANDEX_TRANSLATE_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        translateApi = retrofit.create(TranslateApi.class);

        Call<TranslationResponse> translationResponseCall = translateApi.translate(AppСonstants.YANDEX_TRANSLATE_API_KEY,
                weatherData.getName() + "," + weather.getDescription(), "en-ru","plain");

        translationResponseCall.enqueue(new Callback<TranslationResponse>() {
            @Override
            public void onResponse(Call<TranslationResponse> call, Response<TranslationResponse> response) {
                if (response.code() == 200){
                    String[] translations = response.body().getText().get(0).split(",");
                    town.setText(translations[0]);
                    condition.setText(translations[1]);
                }
            }

            @Override
            public void onFailure(Call<TranslationResponse> call, Throwable t) {

            }
        });

        String conditionIcon = weather.getIcon();
        ImageView conditionImage = bottomSheet.findViewById(R.id.weather_image);
        //сопоставляем погоду и иконки
        switch (conditionIcon){
            case "01d":
                conditionImage.setImageResource(R.drawable.x01d_clear_sky);
                break;
            case "01n":
                conditionImage.setImageResource(R.drawable.x01n_clear_sky);
                break;
            case "02d":
                conditionImage.setImageResource(R.drawable.x02d_few_clouds);
                break;
            case "02n":
                conditionImage.setImageResource(R.drawable.x02n_few_clouds);
                break;
            case "03d":
                conditionImage.setImageResource(R.drawable.x03d_scattered_clouds);
                break;
            case "03n":
                conditionImage.setImageResource(R.drawable.x03n_scattered_clouds);
                break;
            case "04d":
                conditionImage.setImageResource(R.drawable.x04d_broken_clouds);
                break;
            case "04n":
                conditionImage.setImageResource(R.drawable.x04n_broken_clouds);
                break;
            case "09d":
                conditionImage.setImageResource(R.drawable.x09d_shower_rain);
                break;
            case "09n":
                conditionImage.setImageResource(R.drawable.x09n_shower_rain);
                break;
            case "10d":
                conditionImage.setImageResource(R.drawable.x10d_rain);
                break;
            case "10n":
                conditionImage.setImageResource(R.drawable.x10n_rain);
                break;
            case "11d":
                conditionImage.setImageResource(R.drawable.x11d_thunderstorm);
                break;
            case "11n":
                conditionImage.setImageResource(R.drawable.x11n_thunderstorm);
                break;
            case "13d":
                conditionImage.setImageResource(R.drawable.x13d_snow);
                break;
            case "13n":
                conditionImage.setImageResource(R.drawable.x13n_snow);
                break;
            case "50d":
                conditionImage.setImageResource(R.drawable.x50d_mist);
                break;
            case "50n":
                conditionImage.setImageResource(R.drawable.x50n_mist);
                break;
        }

    }
}

package iooojik.app.klass.settings;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import iooojik.app.klass.AppСonstants;
import iooojik.app.klass.Database;
import iooojik.app.klass.MainActivity;
import iooojik.app.klass.R;
import iooojik.app.klass.api.TranslateApi;
import iooojik.app.klass.api.WeatherApi;
import iooojik.app.klass.models.translation.TranslationResponse;
import iooojik.app.klass.models.weather.Main;
import iooojik.app.klass.models.weather.Weather;
import iooojik.app.klass.models.weather.WeatherData;
import iooojik.app.klass.models.weather.Wind;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static iooojik.app.klass.AppСonstants.APP_PREFERENCES_THEME;

public class Settings extends Fragment implements View.OnClickListener{

    public Settings() {}

    private View view;
    private PackageInfo packageInfo;
    private SharedPreferences preferences;
    private Database mDBHelper;
    private SQLiteDatabase mDb;
    private BottomSheetDialog weather;
    private Switch darkTheme, show, showID;
    private Button deleteTests, deleteNotes, showBottomWeather;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        initViews();
        showWeather();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        synchronized (this) {
            //потоки обновления данных
            new Thread(this::setDarkTheme).start();
            new Thread(this::setCurrentVersion).start();
            new Thread(this::deAuth).start();
            new Thread(this::contacts).start();
            new Thread(this::showGroupID).start();
        }

    }

    private void initViews(){
        //инициализация вьюшек и получение настроек и необходимых переменных
        FloatingActionButton floatingActionButton = getActivity().findViewById(R.id.fab);
        floatingActionButton.hide();
        //получаем настройки
        preferences = getActivity().getSharedPreferences(AppСonstants.APP_PREFERENCES, Context.MODE_PRIVATE);
        //получаем packageInfo, чтобы узнать версию установленного приложения
        try {
            packageInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        deleteTests = view.findViewById(R.id.delete_tests);
        deleteTests.setOnClickListener(this);

        deleteNotes = view.findViewById(R.id.delete_notes);
        deleteNotes.setOnClickListener(this);

        showBottomWeather = view.findViewById(R.id.showWeather);
        showBottomWeather.setOnClickListener(this);

        TextView policy = view.findViewById(R.id.policy);
        policy.setOnClickListener(this);

        mDBHelper = new Database(getContext());
        mDBHelper.openDataBase();
        mDBHelper.updateDataBase();
    }

    private void setDarkTheme() {
        //переключатель, отвечающий за изменение темы приложения
        darkTheme = view.findViewById(R.id.darkTheme);

        if (preferences.contains(APP_PREFERENCES_THEME)) {
            // Получаем число из настроек
            int val = preferences.getInt(APP_PREFERENCES_THEME, R.style.AppThemeLight);

            if(val == R.style.AppThemeDark){
                darkTheme.setChecked(true);
            } else if (val == R.style.AppThemeLight){
                darkTheme.setChecked(false);
            }
        }

        darkTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
            View header = navigationView.getHeaderView(0);
            TextView name = header.findViewById(R.id.textView);
            MaterialToolbar toolbar = getActivity().findViewById(R.id.bar);
            LinearLayout mainLayout = view.findViewById(R.id.mainLayout);
            TextView version = view.findViewById(R.id.version);

            LinearLayout layout = weather.findViewById(R.id.layout);
            TextView town = weather.findViewById(R.id.town);
            TextView condition = weather.findViewById(R.id.condition);
            TextView pressure = weather.findViewById(R.id.pressure);
            TextView humidity = weather.findViewById(R.id.humidity);
            TextView windInfo = weather.findViewById(R.id.wind);
            TextView temp = weather.findViewById(R.id.temperature);


            if (isChecked){
                getActivity().runOnUiThread(() -> preferences.edit().putInt(APP_PREFERENCES_THEME, R.style.AppThemeDark).apply());
                getActivity().setTheme(preferences.getInt(APP_PREFERENCES_THEME, R.style.AppThemeLight));

                darkTheme.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                show.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                showID.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));

                deleteTests.setTextColor(ContextCompat.getColor(getContext(), R.color.color_primary_text));
                deleteNotes.setTextColor(ContextCompat.getColor(getContext(), R.color.color_primary_text));
                showBottomWeather.setTextColor(ContextCompat.getColor(getContext(), R.color.color_primary_text));

                version.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));

                mainLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorBackground_dark));

                navigationView.setItemTextColor(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorWhite)));
                navigationView.setItemIconTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorWhite)));
                toolbar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.darkBackground));
                name.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                navigationView.setBackground(getActivity().getDrawable(R.drawable.nav_view_background));


                town.setTextColor(ContextCompat.getColor(getContext(), R.color.colorBackground_light));
                condition.setTextColor(ContextCompat.getColor(getContext(), R.color.colorBackground_light));
                pressure.setTextColor(ContextCompat.getColor(getContext(), R.color.colorBackground_light));
                humidity.setTextColor(ContextCompat.getColor(getContext(), R.color.colorBackground_light));
                windInfo.setTextColor(ContextCompat.getColor(getContext(), R.color.colorBackground_light));
                temp.setTextColor(ContextCompat.getColor(getContext(), R.color.colorBackground_light));
                layout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorBackground_dark));

            }else {

                getActivity().runOnUiThread(() -> preferences.edit().putInt(APP_PREFERENCES_THEME, R.style.AppThemeLight).apply());
                getActivity().setTheme(preferences.getInt(APP_PREFERENCES_THEME, R.style.AppThemeLight));

                darkTheme.setTextColor(ContextCompat.getColor(getContext(), R.color.color_primary_text));
                show.setTextColor(ContextCompat.getColor(getContext(), R.color.color_primary_text));
                showID.setTextColor(ContextCompat.getColor(getContext(), R.color.color_primary_text));

                deleteTests.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                deleteNotes.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                showBottomWeather.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));

                mainLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorBackground_light));

                version.setTextColor(ContextCompat.getColor(getContext(), R.color.color_primary_text));

                navigationView.setItemTextColor(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.color_primary_text)));
                navigationView.setItemIconTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.color_primary_text)));
                navigationView.setItemTextColor(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.color_primary_text)));
                navigationView.setItemIconTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.color_primary_text)));
                toolbar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorBackground_light));
                name.setTextColor(ContextCompat.getColor(getContext(), R.color.color_primary_text));
                navigationView.setBackgroundResource(R.drawable.nav_view_background);

                town.setTextColor(ContextCompat.getColor(getContext(), R.color.color_primary_text));
                condition.setTextColor(ContextCompat.getColor(getContext(), R.color.color_primary_text));
                pressure.setTextColor(ContextCompat.getColor(getContext(), R.color.color_primary_text));
                humidity.setTextColor(ContextCompat.getColor(getContext(), R.color.color_primary_text));
                windInfo.setTextColor(ContextCompat.getColor(getContext(), R.color.color_primary_text));
                temp.setTextColor(ContextCompat.getColor(getContext(), R.color.color_primary_text));

                layout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorBackground_light));

            }

        });

    }

    private void setCurrentVersion(){
        //получение версии
        TextView version = view.findViewById(R.id.version);
        version.setText(String.format("%s%s", version.getText() + " ", packageInfo.versionName));
    }

    private void deAuth(){
        //кнопка деавторизации
        Button exit = view.findViewById(R.id.exit);
        exit.setOnClickListener(v -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());

            builder.setTitle("Важное сообщение!");
            builder.setMessage("При выходе все ваши заметки будут удалены." +
                    "Вы действительно хотите выйти?");

            builder.setPositiveButton("Выйти", (dialog, which) -> {
                preferences.edit().clear().apply();
                mDb = mDBHelper.getWritableDatabase();
                mDb.execSQL("DELETE FROM " + AppСonstants.TABLE_TESTS);
                mDb.execSQL("DELETE FROM " + AppСonstants.TABLE_NOTES);
                mDb.execSQL("DELETE FROM " + AppСonstants.TABLE_TODO_NAME);

                startActivity(new Intent(getContext(), MainActivity.class));
            });

            builder.setNegativeButton("Нет", (dialog, which) -> dialog.cancel());

            builder.create().show();
        });

    }

    private void contacts(){
        //список контактов
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
            case R.id.delete_tests:
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
                builder.setMessage("Вы действительно хотите удалить все тесты? \n" +
                        "При очистке будут удалены и тесты, и результаты!");

                builder.setNegativeButton("Нет", (dialog, which) -> dialog.cancel());
                builder.setPositiveButton("Удалить", (dialog, which) -> {
                    mDb = mDBHelper.getWritableDatabase();
                    mDb.execSQL("DELETE FROM " + AppСonstants.TABLE_TESTS);
                    mDb.execSQL("DELETE FROM " + AppСonstants.TABLE_FILES_TO_QUESTIONS);
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
                    mDb.execSQL("DELETE FROM " + AppСonstants.TABLE_NOTES);
                });
                builder2.create().show();
                break;
            case R.id.policy:
                NavController navHostFragment = NavHostFragment.findNavController(this);
                navHostFragment.navigate(R.id.nav_policy);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    private void showGroupID(){
        showID = view.findViewById(R.id.showGroupID);

        if (preferences.getInt(AppСonstants.SHOW_GROUP_ID, 0) == 1) showID.setChecked(true);

        showID.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) preferences.edit().putInt(AppСonstants.SHOW_GROUP_ID, 1).apply();
            else preferences.edit().putInt(AppСonstants.SHOW_GROUP_ID, 0).apply();
        });

    }

    @SuppressLint("InflateParams")
    private void showWeather(){

        //переключатель
        show = view.findViewById(R.id.showWeatherSwitchPerDay);

        if (preferences.getInt(AppСonstants.SHOW_WEATHER_NOTIF, 0) == 1) show.setChecked(true);


        show.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                preferences.edit().putInt(AppСonstants.SHOW_WEATHER_NOTIF, 1).apply();
            }else {
                preferences.edit().putInt(AppСonstants.SHOW_WEATHER_NOTIF, 0).apply();
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

package iooogik.app.modelling;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    // названия настроек
    public static final String APP_PREFERENCES = "Settings";
    // тема приложения
    public static final String APP_PREFERENCES_THEME = "Theme";
    // показывать ли доп. материалы в заметках
    public static final String APP_PREFERENCES_SHOW_BOOK_MATERIALS = "Show Book Materials";
    // переменная для определия темы
    public static SharedPreferences Settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // получение настроек
        Settings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        // изменение темы

        if (Settings.contains(APP_PREFERENCES_THEME)) {

            switch (Settings.getInt(APP_PREFERENCES_THEME, 0)) {
                case 0:
                    setTheme(R.style.AppThemeLight); // Стандартная
                    break;
                case 1:
                    setTheme(R.style.AppThemeDark); // Тёмная
                    break;
                case 2:
                    setTheme(R.style.AppThemeRed); // Красная
                    break;
                case 3:
                    setTheme(R.style.AppThemeBlue); // Синяя
                    break;
                case 4:
                    setTheme(R.style.AppThemeYellow); // Жёлтая
                    break;

            }
        }

        setContentView(R.layout.activity_main);
        // создание тул-бара
        createToolbar();
    }



    private void createToolbar(){

        BottomAppBar bottomAppBar = findViewById(R.id.bar);
        setSupportActionBar(bottomAppBar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // определение "домашнего" фрагмента
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home)
                .setDrawerLayout(drawer)
                .build();
        // получение nav-контроллера
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        // кнопка "назад" в тул-баре
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}

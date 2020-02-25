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

    // имя настройки
    public static final String APP_PREFERENCES = "Settings";
    public static final String APP_PREFERENCES_THEME = "Theme";
    public static final String APP_PREFERENCES_SHOW_BOOK_MATERIALS = "Theme";
    public static int theme;
    public static SharedPreferences Settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Settings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        if (Settings.contains(APP_PREFERENCES_THEME)) {
            // Получаем число из настроек
            theme = Settings.getInt(APP_PREFERENCES_THEME, 0);

            if(theme == 1){
                setTheme(R.style.AppThemeDark);
            } else if (theme == 0){
                setTheme(R.style.AppThemeLight);
            }
        }

        setContentView(R.layout.activity_main);

        createToolbar();
    }



    private void createToolbar(){

        BottomAppBar bottomAppBar = findViewById(R.id.bar);
        setSupportActionBar(bottomAppBar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);

        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    @Override
    public void onBackPressed() {}
}

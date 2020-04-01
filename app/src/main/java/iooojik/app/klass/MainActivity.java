package iooojik.app.klass;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;

import static iooojik.app.klass.AppСonstants.APP_PREFERENCES;
import static iooojik.app.klass.AppСonstants.APP_PREFERENCES_THEME;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;


    // переменная с настройками приложения
    public SharedPreferences Settings;
    //контроллер
    private NavController navController;
    //packageInfo, чтобы получать текущую версию приложения
    private PackageInfo packageInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // получение настроек
        Settings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        // изменение темы
        switch (Settings.getInt(APP_PREFERENCES_THEME, 0)) {
            case 0:
                setTheme(R.style.AppThemeLight); // Стандартная
                break;
            case 1:
                setTheme(R.style.AppThemeDark); // Тёмная
                break;
        }

        setContentView(R.layout.activity_main);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //метод проверки на аутентификацию пользователя
        //проверка акутальной версии приложения
        //needUpdate();

        isUserAuth();
        createToolbar();

    }

    private void isUserAuth(){
        BottomAppBar bottomAppBar = findViewById(R.id.bar);

        //получаем токен пользователя
        String token = Settings.getString(AppСonstants.AUTH_SAVED_TOKEN, "");

        if(token.isEmpty()){
            navController.navigate(R.id.nav_signIn);
            bottomAppBar.setVisibility(View.GONE);
            //убираем шторку
            DrawerLayout mDrawerLayout = findViewById(R.id.drawer_layout);
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        } else {
            navController.navigate(R.id.nav_profile);
        }
    }

    private void createToolbar(){
        //нижний тул-бар
        BottomAppBar bottomAppBar = findViewById(R.id.bar);
        setSupportActionBar(bottomAppBar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // определение "домашнего" фрагмента
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_profile)
                .setDrawerLayout(drawer).build();
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

    private void needUpdate(){
        //проверяем текущую версию приложения, получив из бд актуальную и сравнив с установленной
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        //текущая версия
        String currentVersion = packageInfo.versionName;

    }

    private void showUpdateDialog(String currVersion) {
        //метод показывание всплывающего окна с просьбой обновить приложение
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);

        View view = getLayoutInflater().inflate(R.layout.req_update_dialog, null, false);

        TextView installedVersion = view.findViewById(R.id.installedVersion);
        installedVersion.setText(String.format("%s%s", installedVersion.getText(), packageInfo.versionName));

        TextView currentVersion = view.findViewById(R.id.currentVersion);
        currentVersion.setText(String.format("%s%s", currentVersion.getText(), currVersion));




        builder.setPositiveButton("Обновить", (dialog, which) -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(AppСonstants.url));
            startActivity(browserIntent);
        });

        builder.setNegativeButton("Обновить позже", (dialog, which) -> dialog.cancel());

        builder.setView(view);
        builder.create().show();

    }

    @Override
    public void onBackPressed() {}
}

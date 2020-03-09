package iooojik.app.modelling;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseDatabase database;
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
            case 2:
                setTheme(R.style.AppThemeRed); // Красная
                break;
            case 3:
                setTheme(R.style.AppThemeBlue); // Синяя
                break;
            case 4:
                setTheme(R.style.AppThemeYellow); // Жёлтая
                break;


            case 5:
                setTheme(R.style.AppThemeRedDark); // Красная тёмная
                break;
            case 6:
                setTheme(R.style.AppThemeBlueDark); // Синяя тёмная
                break;
            case 7:
                setTheme(R.style.AppThemeYellowDark); // Жёлтая тёмная
                break;

        }
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        setContentView(R.layout.activity_main);
        // создание тул-бара
        createToolbar();
        database = FirebaseDatabase.getInstance();
        needUpdate();
    }

    private void createToolbar(){
        //нижний тул-бар
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

    private void needUpdate(){

        DatabaseReference databaseReference = database.getReference();
        databaseReference.child("Current Version").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String version = null;
                version = String.valueOf(dataSnapshot.getValue(String.class));
                try {
                    packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                String currentVersion = packageInfo.versionName;
                if((!currentVersion.equals(version) || !version.equals(currentVersion)) && version != null){
                    showUpdateDialog(version);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showUpdateDialog(String currVersion) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);

        View view = getLayoutInflater().inflate(R.layout.req_update_dialog, null, false);

        TextView installedVersion = view.findViewById(R.id.installedVersion);
        installedVersion.setText(String.format("%s%s", installedVersion.getText(), packageInfo.versionName));

        TextView currentVersion = view.findViewById(R.id.currentVersion);
        currentVersion.setText(String.format("%s%s", currentVersion.getText(), currVersion));

        DatabaseReference databaseReference = database.getReference();
        databaseReference.child("Update List").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String changes;
                changes = String.valueOf(dataSnapshot.getValue(String.class));
                setUPDList(changes, view);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        builder.setPositiveButton("Обновить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=iooogik.app.modelling"));
                startActivity(browserIntent);
            }
        });

        builder.setNegativeButton("Обновить позже", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.setView(view);
        builder.create().show();

    }

    private View setUPDList(String changes, View view){
        TextView changesList = view.findViewById(R.id.changeList);
        changesList.setText(changes);
        return view;
    }

}

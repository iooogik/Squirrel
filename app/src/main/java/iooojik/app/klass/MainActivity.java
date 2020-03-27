package iooojik.app.klass;

import android.Manifest;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    // название настроек
    public static final String APP_PREFERENCES = "Settings";
    // тема приложения
    public static final String APP_PREFERENCES_THEME = "Theme";
    // показывать ли доп. материалы в заметках
    public static final String APP_PREFERENCES_SHOW_BOOK_MATERIALS = "Show Book Materials";
    //зарегистрирован ли пользователь
    public static final String APP_PREFERENCES_IS_AUTH = "is User Passed Auth";
    // переменная с настройками приложения
    public SharedPreferences Settings;
    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private NavController navController;

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
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        setContentView(R.layout.activity_main);
        isUserAuth();
        database = FirebaseDatabase.getInstance();
        //проверка акутальной версии приложения
        needUpdate();
        createToolbar();
        //запрос на разрешение использования камеры
        int permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (!(permissionStatus == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 1);
        }
    }

    private void isUserAuth(){
        // создание тул-бара
        //нижний тул-бар
        BottomAppBar bottomAppBar = findViewById(R.id.bar);
        setSupportActionBar(bottomAppBar);

        database = FirebaseDatabase.getInstance();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user == null){
            navController.navigate(R.id.nav_signIn);
            bottomAppBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        isUserAuth();
    }

    private void createToolbar(){
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        // определение "домашнего" фрагмента
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_profile).setDrawerLayout(drawer)
                .build();
        // получение nav-контроллера

        NavigationView navigationView = findViewById(R.id.nav_view);
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
        //метод показывание всплывающего окна с просьбой обновить приложение
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

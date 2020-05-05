package iooojik.app.klass.sport;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;

import iooojik.app.klass.AppСonstants;
import iooojik.app.klass.R;
import iooojik.app.klass.api.Api;
import iooojik.app.klass.models.PostResult;
import iooojik.app.klass.models.ServerResponse;
import iooojik.app.klass.models.achievements.AchievementsData;
import iooojik.app.klass.models.achievements.AchievementsToUser;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Sport extends Fragment implements OnMapReadyCallback, View.OnClickListener {

    public Sport() {}

    private View view;
    private GoogleMap map;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location startLocation;
    private float distance = 0;
    private SharedPreferences preferences;
    private Api api;
    private BottomSheetDialog bottomSheetDialog;
    private FloatingActionButton fab;
    private TextView speedText, distanceText, coins;
    private boolean running;
    private Handler chrono = new Handler();
    private int seconds = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sport, container, false);
        preferences = getActivity().getSharedPreferences(AppСonstants.APP_PREFERENCES, Context.MODE_PRIVATE);
        fab = getActivity().findViewById(R.id.fab);
        fab.show();
        fab.setOnClickListener(this);
        fab.setImageResource(R.drawable.round_keyboard_arrow_up_24);
        enableBottomSheet();
        prepareMap();
        setLocationManager();
        startTimer();
        return view;
    }

    @SuppressLint("DefaultLocale")
    private void startTimer(){
        running = true;
        seconds = 0;
        chrono.post(new Runnable() {
            @Override
            public void run() {
                if(running) {
                    seconds++;
                    chrono.postDelayed(this, 1000);
                }

            }
        });
    }

    @SuppressLint("InflateParams")
    private void enableBottomSheet() {
        bottomSheetDialog = new BottomSheetDialog(getActivity());
        View bottomSheet = getActivity().getLayoutInflater().inflate(R.layout.bottom_sheet_sport, null);
        Button stop = bottomSheet.findViewById(R.id.stop);
        stop.setOnClickListener(this);

        speedText = bottomSheet.findViewById(R.id.speed);
        distanceText = bottomSheet.findViewById(R.id.distance);
        coins = bottomSheet.findViewById(R.id.coins);

        bottomSheetDialog.setOnCancelListener(dialog -> fab.show());
        bottomSheetDialog.setContentView(bottomSheet);
    }

    private void prepareMap(){
        SupportMapFragment mapFragment = (SupportMapFragment)
                this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void setLocationManager() {
        //проверяем наличие разрешения на использование геолокации пользователя
        int permissionStatus = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        //если нет разрешения, то запрашиваем его, иначе показываем погоду
        if (!(permissionStatus == PackageManager.PERMISSION_GRANTED)) ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        else {
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            //слушатель, отслеживающий изменение координат пользователя
            locationListener = new LocationListener() {
                @SuppressLint("DefaultLocale")
                @Override
                public void onLocationChanged(Location location) {
                    if (startLocation != null) {
                        map.addPolyline(new PolylineOptions().add(new LatLng(startLocation.getLatitude(), startLocation.getLongitude()),
                                new LatLng(location.getLatitude(), location.getLongitude())).width(25).color(R.color.notCompleted));
                        distance+=startLocation.distanceTo(location);
                        int speed = (int) location.getSpeed();
                        distanceText.setText(String.format("%sкм", String.format("%.3f %n", distance / 1000)));
                        speedText.setText(String.format("%s км/ч", String.valueOf(speed)));
                        coins.setText(String.valueOf(Math.round((distance/1000)/3) * 3));
                        seconds = 0;
                    }
                    startLocation = location;
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    }
                    startLocation = locationManager.getLastKnownLocation(provider);
                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };

            //получаем координаты из GPS или из сети
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10*1000, 50, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10*1000, 50, locationListener);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.setMyLocationEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.stop:
                locationManager.removeUpdates(locationListener);
                //получение койнов в зависимости от пройденного пути
                int coins = 0;
                coins = Math.round((distance/1000)/3) * 3;
                updateCoins(coins);
                bottomSheetDialog.hide();
                break;
            case R.id.fab:
                bottomSheetDialog.show();
                fab.hide();
                break;
        }
    }

    private void updateCoins(int coins){

        doRetrofit();
        //получаем актуальную информацию о койнах
        Call<ServerResponse<AchievementsData>> call = api.getAchievements(AppСonstants.X_API_KEY,
                "user_email", preferences.getString(AppСonstants.USER_EMAIL, ""));

        call.enqueue(new Callback<ServerResponse<AchievementsData>>() {
            @Override
            public void onResponse(Call<ServerResponse<AchievementsData>> call, Response<ServerResponse<AchievementsData>> response) {
                if (response.code() == 200){
                    AchievementsData data = response.body().getData();
                    AchievementsToUser achievements = data.getAchievementsToUsers().get(0);
                    preferences.edit().putInt(AppСonstants.USER_COINS, Integer.parseInt(achievements.getCoins())).apply();
                    preferences.edit().putInt(AppСonstants.ACHIEVEMENTS_ID, Integer.parseInt(achievements.getId())).apply();
                }
                else Log.e("GET ACHIEVEMENTS", String.valueOf(response.raw()));
            }

            @Override
            public void onFailure(Call<ServerResponse<AchievementsData>> call, Throwable t) {
                Log.e("GET ACHIEVEMENTS", String.valueOf(t));
            }
        });

        int newCoins = preferences.getInt(AppСonstants.USER_COINS, 0) + coins;
        if (newCoins != 0) {
            preferences.edit().putInt(AppСonstants.USER_COINS, newCoins).apply();

            HashMap<String, String> changes = new HashMap<>();
            changes.put("user_email", preferences.getString(AppСonstants.USER_EMAIL, ""));
            changes.put("_id", String.valueOf(preferences.getInt(AppСonstants.ACHIEVEMENTS_ID, -1)));
            changes.put("coins", String.valueOf(preferences.getInt(AppСonstants.USER_COINS, 0)));

            Call<ServerResponse<PostResult>> call2 = api.updateAchievement(AppСonstants.X_API_KEY,
                    preferences.getString(AppСonstants.STANDART_TOKEN, ""), changes);

            call2.enqueue(new Callback<ServerResponse<PostResult>>() {
                @Override
                public void onResponse(Call<ServerResponse<PostResult>> call, Response<ServerResponse<PostResult>> response) {
                    if (response.code() != 200)
                        Log.e("ADD ACHIEVEMENT", String.valueOf(response.raw()));
                }

                @Override
                public void onFailure(Call<ServerResponse<PostResult>> call, Throwable t) {
                    Log.e("ADD ACHIEVEMENT", String.valueOf(t));
                }
            });
        }
        Snackbar.make(getView(), "Получено " + coins + " койнов", Snackbar.LENGTH_SHORT).show();
    }

    private void doRetrofit(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppСonstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(Api.class);
    }

}

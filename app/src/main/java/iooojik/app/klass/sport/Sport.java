package iooojik.app.klass.sport;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sport, container, false);
        preferences = getActivity().getSharedPreferences(AppСonstants.APP_PREFERENCES, Context.MODE_PRIVATE);
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.hide();
        Button begin = view.findViewById(R.id.stop);
        begin.setOnClickListener(this);
        prepareMap();
        setLocationManager();
        return view;
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
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    if (startLocation != null) {
                        map.addPolyline(new PolylineOptions().add(new LatLng(startLocation.getLatitude(), startLocation.getLongitude()),
                                new LatLng(location.getLatitude(), location.getLongitude())).width(25).color(R.color.notCompleted));
                        distance+=startLocation.distanceTo(location);
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
            //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.setMyLocationEnabled(true);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.stop:
                locationManager.removeUpdates(locationListener);
                //получение койнов в зависимости от пройденного пути
                int coins = 0;
                coins = Math.round(distance/3) * 3;
                updateCoins(coins);
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

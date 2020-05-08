package iooojik.app.klass.coinsSearch;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
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
import iooojik.app.klass.models.bonusCrate.CratesData;
import iooojik.app.klass.models.caseData.CaseData;
import iooojik.app.klass.models.caseData.CasesToUser;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class CoinsSearch extends Fragment implements OnMapReadyCallback, View.OnClickListener {

    public CoinsSearch() {}

    private GoogleMap map;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location startLocation, caseLocation;
    private float distance = 0;
    private SharedPreferences preferences;
    private Api api;
    private BottomSheetDialog bottomSheetDialog;
    private FloatingActionButton fab;
    private TextView speedText, distanceText, coins;
    private Button addCaseOnMap;
    private String caseID = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sport, container, false);
        preferences = getActivity().getSharedPreferences(AppСonstants.APP_PREFERENCES, Context.MODE_PRIVATE);
        fab = getActivity().findViewById(R.id.fab);
        fab.show();
        fab.setOnClickListener(this);
        fab.setImageResource(R.drawable.round_keyboard_arrow_up_24);
        enableBottomSheet();
        prepareMap();
        setLocationManager();
        loadCase();
        return view;
    }

    @SuppressLint("InflateParams")
    private void enableBottomSheet() {
        bottomSheetDialog = new BottomSheetDialog(getActivity());
        View bottomSheet = getActivity().getLayoutInflater().inflate(R.layout.bottom_sheet_sport, null);
        Button stop = bottomSheet.findViewById(R.id.stop);
        stop.setOnClickListener(this);

        addCaseOnMap = bottomSheet.findViewById(R.id.open_case);
        addCaseOnMap.setOnClickListener(this);

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
        if (!(permissionStatus == PackageManager.PERMISSION_GRANTED)) ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
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
                    }

                    checkCaseLocation(location);

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
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3*1000, 3, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3*1000, 3, locationListener);
        }
    }

    private void checkCaseLocation(Location location) {
        //проверяем был ли найден кейс
        //координаты кейса
        if (caseLocation != null) {
            double caseLat = caseLocation.getLatitude();
            double caseLot = caseLocation.getLongitude();
            //проверяем разницу в координатах
            if (Math.abs(location.getLatitude() - caseLat) < 0.0003 && Math.abs(location.getLongitude() - caseLot) < 0.0003) {
                //показываем Dialog и зачисляем монеты
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
                int coins = (int) getRandomBetweenRange(10, 30);
                builder.setMessage("Поздравляем! Вы нашли кейс! На ваш счёт зачислено " + coins + " койнов");
                builder.setPositiveButton(R.string.ok_ru, (dialog, which) -> dialog.cancel());
                builder.create().show();
                deleteCase();
                doRetrofit();
                HashMap<String, String> changes = new HashMap<>();
                changes.put(AppСonstants.USER_EMAIL_FIELD, preferences.getString(AppСonstants.USER_EMAIL, ""));
                changes.put(AppСonstants.ID_FIELD, String.valueOf(preferences.getInt(AppСonstants.ACHIEVEMENTS_ID, -1)));
                changes.put(AppСonstants.COINS_FIELD, String.valueOf(preferences.getInt(AppСonstants.USER_COINS, 0) + coins));

                Call<ServerResponse<PostResult>> call = api.updateAchievement(AppСonstants.X_API_KEY,
                        preferences.getString(AppСonstants.STANDART_TOKEN, ""), changes);

                call.enqueue(new Callback<ServerResponse<PostResult>>() {
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
                caseLocation = null;
                map.clear();
                addCaseOnMap.setEnabled(true);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.setMyLocationEnabled(true);
        map.setIndoorEnabled(true);
        if (preferences.getInt(AppСonstants.APP_PREFERENCES_THEME, R.style.AppThemeLight) == R.style.AppThemeDark)
            map.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.dark_map_style));
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
            case R.id.open_case:
                if (Integer.valueOf(preferences.getString(AppСonstants.CASES, "-1")) != 0) {
                    int openedCases = 0;
                    if (openedCases == 0) {
                        //получаем рандомные координаты в радиусе 1км и строим маршрут до этих координат
                        double lat = startLocation.getLatitude();
                        double lon = startLocation.getLongitude();

                        double k = getRandomBetweenRange(0, 1);
                        int chance = (int) getRandomBetweenRange(1, 2);
                        if (chance == 2) k *= (-1);
                        lat = lat + (k / 100);

                        k = getRandomBetweenRange(0, 1);
                        chance = (int) getRandomBetweenRange(1, 2);
                        if (chance == 2) k *= (-1);
                        lon = lon + (k / 100);

                        caseLocation = new Location("");
                        caseLocation.setLatitude(lat);
                        caseLocation.setLongitude(lon);
                        addCaseOnMap.setEnabled(false);
                        getActivity().runOnUiThread(() -> {
                            saveCaseLocation();
                            loadCase();
                        });

                        // обработать получение кейса, когда пользователь пришёл за ним (попробовать через поток)

                    } else Snackbar.make(getView(), "Вы уже открыли кейс, сходите за ним! " +
                          "Или нажмите \"Отменить поиск кейса\", чтобы открыть новый!", Snackbar.LENGTH_LONG).show();
              } else Snackbar.make(getView(), "У вас нет кейсов", Snackbar.LENGTH_LONG).show();
                break;
        }
    }

    private void saveCaseLocation(){
        doRetrofit();
        HashMap<String, String> map = new HashMap<>();
        map.put(AppСonstants.USER_EMAIL_FIELD, preferences.getString(AppСonstants.USER_EMAIL, ""));
        map.put(AppСonstants.LAT_FIELD, String.valueOf(caseLocation.getLatitude()));
        map.put(AppСonstants.LOT_FIELD, String.valueOf(caseLocation.getLongitude()));
        Call<ServerResponse<PostResult>> call = api.addCaseCoordinates(AppСonstants.X_API_KEY,
                preferences.getString(AppСonstants.AUTH_SAVED_TOKEN, ""), map);
        call.enqueue(new Callback<ServerResponse<PostResult>>() {
            @Override
            public void onResponse(Call<ServerResponse<PostResult>> call, Response<ServerResponse<PostResult>> response) {

            }

            @Override
            public void onFailure(Call<ServerResponse<PostResult>> call, Throwable t) {

            }
        });

        Call<ServerResponse<CratesData>> getCrates = api.getCrates(AppСonstants.X_API_KEY,
                preferences.getString(AppСonstants.AUTH_SAVED_TOKEN, ""), AppСonstants.USER_EMAIL_FIELD,
                preferences.getString(AppСonstants.USER_EMAIL, ""));
        getCrates.enqueue(new Callback<ServerResponse<CratesData>>() {
            @Override
            public void onResponse(Call<ServerResponse<CratesData>> call1, Response<ServerResponse<CratesData>> response) {
                if (response.code() == 200){
                    CratesData data = response.body().getData();
                    if (data.getBonusCratesToUsers().size() != 0){
                        updateCrateInfo(data.getBonusCratesToUsers().get(0).getId(),
                                data.getBonusCratesToUsers().get(0).getCount());
                    }
                }
            }

            @Override
            public void onFailure(Call<ServerResponse<CratesData>> call1, Throwable t) {

            }
        });
    }

    private void updateCrateInfo(String id, String count) {
        doRetrofit();
        HashMap<String, String> map = new HashMap<>();
        map.put(AppСonstants.ID_FIELD, id);
        map.put(AppСonstants.USER_EMAIL_FIELD, preferences.getString(AppСonstants.USER_EMAIL, ""));
        map.put(AppСonstants.COUNT_FIELD, String.valueOf(Integer.valueOf(count) - 1));
        Call<ServerResponse<PostResult>> call = api.updateCrateInfo(AppСonstants.X_API_KEY,
                preferences.getString(AppСonstants.AUTH_SAVED_TOKEN, ""), map);
        call.enqueue(new Callback<ServerResponse<PostResult>>() {
            @Override
            public void onResponse(Call<ServerResponse<PostResult>> call, Response<ServerResponse<PostResult>> response) {

            }

            @Override
            public void onFailure(Call<ServerResponse<PostResult>> call, Throwable t) {

            }
        });
    }

    private void loadCase(){
        //получаем информацию о местонахождении кейса и показываем его на карте
        doRetrofit();
        Call<ServerResponse<CaseData>> call = api.getCase(AppСonstants.X_API_KEY,
                preferences.getString(AppСonstants.AUTH_SAVED_TOKEN, ""),
                AppСonstants.USER_EMAIL_FIELD,
                preferences.getString(AppСonstants.USER_EMAIL, ""));

        call.enqueue(new Callback<ServerResponse<CaseData>>() {
            @Override
            public void onResponse(Call<ServerResponse<CaseData>> call, Response<ServerResponse<CaseData>> response) {
                if (response.code() == 200){
                    CaseData caseData = response.body().getData();
                    if (caseData.getCasesToUsers().size() != 0) {
                        CasesToUser casesToUsers = caseData.getCasesToUsers().get(0);
                        caseLocation = new Location("");
                        caseLocation.setLatitude(Double.valueOf(casesToUsers.getLatitude()));
                        caseLocation.setLongitude(Double.valueOf(casesToUsers.getLongitude()));
                        caseID = casesToUsers.getId();
                        LatLng latLng = new LatLng(Double.valueOf(casesToUsers.getLatitude()), Double.valueOf(casesToUsers.getLongitude()));

                        map.addMarker(new MarkerOptions().position(latLng).title("Кейс").
                                snippet("Поскорее найдите меня!").icon(BitmapDescriptorFactory.fromResource(R.drawable.small_crate)));
                        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        addCaseOnMap.setEnabled(false);
                    }
                }
            }

            @Override
            public void onFailure(Call<ServerResponse<CaseData>> call, Throwable t) {

            }
        });

    }

    private void deleteCase(){
        //удаление "активного" кейса
        doRetrofit();
        Call<ServerResponse<PostResult>> call = api.removeCase(AppСonstants.X_API_KEY, caseID);
        call.enqueue(new Callback<ServerResponse<PostResult>>() {
            @Override
            public void onResponse(Call<ServerResponse<PostResult>> call, Response<ServerResponse<PostResult>> response) {

            }

            @Override
            public void onFailure(Call<ServerResponse<PostResult>> call, Throwable t) {

            }
        });
    }

    private static double getRandomBetweenRange(double min, double max){
        return (int)(Math.random()*((max-min)+1))+min;
    }

    private void updateCoins(int coins){

        doRetrofit();
        //получаем актуальную информацию о койнах
        Call<ServerResponse<AchievementsData>> call = api.getAchievements(AppСonstants.X_API_KEY,
                AppСonstants.USER_EMAIL_FIELD, preferences.getString(AppСonstants.USER_EMAIL, ""));

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
            changes.put(AppСonstants.USER_EMAIL_FIELD, preferences.getString(AppСonstants.USER_EMAIL, ""));
            changes.put(AppСonstants.ID_FIELD, String.valueOf(preferences.getInt(AppСonstants.ACHIEVEMENTS_ID, -1)));
            changes.put(AppСonstants.COINS_FIELD, String.valueOf(preferences.getInt(AppСonstants.USER_COINS, 0)));

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

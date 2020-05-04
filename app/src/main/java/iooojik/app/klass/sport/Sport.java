package iooojik.app.klass.sport;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
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

import iooojik.app.klass.R;


public class Sport extends Fragment implements OnMapReadyCallback, View.OnClickListener {

    public Sport() {}

    private View view;
    private GoogleMap map;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location startLocation;
    private float distance = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sport, container, false);
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
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
                    0, locationListener);

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,
                    0, locationListener);
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
                break;
        }
    }
}

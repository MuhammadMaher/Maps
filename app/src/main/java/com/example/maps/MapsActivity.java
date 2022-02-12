package com.example.maps;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.maps.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {
    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getCurrentLocation();

    }
// لمعرفة الموقع الخاص باليوزر
    private FusedLocationProviderClient fusedLocationClient;

    void getCurrentLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            Log.i(TAG, "onSuccess: " + location.getAltitude());
                            Log.i(TAG, "onSuccess: " + location.getLongitude());

                            LatLng userLocation= new LatLng(location.getLatitude(),location.getLongitude());
                            mMap.addMarker(new MarkerOptions().position(userLocation).title("My Location"));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation,12));

                        }
                    }
                });

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng cairo= new LatLng(30.1131466,31.3362272);
        mMap.addMarker(new MarkerOptions().position(cairo).title("Cairo"));
       // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(kafrElshikhStaduim,15));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cairo,12));
        // اختيار مكان علي الخريطة
        mMap.setOnMapClickListener(this);
        Geocoder geocoder=new Geocoder(this,new Locale("ar"));
        try {
           List<Address> addressList= geocoder.getFromLocationName("حديقة",5,
                   30.044144, 31.235665,
                   30.044720, 31.235536);
            Log.i(TAG, "onMapReady: "+addressList.size());

            for (Address address:addressList ) {

                Log.i(TAG, "onMapReady: " + address.getAddressLine(0));
                Log.i(TAG, "onMapReady: " + address.getLatitude());
                Log.i(TAG, "onMapReady: " + address.getLongitude());

                LatLng park= new LatLng(address.getLatitude(),address.getLongitude());
                mMap.addMarker(new MarkerOptions().position(park).title(address.getFeatureName()));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(park,12));

            }
        } catch (IOException e) {
            Log.i(TAG, "onMapReady: "+e.getLocalizedMessage());
        }


    }
    String address;
    String place;
    LatLng selectedlatlong=null;
    @Override
    public void onMapClick(@NonNull  LatLng latLng) {
        selectedlatlong=latLng;
         // mMap.clear();
//        mMap.addMarker(new MarkerOptions().position(latLng).title("name position"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));

        // تفاصيل النقطة اللي دوست عليها عن طريق الاحداثيات فقط
        Geocoder geocoder=new Geocoder(this,new Locale("ar"));
        try {
          List<Address> addressList= geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
        //                                                                ///
            address = addressList.get(0).getAddressLine(0);
            place = addressList.get(0).getLocality();

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title(place);

            Marker marker = mMap.addMarker(markerOptions);
            marker.showInfoWindow();

        //                                                           ///
            Log.i(TAG, "onMapClick: "+addressList.get(0).getAddressLine(0));
            Log.i(TAG, "onMapClick: "+addressList.get(0).getAdminArea());
        } catch (IOException e) {
            Log.i(TAG, "onMapClick: "+e.getLocalizedMessage());

        }
    }

    void onSelected(){
        if(selectedlatlong ==null){
            Toast.makeText(this, "please select location", Toast.LENGTH_SHORT).show();
       return;
        }

        Intent intent=new Intent();
        intent.putExtra("latLong",selectedlatlong);
        setResult(RESULT_OK,intent);
        finish();
    }

    // هستقبل في شاشة تانية
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        LatLng latLng=data.getParcelableExtra("latLong");
//    }
}
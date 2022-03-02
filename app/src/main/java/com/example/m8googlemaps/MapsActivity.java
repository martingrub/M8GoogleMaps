package com.example.m8googlemaps;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.m8googlemaps.model.ApiCall;

import com.example.m8googlemaps.model.ModelApi;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.m8googlemaps.databinding.ActivityMapsBinding;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        /*Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.sunrise-sunset.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiCall apiCall = (ApiCall) retrofit.create(ApiCall.class);
        Call<ModelApi> call = apiCall.getData("41.390205", "2.154007");*/
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.flickr.com/services/rest/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiCall apiCall = (ApiCall) retrofit.create(ApiCall.class);
        Call<ModelApi> call = apiCall.getPhotos();
        call.enqueue(new Callback<ModelApi>(){
            @Override
            public void onResponse(Call<ModelApi> call, Response<ModelApi> response) {
                if(response.code()!=200){
                    Log.i("testApi", "checkConnection");
                    return;
                }

                Log.i("testApi", response.body().getStat() + " - " + response.body().getPhotos().getPhoto());
            }

            @Override
            public void onFailure(Call<ModelApi> call, Throwable t) {

            }
        });

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // Add a marker in Sydney and move the camera

                mMap.addMarker(new MarkerOptions().position(latLng).title("New position"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                Log.i("hola", latLng.latitude+" "+latLng.longitude);
                getAddress(latLng.latitude,latLng.longitude);
                double Latitude = latLng.latitude;
                double Longitude = latLng.longitude;
                ApiThread process = new ApiThread(Latitude,Longitude);
                process.execute();

            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                mMap.clear();

                return true;
            }
        });
        enableMyLocation();




    }
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
            }
        } else {
            // Permission to access the location is missing. Show rationale and request permission

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.length > 0 ){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                enableMyLocation();
                Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show();
        }

    }

    public void getAddress(double lat, double lng) {
        try {
            Geocoder geo = new Geocoder(this.getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(lat, lng, 1);
            if (addresses.isEmpty()) {
                Toast.makeText(this, "No s’ha trobat informació", Toast.LENGTH_LONG).show();
            } else {
                if (addresses.size() > 0) {
                    String msg =addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() +", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName();

                    Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                }
            }
        }
        catch(Exception e){
            Toast.makeText(this, "No Location Name Found", Toast.LENGTH_LONG).show();
        }

    }


}

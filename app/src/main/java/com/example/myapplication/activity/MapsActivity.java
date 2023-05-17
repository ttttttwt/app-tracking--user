package com.example.myapplication.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.network.ApiService;
import com.example.myapplication.network.model.ActiveNetwork;
import com.example.myapplication.network.model.LocationNetwork;
import com.example.myapplication.network.model.MessageNetwork;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.myapplication.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.SphericalUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private PolylineOptions polylineOptions = new PolylineOptions();
    private Polyline polyline;
    private ActivityMapsBinding binding;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location currentLocation;
    private LocationRequest locationRequest;
    private boolean hasRunning = false;
    private LatLng start;
    private ArrayList<LatLng> latLngArrayList = new ArrayList<>();

    private double distanceActive = 0;
    float totalTime;
    private long startTime = 0L;
    private float averageSpeed = 0;
    private long elapsedTime = 0L;
    private boolean isTimerRunning = false, stop=false;
    private Handler handler = new Handler();
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            elapsedTime = SystemClock.elapsedRealtime() - startTime;
            int hours = (int) (elapsedTime / 3600000);
            int minutes = (int) ((elapsedTime - hours * 3600000) / 60000);
            int seconds = (int) ((elapsedTime - hours * 3600000 - minutes * 60000) / 1000);
            String time = String.format("%02d:%02d:%02d", hours, minutes, seconds);
            totalTime = hours * 60 + minutes;
            binding.textView9.setText("Time: "+time);
            handler.postDelayed(this, 1000);
        }
    };
    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            if(locationResult == null) {
                return;
            }
            for (Location location: locationResult.getLocations()) {
                Log.d("Map", "result: " + location.getLatitude());
                LatLng newLocatoin = new LatLng(location.getLatitude(), location.getLongitude());
//                mMap.addMarker(new MarkerOptions().position(newLocatoin));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLocatoin, 18));

                polylineOptions.add(newLocatoin);
                latLngArrayList.add(newLocatoin);
                double temp = SphericalUtil.computeDistanceBetween(start, newLocatoin) / 1000.0;
                start = newLocatoin;

                DecimalFormat decimalFormat = new DecimalFormat("#.###");
                decimalFormat.setRoundingMode(RoundingMode.DOWN);
                double roundedTemp = Double.parseDouble(decimalFormat.format(temp));

                BigDecimal distance = new BigDecimal(distanceActive);
                distance = distance.add(BigDecimal.valueOf(roundedTemp));

                float averageSpeed = 0.0f;
                if (totalTime != 0) {
                    averageSpeed = (float) (distance.floatValue() / (totalTime / 60.0));
                }

                distanceActive = distance.floatValue();
                binding.textView10.setText("Speed: " + averageSpeed + " km/h");
                binding.textView.setText("Distance: " + distance.floatValue() + " Km");
                polyline.setPoints(polylineOptions.getPoints());

            }
        }
    };
    private int REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intentLogin = getIntent();
        String used_id = intentLogin.getStringExtra("user_id");
        String userName = intentLogin.getStringExtra("userName");
        String email = intentLogin.getStringExtra("email");
        String distance = intentLogin.getStringExtra("distance");
        String time = intentLogin.getStringExtra("time");
        String speed = intentLogin.getStringExtra("speed");

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getCurrentLocation();


        binding.bottomNavigation.setSelectedItemId(R.id.run);
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int ID = item.getItemId();
            if (ID == R.id.home) {
                Intent intent = new Intent(this, Home.class);
                intent.putExtra("user_id", used_id);
                intent.putExtra("userName", userName);
                intent.putExtra("email", email);
                intent.putExtra("distance", distance);
                intent.putExtra("time", time);
                intent.putExtra("speed", speed);
                startActivity(intent);
                finish();
            } else if (ID == R.id.setting_menu) {
                Intent intent = new Intent(this, Setting.class);
                intent.putExtra("user_id", used_id);
                intent.putExtra("userName", userName);
                intent.putExtra("email", email);
                intent.putExtra("distance", distance);
                intent.putExtra("time", time);
                intent.putExtra("speed", speed);
                startActivity(intent);
                finish();
            } else if (ID == R.id.statistical) {
                Intent intent = new Intent(this, Statistical.class);
                intent.putExtra("user_id", used_id);
                intent.putExtra("userName", userName);
                intent.putExtra("email", email);
                intent.putExtra("distance", distance);
                intent.putExtra("time", time);
                intent.putExtra("speed", speed);
                startActivity(intent);
                finish();
            }
            return true;
        });

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000); // 10 seconds, adjust as needed
        locationRequest.setFastestInterval(3000);


        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isTimerRunning) {
                    setUp();
                    binding.button.setText("Stop");
                    binding.textView.setText("Distance: 0" );
                    binding.textView.setVisibility(View.VISIBLE);
                    binding.textView9.setVisibility(View.VISIBLE);
                    binding.textView10.setVisibility(View.VISIBLE);
                    startTime = SystemClock.elapsedRealtime();
                    handler.postDelayed(timerRunnable, 0);
                    isTimerRunning = true;
                } else {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String currentDate = dateFormat.format(new Date());
                    float time = totalTime;
                    float distance1 = (float) distanceActive;

                    alertSave(used_id, currentDate, time, distance1,averageSpeed).show();



                }
            }
        });
    }

    private void postActive(String id, String date, float time, float distance, float speed) {
        ActiveNetwork activeNetwork = new ActiveNetwork();
        activeNetwork.setDate(date);
        activeNetwork.setUser_id(id);
        activeNetwork.setTime(time);
        activeNetwork.setDistance(distance);
        activeNetwork.setSpeed(speed);

        ApiService.apiService.addActive(activeNetwork).enqueue(new Callback<MessageNetwork>() {
            @Override
            public void onResponse(Call<MessageNetwork> call, Response<MessageNetwork> response) {
                if (response.code() == 201) {
                    String uID = response.body().getId();
                    postLocation( uID, date);
                    alert("Active has save", "Success").show();

                }
            }
            @Override
            public void onFailure(Call<MessageNetwork> call, Throwable t) {
                alert("Error: " + t.getMessage(), "Error").show();
            }
        });
    }

    private void postLocation(String id, String date1) {
        ArrayList<LocationNetwork> locationNetworks = new ArrayList<>();
        for (LatLng a: latLngArrayList) {
            locationNetworks.add(new LocationNetwork(id, (float)a.latitude, (float)a.longitude, date1));
        }
        ApiService.apiService.addLoction(id, locationNetworks).enqueue(new Callback<MessageNetwork>() {
            @Override
            public void onResponse(Call<MessageNetwork> call, Response<MessageNetwork> response) {
                if(response.code() == 201) {
                    Log.d("test", "success");
                }
            }

            @Override
            public void onFailure(Call<MessageNetwork> call, Throwable t) {
                Log.d("test", "Error");
            }
        });
    }

    private AlertDialog.Builder alertSave(String userID, String date, float time, float distance, float speed) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        LayoutInflater inflater	= getLayoutInflater();
        View alertLayout = getLayoutInflater().inflate(R.layout.map_diaglog, null);
        TextView distanceText = (TextView) alertLayout.findViewById(R.id.textView11);
        TextView timeText = (TextView) alertLayout.findViewById(R.id.textView12);
        TextView aSpeed = (TextView) alertLayout.findViewById(R.id.textView14);
        distanceText.setText("Distance: " + distance);
        timeText.setText("Time" + time);
        aSpeed.setText("Speed" + speed);

//        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().f(R.id.map2);
        SupportMapFragment supportMapFragment1 = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map2);

        supportMapFragment1.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                GoogleMap mMap1 = googleMap;
                googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getBaseContext(), R.raw.map_style));
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);

                PolylineOptions polylineOptions1 = new PolylineOptions();
                latLngArrayList.get(0);
                polylineOptions1.color(Color.BLUE);
                polylineOptions1.width(10);

                for (LatLng latLng: latLngArrayList) {
                    polylineOptions1.add(latLng);
                }
                Polyline polyline1 = mMap1.addPolyline(polylineOptions1);
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLngArrayList.get(latLngArrayList.size() - 1))
                        .title("End")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

                mMap1.addMarker(new MarkerOptions().position(latLngArrayList.get(0)).title("Start"));
                mMap1.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngArrayList.get(0), 18));
                mMap1.addMarker(markerOptions);
                latLngArrayList.clear();
            }
        });


        alert.setView(alertLayout);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
//                postActive(userID, date, time, distance, speed);
                handler.removeCallbacks(timerRunnable);
                isTimerRunning = false;
                binding.textView.setVisibility(View.GONE);
                binding.textView9.setVisibility(View.GONE);
                binding.textView10.setVisibility(View.GONE);
                distanceActive = 0;
                averageSpeed = 0;
                stopLocationUpdate();

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                // Remove the SupportMapFragment
                transaction.remove(supportMapFragment1);
                // Commit the transaction
                transaction.commit();

                postActive(userID, date, time, distance, speed);
                binding.button.setText("Run");

            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                dialog.cancel();
            }
        });
        return alert;
    }

    private void setUp() {
        LocationSettingsRequest locationSettingsRequest = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build();
        SettingsClient client = LocationServices.getSettingsClient(this);

        Task<LocationSettingsResponse> locationSettingsResponseTask = client.checkLocationSettings(locationSettingsRequest);
        locationSettingsResponseTask.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                startLocationUpdate();
            }
        });
        locationSettingsResponseTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                ResolvableApiException apiException = (ResolvableApiException) e;
                try {
                    apiException.startResolutionForResult(MapsActivity.this, 1001);
                } catch (IntentSender.SendIntentException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        LocationSettingsRequest locationSettingsRequest = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build();
//        SettingsClient client = LocationServices.getSettingsClient(this);
//
//        Task<LocationSettingsResponse> locationSettingsResponseTask = client.checkLocationSettings(locationSettingsRequest);
//        locationSettingsResponseTask.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
//            @Override
//            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
//                startLocationUpdate();
//            }
//        });
//        locationSettingsResponseTask.addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                ResolvableApiException apiException = (ResolvableApiException) e;
//                try {
//                    apiException.startResolutionForResult(MapsActivity.this, 1001);
//                } catch (IntentSender.SendIntentException ex) {
//                    ex.printStackTrace();
//                }
//            }
//        });
//    }

    private AlertDialog.Builder alert(String msg, String title) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage(msg);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                dialog.cancel();
            }
        });
        return alert;
    }

    @Override
    protected void onStop() {
        super.onStop();
//        stopLocationUpdate();
    }

    private void startLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void stopLocationUpdate() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        mMap.clear();
        polylineOptions.getPoints().clear();
        getCurrentLocation();
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    supportMapFragment.getMapAsync(MapsActivity.this);
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        mMap.setMyLocationEnabled(true);
        // Add a marker in Sydney and move the camera
        start = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        polylineOptions.color(Color.BLUE);
        polylineOptions.width(10);
        polylineOptions.add(start);
        polyline = mMap.addPolyline(polylineOptions);
        mMap.addMarker(new MarkerOptions().position(start).title("Start"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(start, 18));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(getApplicationContext(), "Not Location Permission", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
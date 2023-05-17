package com.example.myapplication.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.ActiveAdapter;
import com.example.myapplication.adapter.SelectListener;
import com.example.myapplication.databinding.ActivityStatisticalBinding;
import com.example.myapplication.network.ApiService;
import com.example.myapplication.network.model.ActiveNetwork;
import com.example.myapplication.network.model.DayOfWeek;
import com.example.myapplication.network.model.LocationNetwork;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Statistical extends AppCompatActivity {
    ActivityStatisticalBinding binding;
    ArrayList barArrayList;
    List<ActiveNetwork> activeNetworks;

    BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStatisticalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        Intent intentLogin = getIntent();
        String used_id = intentLogin.getStringExtra("user_id");
        String userName = intentLogin.getStringExtra("userName");
        String email = intentLogin.getStringExtra("email");
        String distance = intentLogin.getStringExtra("distance");
        String time = intentLogin.getStringExtra("time");
        String speed = intentLogin.getStringExtra("speed");

        controlTab();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(binding.getRoot().getContext(), LinearLayout.VERTICAL);
        binding.recyclerView.addItemDecoration(dividerItemDecoration);

        activeNetworks = new ArrayList<>();
        getActives();

        getBarChartData(used_id);
        barChart = binding.distanceChart;
        barChart.animateXY(3000, 3000);

        String[] month = new String[] {"1", "2", "3", "4", "5", "6", "7"};
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(month));
        xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(0);
        xAxis.setGranularityEnabled(true);

        barChart.getXAxis().setAxisMaximum(month.length + 0.25f);

        barChart.invalidate();


        binding.bottomNavigation.setSelectedItemId(R.id.statistical);
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int ID = item.getItemId();
            if (ID == R.id.run) {
                Intent intent = new Intent(this, MapsActivity.class);
                intent.putExtra("user_id", used_id);
                intent.putExtra("userName", userName );
                intent.putExtra("email", email);
                intent.putExtra("distance", distance);
                intent.putExtra("time", time);
                intent.putExtra("speed", speed);
                startActivity(intent);
                finish();
            } else if (ID == R.id.setting_menu) {
                Intent intent = new Intent(this, Setting.class);
                intent.putExtra("user_id", used_id);
                intent.putExtra("userName", userName );
                intent.putExtra("email", email);
                intent.putExtra("distance", distance);
                intent.putExtra("time", time);
                intent.putExtra("speed", speed);
                startActivity(intent);
                finish();
            } else if (ID == R.id.home) {
                Intent intent = new Intent(this, Home.class);
                intent.putExtra("user_id", used_id);
                intent.putExtra("userName", userName );
                intent.putExtra("email", email);
                intent.putExtra("distance", distance);
                intent.putExtra("time", time);
                intent.putExtra("speed", speed);
                startActivity(intent);
                finish();
            }
            return true;
        });
    }

    private void controlTab() {
        TabHost tabHost = binding.tabHost;
        tabHost.setup();
        TabHost.TabSpec tab1 = tabHost.newTabSpec("History");
        TabHost.TabSpec tab2 = tabHost.newTabSpec("Statistical");

        tab1.setContent(R.id.tab1);
        tab1.setIndicator("History");

        tab2.setContent(R.id.tab2);
        tab2.setIndicator("Statistical");

        tabHost.addTab(tab1);
        tabHost.addTab(tab2);
    }

    private void getActives() {

        ApiService.apiService.getActives("1").enqueue(new Callback<List<ActiveNetwork>>() {
            @Override
            public void onResponse(Call<List<ActiveNetwork>> call, Response<List<ActiveNetwork>> response) {

                if (response.body() != null) {
                    activeNetworks = response.body();
                    binding.recyclerView.setAdapter(new ActiveAdapter(activeNetworks));

                } else {
                    alert("Error code" + response.code(), "Error");
                }
            }

            @Override
            public void onFailure(Call<List<ActiveNetwork>> call, Throwable t) {
                alert("Error" + t.getMessage(), "Error");

            }
        });
    }
    private AlertDialog.Builder alert(String msg, String title) {
        AlertDialog.Builder alert = new AlertDialog.Builder(binding.getRoot().getContext());
        alert.setMessage(msg);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                dialog.cancel();
            }
        });
        return alert;
    }

    private void getBarChartData(String id) {
        barArrayList = new ArrayList<>();
       ApiService.apiService.getDayOfWeek(id).enqueue(new Callback<DayOfWeek>() {
           @Override
           public void onResponse(Call<DayOfWeek> call, Response<DayOfWeek> response) {
                if (response.body() != null) {
                     DayOfWeek dayOfWeek = response.body();
                     barArrayList.add(new BarEntry(1, dayOfWeek.getDay_0()));
                     barArrayList.add(new BarEntry(2, dayOfWeek.getDay_1()));
                     barArrayList.add(new BarEntry(3, dayOfWeek.getDay_2()));
                     barArrayList.add(new BarEntry(4, dayOfWeek.getDay_3()));
                     barArrayList.add(new BarEntry(5, dayOfWeek.getDay_4()));
                     barArrayList.add(new BarEntry(6, dayOfWeek.getDay_5()));
                     barArrayList.add(new BarEntry(7, dayOfWeek.getDay_6()));
                    BarDataSet barDataSet = new BarDataSet(barArrayList, "");
                    BarData barData = new BarData(barDataSet);

                    barChart.setData(barData);
                    barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                    barDataSet.setValueTextColor(Color.BLACK);
                    barDataSet.setValueTextSize(16f);
                }
           }

           @Override
           public void onFailure(Call<DayOfWeek> call, Throwable t) {
               alert("Error" + t.getMessage(), "Error");
           }
       });

       System.out.println(barArrayList);
    }

//    @Override
//    public void onItemClick(ActiveNetwork activeNetwork) {
//        alertShow(activeNetwork.getId());
//    }

//    private AlertDialog.Builder alertShow(String activeID) {
//        AlertDialog.Builder alert = new AlertDialog.Builder(this);
//
//        LayoutInflater inflater	= getLayoutInflater();
//        View alertLayout = getLayoutInflater().inflate(R.layout.map_diaglog, null);
//        TextView distanceText = (TextView) alertLayout.findViewById(R.id.textView11);
//        TextView timeText = (TextView) alertLayout.findViewById(R.id.textView12);
//        TextView aSpeed = (TextView) alertLayout.findViewById(R.id.textView14);
//
//        SupportMapFragment supportMapFragment1 = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map2);
//
//        supportMapFragment1.getMapAsync(new OnMapReadyCallback() {
//            @Override
//            public void onMapReady(@NonNull GoogleMap googleMap) {
//                GoogleMap mMap1 = googleMap;
//                googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getBaseContext(), R.raw.map_style));
//                mMap1.getUiSettings().setZoomControlsEnabled(true);
//                mMap1.getUiSettings().setMyLocationButtonEnabled(true);
//
//                PolylineOptions polylineOptions1 = new PolylineOptions();
//                polylineOptions1.color(Color.BLUE);
//                polylineOptions1.width(10);
//
//                ApiService.apiService.getLocation(activeID).enqueue(new Callback<ArrayList<LocationNetwork>>() {
//                    @Override
//                    public void onResponse(Call<ArrayList<LocationNetwork>> call, Response<ArrayList<LocationNetwork>> response) {
//                        if (response.code() == 200) {
//                            List<LocationNetwork> locationNetworkList = response.body();
//                            for (LocationNetwork locationNetwork: locationNetworkList) {
//                                polylineOptions1.add(new LatLng(locationNetwork.getLatitude(), locationNetwork.getLongitude()));
//                            }
//
//                            Polyline polyline1 = mMap1.addPolyline(polylineOptions1);
//                            MarkerOptions markerOptions = new MarkerOptions()
//                                    .position(new LatLng(locationNetworkList.get(locationNetworkList.size() - 1).getLatitude(), locationNetworkList.get(locationNetworkList.size() - 1).getLongitude())).title("End")
//                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
//
//                            mMap1.addMarker(new MarkerOptions().position(new LatLng(locationNetworkList.get(1).getLatitude(), locationNetworkList.get(1).getLongitude())).title("Start"));
//                            mMap1.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(locationNetworkList.get(1).getLatitude(), locationNetworkList.get(1).getLongitude()), 18));
//                            mMap1.addMarker(markerOptions);
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<ArrayList<LocationNetwork>> call, Throwable t) {
//
//                    }
//                });
//
//
//
//            }
//        });
//
//
//        alert.setView(alertLayout);
//
//        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
////                postActive(userID, date, time, distance, speed);
//
//                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                // Remove the SupportMapFragment
//                transaction.remove(supportMapFragment1);
//                // Commit the transaction
//                transaction.commit();
//
//            }
//        });
//        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                // User clicked OK button
//                dialog.cancel();
//            }
//        });
//        return alert;
//    }
}
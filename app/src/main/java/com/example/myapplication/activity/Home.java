package com.example.myapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivityHomeBinding;
import com.example.myapplication.network.ApiService;
import com.example.myapplication.network.model.DayOfWeek;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Home extends AppCompatActivity {

    ActivityHomeBinding binding;
    float[] dayOfWeekArr = new float[7];


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intentLogin = getIntent();
        String used_id = intentLogin.getStringExtra("user_id");
        String userName = intentLogin.getStringExtra("userName");
        String email = intentLogin.getStringExtra("email");
        String distance = intentLogin.getStringExtra("distance");
        String time = intentLogin.getStringExtra("time");
        String speed = intentLogin.getStringExtra("speed");
        checkDayOfWeek(used_id);

        binding.username.setText("Hello, " + userName);
        binding.email.setText(email);
        binding.distanceText.setText(distance);
        binding.timeText.setText(time);
        binding.speedText.setText(speed);

        binding.bottomNavigation.setSelectedItemId(R.id.home);
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
            } else if (ID == R.id.statistical) {
                Intent intent = new Intent(this, Statistical.class);
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

    private void checkDayOfWeek(String id) {
        ApiService.apiService.getDayOfWeek(id).enqueue(new Callback<DayOfWeek>() {
            @Override
            public void onResponse(Call<DayOfWeek> call, Response<DayOfWeek> response) {
                DayOfWeek dayOfWeek = response.body();
                if (response.code() == 200) {
                    dayOfWeekArr[0] = dayOfWeek.getDay_0();
                    dayOfWeekArr[1] = dayOfWeek.getDay_1();
                    dayOfWeekArr[2] = dayOfWeek.getDay_2();
                    dayOfWeekArr[3] = dayOfWeek.getDay_3();
                    dayOfWeekArr[4] = dayOfWeek.getDay_4();
                    dayOfWeekArr[5] = dayOfWeek.getDay_5();
                    dayOfWeekArr[6] = dayOfWeek.getDay_6();

                    int total = 0;
                    for (int i = 0; i < 7; i++) {
                        if (dayOfWeekArr[i] == 1) {
                            total++;
                        }
                    }

                    binding.textViewDayOfweek.setText(String.valueOf(total));

                    if (dayOfWeekArr[0] == 1) {
                        binding.imageView2.setImageResource(R.drawable.fire);
                    }
                    if (dayOfWeekArr[1] == 1) {
                        binding.imageView3.setImageResource(R.drawable.fire);
                    }
                    if (dayOfWeekArr[2] == 1) {
                        binding.imageView4.setImageResource(R.drawable.fire);
                    }
                    if (dayOfWeekArr[3] == 1) {
                        binding.imageView5.setImageResource(R.drawable.fire);
                    }
                    if (dayOfWeekArr[4] == 1) {
                        binding.imageView6.setImageResource(R.drawable.fire);
                    }
                    if (dayOfWeekArr[5] == 1) {
                        binding.imageView7.setImageResource(R.drawable.fire);
                    }
                    if (dayOfWeekArr[6] == 1) {
                        binding.imageView8.setImageResource(R.drawable.fire);
                    }


                }
            }

            @Override
            public void onFailure(Call<DayOfWeek> call, Throwable t) {
                alert("Error: " + t.getMessage(), "Error").show();
            }
        });
    }

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
}
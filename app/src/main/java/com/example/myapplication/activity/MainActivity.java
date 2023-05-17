package com.example.myapplication.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivityMainBinding;
import com.example.myapplication.network.ApiService;
import com.example.myapplication.network.model.StatisticalNetwork;
import com.example.myapplication.network.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.usernameEditText.setText("hello101");
        binding.passwordEditText.setText("1234");

        binding.loginButton.setOnClickListener(v -> {
            String username = binding.usernameEditText.getText().toString();
            String password = binding.passwordEditText.getText().toString();
//                    System.out.println("Username: " + username + " Password: " + password);
            checkUser(username, password);
        });

        binding.linkToRegisterTextView.setOnClickListener(v -> {
            switchActivitiesToRegister();
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    // function

    private AlertDialog.Builder Alert(String msg, String title) {
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

    private void switchActivitiesToRegister() {
        Intent switchActivityIntent = new Intent(this, RegisterActivity.class);
        startActivity(switchActivityIntent);
    }

    private void switchActivitiesToHome(String userID, String userName, String email, String distance, String time, String speed) {
        Intent switchActivityIntent = new Intent(this, Home.class);
        switchActivityIntent.putExtra("user_id", userID);
        switchActivityIntent.putExtra("userName", userName );
        switchActivityIntent.putExtra("email", email);
        switchActivityIntent.putExtra("distance", distance);
        switchActivityIntent.putExtra("time", time);
        switchActivityIntent.putExtra("speed", speed);
        startActivity(switchActivityIntent);
    }


    private void checkUser(String username, String password) {
        User user = new User(username, password);

        ApiService.apiService.login(user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User userResponse = response.body();
                if (userResponse != null) {
                    setContentView(R.layout.fragment_wating_screen);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getUserData(userResponse.getId());
                        }
                    }, 1000);
                } else {
                    Alert("Username or password is incorrect.", "Error").show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<User> call, Throwable t) {
                Alert("Error: " + t.getMessage(), "Error").show();
            }
        });
    }

    private void getUserData(String usedID) {
        ApiService.apiService.getUser(usedID).enqueue(new Callback<User>() {
            String[] arr = new String[6];
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User userResponse = response.body();
                if (userResponse != null) {
                    arr[0] = userResponse.getId();
                    arr[1] = userResponse.getUsername();
                    arr[2] = userResponse.getEmail();
                    getStatistical(arr);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Alert("Error: " + t.getMessage(), "Error").show();
            }
        });
    }

    private void getStatistical(String[] arr) {
        ApiService.apiService.statistical(arr[0]).enqueue(new Callback<StatisticalNetwork>() {
            @Override
            public void onResponse(Call<StatisticalNetwork> call, Response<StatisticalNetwork> response) {
                StatisticalNetwork statisticalNetwork = response.body();
                if (statisticalNetwork != null) {
                    arr[3] = String.valueOf(statisticalNetwork.getDistance());
                    arr[4] = String.valueOf(statisticalNetwork.getTime());
                    arr[5] = String.valueOf(statisticalNetwork.getSpeed());
                    switchActivitiesToHome(arr[0], arr[1], arr[2], arr[3], arr[4], arr[5]);
                    finish();
                }

            }

            @Override
            public void onFailure(Call<StatisticalNetwork> call, Throwable t) {
                Alert("Error: " + t.getMessage(), "Error").show();

            }
        });

    }
}
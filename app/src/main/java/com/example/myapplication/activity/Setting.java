package com.example.myapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivitySettingBinding;
import com.example.myapplication.network.ApiService;
import com.example.myapplication.network.model.MessageNetwork;
import com.example.myapplication.network.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Setting extends AppCompatActivity {
    ActivitySettingBinding binding;
    String newData;
    String used_id;
    String userName;
    String email;
    String distance;
    String time;
    String speed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intentLogin = getIntent();
        used_id = intentLogin.getStringExtra("user_id");
        userName = intentLogin.getStringExtra("userName");
        email = intentLogin.getStringExtra("email");
        distance = intentLogin.getStringExtra("distance");
        time = intentLogin.getStringExtra("time");
        speed = intentLogin.getStringExtra("speed");

        binding.username.setText("Hello, " + userName);
        binding.email.setText(email);

        binding.textView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertChange("Enter new user name", 1).show();
            }
        });

        binding.textView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertChange("Enter new user name", 2).show();
            }
        });

        binding.textView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertChange("Enter new user name", 3).show();
            }
        });

        binding.textView7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDelete(used_id).show();
            }
        });

        binding.textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(binding.getRoot().getContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        binding.bottomNavigation.setSelectedItemId(R.id.setting_menu);
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

    private AlertDialog.Builder alertDelete(String userID) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Are you sure to delete account");
        alert.setTitle("Delete account");
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ApiService.apiService.deleteUser(userID).enqueue(new Callback<MessageNetwork>() {
                    @Override
                    public void onResponse(Call<MessageNetwork> call, Response<MessageNetwork> response) {
                        MessageNetwork msg = response.body();
                        if (response.code() == 204) {
                            alert("delete account success", "Success").show();
                            Intent intent = new Intent(binding.getRoot().getContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                    @Override
                    public void onFailure(Call<MessageNetwork> call, Throwable t) {
                        alert("Error: " + t.getMessage(), "Error").show();
                    }
                });
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

    private AlertDialog.Builder alertChange(String msg, int type) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        LayoutInflater inflater	= getLayoutInflater();
        View alertLayout = getLayoutInflater().inflate(R.layout.dialog_change_name, null);
        TextView enterTextView = (TextView) alertLayout.findViewById(R.id.Enter);
        EditText editText = (EditText) alertLayout.findViewById(R.id.enterNewText);
        alert.setView(alertLayout);

        if (type == 1) {
            enterTextView.setText("Enter new username here:");
        } else if (type == 2) {
            enterTextView.setText("Enter new password:");
        } else if (type == 3) {
            enterTextView.setText("Enter new email:");
        }

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (type == 1) {
                    newData = editText.getText().toString();
                    changeName(newData);

                } else if (type == 2) {
                    newData = editText.getText().toString();
                    changePassword(newData);

                } else if (type == 3) {
                    newData = editText.getText().toString();
                    changeEmail(newData);
                }
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

    private void changeName(String name) {
        User userTemp = new User();
        userTemp.setId(name);
        ApiService.apiService.updateUser(used_id, userTemp).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User user = response.body();
                if (user != null) {
                    used_id = user.getId();
                    userName = user.getUsername();
                    binding.username.setText(userName);
                    alert("Success", "Change name success").show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                alert("Error", "Change name error").show();
            }
        });
    }

    private void changePassword(String password) {
        User userTemp = new User();
        userTemp.setId(password);
        ApiService.apiService.updateUser(used_id, userTemp).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User user = response.body();
                if (user != null) {
                    used_id = user.getId();
                    userName = user.getUsername();
                    alert("Success", "Change password success").show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                alert("Error", "Change password error").show();
            }
        });
    }

    private void changeEmail(String emailTemp) {
        User userTemp = new User();
        userTemp.setEmail(emailTemp);
        ApiService.apiService.updateUser(used_id, userTemp).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User user = response.body();
                if (user != null) {
                    used_id = user.getId();
                    email = user.getEmail();
                    binding.email.setText(email);
                    alert("Success", "Change email success").show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                alert("Error", "Change email error").show();
            }
        });
    }
}
package com.example.myapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.myapplication.databinding.RegisterBinding;
import com.example.myapplication.network.ApiService;
import com.example.myapplication.network.model.MessageNetwork;
import com.example.myapplication.network.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    RegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = RegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.loginTextView.setOnClickListener(v -> {
            switchActivitiesToLogin();
        });

        binding.registerButton.setOnClickListener(v -> {
            String username = binding.usernameEditText.getText().toString();
            String password = binding.passwordEditText.getText().toString();
            String confirmPassword = binding.secoundPasswordEditText.getText().toString();
            String email = binding.emailEditText.getText().toString();

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Please fill all the fields.", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
            } else {
                registerUser(username, password, email);
            }


        });

    }
    private void switchActivitiesToLogin() {
        Intent switchActivityIntent = new Intent(this, MainActivity.class);
        startActivity(switchActivityIntent);
    }

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

    private void registerUser(String username, String password, String email) {
        ApiService.apiService.register(new User(username, password, email)).enqueue(new Callback<MessageNetwork>() {
            @Override
            public void onResponse(Call<MessageNetwork> call, Response<MessageNetwork> response) {
                MessageNetwork message = response.body();

                if (message != null) {
                    AlertDialog alertDialog;
                    if (message.getMsg().equals("User created successfully.")) {
                        alertDialog = Alert(message.getMsg(), "User created successfully.").create();
                    }
                   else {
                        alertDialog = Alert(message.getMsg(), "Error").create();
                    }
                    alertDialog.show();
                } else {
                    AlertDialog alertDialog = Alert("Username or email already exists.", "Error").create();
                    alertDialog.show();
                }
            }

            @Override
            public void onFailure(Call<MessageNetwork> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Error: " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
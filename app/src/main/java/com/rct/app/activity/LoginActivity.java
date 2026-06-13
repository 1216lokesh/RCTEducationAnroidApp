package com.rct.app.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.rct.app.R;
import com.rct.app.api.ApiConfig;
import com.rct.app.utils.LocaleHelper;
import com.rct.app.utils.NotificationHelper;
import com.rct.app.utils.ReminderScheduler;
import com.rct.app.utils.SessionManager;
import org.json.JSONObject;

public class LoginActivity extends BaseActivity {

    EditText etEmail, etPassword;
    Button btnLogin, btnRegister;
    SessionManager session;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.applyLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        session     = new SessionManager(this);
        etEmail     = findViewById(R.id.etEmail);
        etPassword  = findViewById(R.id.etPassword);
        btnLogin    = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        if (session.isLoggedIn()) {
            goToDashboard(session.getRole());
            finish();
            return;
        }

        btnLogin.setOnClickListener(v -> {
            if (!isNetworkAvailable()) {
                showNoInternetDialog();
                return;
            }
            String email = etEmail.getText().toString().trim();
            String pass  = etPassword.getText().toString().trim();
            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this,
                        "Please fill all fields",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            loginUser(email, pass);
        });

        btnRegister.setOnClickListener(v -> {
            if (!isNetworkAvailable()) {
                showNoInternetDialog();
                return;
            }
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager != null) {
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
        return false;
    }

    private void showNoInternetDialog() {
        new AlertDialog.Builder(this)
                .setTitle("No Internet Connection")
                .setMessage("Please check your internet connection and try again.")
                .setPositiveButton("OK", (dialog, which) ->
                        dialog.dismiss())
                .setCancelable(true)
                .show();
    }

    private void loginUser(String email, String pass) {
        try {
            JSONObject body = new JSONObject();
            body.put("email", email);
            body.put("password", pass);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    ApiConfig.LOGIN,
                    body,
                    response -> {
                        try {
                            String status = response.getString("status");
                            if (status.equals("success")) {
                                int    id   = response.getInt("id");
                                String name = response.getString("name");
                                String role = response.getString("role");

                                session.saveSession(id, name, role);
                                ReminderScheduler.scheduleDailyReminder(this);
                                NotificationHelper.createNotificationChannel(this);
                                NotificationHelper.showLoginNotification(this);

                                goToDashboard(role);
                                finish();
                            } else {
                                Toast.makeText(this,
                                        "Invalid credentials",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(this,
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    },
                    error -> {
                        String msg = "Connection error: ";
                        if (error.networkResponse != null) {
                            msg += "Code " + error.networkResponse.statusCode;
                        } else if (error.getMessage() != null) {
                            msg += error.getMessage();
                        } else {
                            msg += "No response from server";
                        }
                        Toast.makeText(this, msg,
                                Toast.LENGTH_LONG).show();
                    }
            );

            request.setRetryPolicy(new DefaultRetryPolicy(
                    30000, 0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            Volley.newRequestQueue(this).add(request);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this,
                    "Error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void goToDashboard(String role) {
        if (role.equals("admin")) {
            startActivity(new Intent(this,
                    AdminDashboardActivity.class));
        } else {
            startActivity(new Intent(this,
                    DashboardActivity.class));
        }
    }
}
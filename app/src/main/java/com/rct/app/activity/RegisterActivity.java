package com.rct.app.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.rct.app.R;
import com.rct.app.api.ApiConfig;
import com.rct.app.utils.LocaleHelper;
import com.rct.app.utils.NotificationHelper;
import com.rct.app.utils.SessionManager;
import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends BaseActivity {

    EditText etName, etEmail, etPassword, etPhone;
    Button btnRegister;
    TextView tvLogin;
    SessionManager session;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.applyLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        session     = new SessionManager(this);
        etName      = findViewById(R.id.etName);
        etEmail     = findViewById(R.id.etEmail);
        etPassword  = findViewById(R.id.etPassword);
        etPhone     = findViewById(R.id.etPhone);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin     = findViewById(R.id.tvLogin);

        btnRegister.setOnClickListener(v -> registerUser());

        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
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
                .setMessage("Please connect to WiFi and make sure " +
                        "XAMPP is running, then try again.")
                .setPositiveButton("OK", (dialog, which) ->
                        dialog.dismiss())
                .setCancelable(true)
                .show();
    }

    private boolean validateInputs(String name, String email,
                                   String password, String phone) {
        if (name.isEmpty()) {
            etName.setError("Name is required");
            etName.requestFocus();
            return false;
        }

        if (name.length() < 3) {
            etName.setError("Name must be at least 3 characters");
            etName.requestFocus();
            return false;
        }

        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email address");
            etEmail.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return false;
        }

        if (phone.isEmpty()) {
            etPhone.setError("Phone number is required");
            etPhone.requestFocus();
            return false;
        }

        if (!phone.matches("[0-9]{10}")) {
            etPhone.setError("Enter a valid 10-digit phone number");
            etPhone.requestFocus();
            return false;
        }

        return true;
    }

    private void registerUser() {
        // Check internet first
        if (!isNetworkAvailable()) {
            showNoInternetDialog();
            return;
        }

        String name     = etName.getText().toString().trim();
        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String phone    = etPhone.getText().toString().trim();

        if (!validateInputs(name, email, password, phone)) {
            return;
        }

        JSONObject params = new JSONObject();
        try {
            params.put("name",     name);
            params.put("email",    email);
            params.put("password", password);
            params.put("phone",    phone);
            params.put("role",     "patient");
            params.put("language", session.getLanguage());
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                ApiConfig.REGISTER,
                params,
                response -> {
                    try {
                        String status = response.getString("status");
                        if (status.equals("success")) {
                            NotificationHelper.createNotificationChannel(this);
                            NotificationHelper.showRegisterNotification(this);
                            Toast.makeText(this,
                                    "Registered successfully! Please login.",
                                    Toast.LENGTH_LONG).show();
                            startActivity(new Intent(this,
                                    LoginActivity.class));
                            finish();
                        } else {
                            String msg = response.optString("message",
                                    "Registration failed");
                            Toast.makeText(this, msg,
                                    Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this,
                                "Response error: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    String msg = "Connection failed: ";
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
                15000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}
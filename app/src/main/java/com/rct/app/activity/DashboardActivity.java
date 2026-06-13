package com.rct.app.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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

public class DashboardActivity extends BaseActivity {

    TextView tvWelcome, tvProgressCount, tvProcedure, tvCategory, tvDescription;
    ProgressBar progressBar;
    Button btnAppointment1, btnAppointment2,
            btnAppointment3, btnFollowUp, btnLogout;
    SessionManager session;

    String assignedCategory   = "Endodontic";
    int    assignedProcedureId = 0;

    private static final int NOTIFICATION_PERMISSION_CODE = 101;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.applyLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        session = new SessionManager(this);

        tvWelcome        = findViewById(R.id.tvWelcome);
        tvProgressCount  = findViewById(R.id.tvProgressCount);
        progressBar      = findViewById(R.id.progressBar);
        tvProcedure      = findViewById(R.id.tvProcedure);
        tvCategory       = findViewById(R.id.tvCategory);
        tvDescription    = findViewById(R.id.tvDescription);
        btnAppointment1  = findViewById(R.id.btnAppointment1);
        btnAppointment2  = findViewById(R.id.btnAppointment2);
        btnAppointment3  = findViewById(R.id.btnAppointment3);
        btnFollowUp      = findViewById(R.id.btnFollowUp);
        btnLogout        = findViewById(R.id.btnLogout);

        tvWelcome.setText("Welcome, " + session.getName());

        requestNotificationPermission();
        NotificationHelper.createNotificationChannel(this);
        ReminderScheduler.scheduleDailyReminder(this);

        // Load assigned procedure from server
        loadAssignedProcedure();

        btnAppointment1.setOnClickListener(v -> {
            Intent intent = new Intent(this, Baseline1Activity.class);
            intent.putExtra("procedure_id", assignedProcedureId);
            intent.putExtra("category", assignedCategory);
            startActivity(intent);
        });

        btnAppointment2.setOnClickListener(v -> {
            Intent intent = new Intent(this, Baseline2Activity.class);
            intent.putExtra("procedure_id", assignedProcedureId);
            intent.putExtra("category", assignedCategory);
            startActivity(intent);
        });

        btnAppointment3.setOnClickListener(v -> {
            Intent intent = new Intent(this, Baseline3Activity.class);
            intent.putExtra("procedure_id", assignedProcedureId);
            intent.putExtra("category", assignedCategory);
            startActivity(intent);
        });

        btnFollowUp.setOnClickListener(v ->
                startActivity(new Intent(this, Baseline4Activity.class)));

        btnLogout.setOnClickListener(v -> showLogoutDialog());
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateProgress();
    }

    private void loadAssignedProcedure() {
        try {
            JSONObject body = new JSONObject();
            body.put("user_id", session.getUserId());

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    ApiConfig.GET_PATIENT_PROCEDURE,
                    body,
                    response -> {
                        try {
                            String status = response.getString("status");
                            if (status.equals("success")) {
                                String name     = response.getString("procedure_name");
                                String category = response.getString("category");
                                String desc     = response.getString("description");
                                assignedProcedureId = response.getInt("procedure_id");
                                assignedCategory    = category;

                                tvProcedure.setText("📋 Procedure: " + name);
                                tvCategory.setText("🏥 Category: " + category);
                                tvDescription.setText("ℹ️ " + desc);
                            } else {
                                tvProcedure.setText("⚠️ No procedure assigned yet");
                                tvCategory.setText("Please contact your dentist");
                                tvDescription.setText("");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    },
                    error -> {
                        tvProcedure.setText("⚠️ Could not load procedure");
                        tvCategory.setText("Check connection");
                        tvDescription.setText("");
                    }
            );
            Volley.newRequestQueue(this).add(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateProgress() {
        SharedPreferences prefs =
                getSharedPreferences("RCTSession", MODE_PRIVATE);
        int progress = 0;
        if (prefs.getBoolean("apt1_completed", false)) progress++;
        if (prefs.getBoolean("apt2_completed", false)) progress++;
        if (prefs.getBoolean("apt3_completed", false)) progress++;
        if (prefs.getBoolean("followup_completed", false)) progress++;

        progressBar.setProgress(progress);
        tvProgressCount.setText(progress + " / 4 Completed");
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes, Logout", (dialog, which) -> {
                    session.logout();
                    ReminderScheduler.cancelDailyReminder(this);
                    Intent intent = new Intent(this, LanguageActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .show();
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                NotificationHelper.showDailyReminderNotification(this);
            }
        }
    }
}

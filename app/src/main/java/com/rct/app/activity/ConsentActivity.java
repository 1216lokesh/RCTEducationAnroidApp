package com.rct.app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.rct.app.R;
import com.rct.app.api.ApiConfig;
import com.rct.app.utils.LocaleHelper;
import com.rct.app.utils.NotificationHelper;
import com.rct.app.utils.SessionManager;
import org.json.JSONObject;

public class ConsentActivity extends BaseActivity {

    Button btnAgree;
    CheckBox cbConsent;
    SessionManager session;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.applyLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consent);

        session   = new SessionManager(this);
        btnAgree  = findViewById(R.id.btnAgree);
        cbConsent = findViewById(R.id.cbConsent);

        btnAgree.setOnClickListener(v -> {
            if (!cbConsent.isChecked()) {
                Toast.makeText(this,
                        "Please check the consent box first",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            saveConsent("yes");
        });
    }

    private void saveConsent(String consentValue) {
        try {
            JSONObject body = new JSONObject();
            body.put("user_id",       session.getUserId());
            body.put("consent_given", consentValue);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    ApiConfig.SAVE_CONSENT,
                    body,
                    response -> {
                        Toast.makeText(this,
                                "Consent saved!",
                                Toast.LENGTH_SHORT).show();

                        // Mark Appointment 1 completed
                        getSharedPreferences("RCTSession", MODE_PRIVATE)
                                .edit()
                                .putBoolean("apt1_completed", true)
                                .apply();

                        // Show appointment 1 completed notification
                        NotificationHelper
                                .showAppointmentCompletedNotification(
                                        this, 1);

                        // Go to Satisfaction Survey next
                        startActivity(new Intent(this,
                                SatisfactionActivity.class));
                        finish();
                    },
                    error -> Toast.makeText(this,
                            "Error saving consent",
                            Toast.LENGTH_SHORT).show()
            );
            Volley.newRequestQueue(this).add(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
package com.rct.app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.rct.app.R;
import com.rct.app.api.ApiConfig;
import com.rct.app.utils.LocaleHelper;
import com.rct.app.utils.NotificationHelper;
import com.rct.app.utils.SessionManager;
import org.json.JSONObject;

public class FollowUp1WeekActivity extends BaseActivity {

    RadioGroup rg1, rg2, rg3;
    Button btnSubmit;
    SessionManager session;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.applyLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followup1week);

        session   = new SessionManager(this);
        rg1       = findViewById(R.id.rg1);
        rg2       = findViewById(R.id.rg2);
        rg3       = findViewById(R.id.rg3);
        btnSubmit = findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(v -> {
            if (rg1.getCheckedRadioButtonId() == -1 ||
                    rg2.getCheckedRadioButtonId() == -1 ||
                    rg3.getCheckedRadioButtonId() == -1) {
                Toast.makeText(this,
                        getString(R.string.please_answer_all),
                        Toast.LENGTH_SHORT).show();
                return;
            }
            saveFollowUp();
        });
    }

    private void saveFollowUp() {
        try {
            JSONObject body = new JSONObject();
            body.put("user_id", session.getUserId());
            body.put("quiz",    "followup_1week");
            body.put("score",   1);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    ApiConfig.SAVE_SCORE,
                    body,
                    response -> {
                        getSharedPreferences("RCTSession", MODE_PRIVATE)
                                .edit()
                                .putBoolean("followup_completed", true)
                                .apply();

                        NotificationHelper.showAppointmentCompletedNotification(
                                this, 4);

                        Toast.makeText(this,
                                getString(R.string.followup_complete),
                                Toast.LENGTH_LONG).show();

                        startActivity(new Intent(this,
                                DashboardActivity.class));
                        finish();
                    },
                    error -> {
                        String msg = "Error: ";
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
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            Volley.newRequestQueue(this).add(request);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
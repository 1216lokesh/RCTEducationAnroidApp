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
import com.rct.app.utils.SessionManager;
import org.json.JSONObject;

public class AnxietyAssessmentActivity extends BaseActivity {

    RadioGroup rg1, rg2, rg3, rg4, rg5;
    Button btnSubmit;
    SessionManager session;
    int procedureId   = 1;
    int appointmentNo = 1;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.applyLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anxiety_assessment);

        session       = new SessionManager(this);
        procedureId   = getIntent().getIntExtra("procedure_id", 1);
        appointmentNo = getIntent().getIntExtra("appointment_no", 1);

        rg1 = findViewById(R.id.rg1);
        rg2 = findViewById(R.id.rg2);
        rg3 = findViewById(R.id.rg3);
        rg4 = findViewById(R.id.rg4);
        rg5 = findViewById(R.id.rg5);
        btnSubmit = findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(v -> {
            if (rg1.getCheckedRadioButtonId() == -1 ||
                    rg2.getCheckedRadioButtonId() == -1 ||
                    rg3.getCheckedRadioButtonId() == -1 ||
                    rg4.getCheckedRadioButtonId() == -1 ||
                    rg5.getCheckedRadioButtonId() == -1) {
                Toast.makeText(this,
                        getString(R.string.please_answer_all),
                        Toast.LENGTH_SHORT).show();
                return;
            }
            saveAnxiety(calculateScore());
        });
    }

    private int calculateScore() {
        int score = 0;
        score += getSelectedScore(rg1);
        score += getSelectedScore(rg2);
        score += getSelectedScore(rg3);
        score += getSelectedScore(rg4);
        score += getSelectedScore(rg5);
        return score;
    }

    private int getSelectedScore(RadioGroup rg) {
        int id = rg.getCheckedRadioButtonId();
        if (id == rg.getChildAt(0).getId()) return 0;
        if (id == rg.getChildAt(1).getId()) return 1;
        if (id == rg.getChildAt(2).getId()) return 2;
        if (id == rg.getChildAt(3).getId()) return 3;
        return 0;
    }

    private void goToNextScreen() {
        if (appointmentNo == 2) {
            startActivity(new Intent(this, Quiz2Activity.class));
        } else if (appointmentNo == 3) {
            startActivity(new Intent(this, Quiz3Activity.class));
        } else {
            startActivity(new Intent(this, Quiz1Activity.class));
        }
        finish();
    }

    private void saveAnxiety(int score) {
        try {
            JSONObject body = new JSONObject();
            body.put("patient_id",   session.getUserId());
            body.put("procedure_id", procedureId);
            body.put("timepoint",    "apt" + appointmentNo);
            body.put("score",        score);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    ApiConfig.SAVE_ANXIETY,
                    body,
                    response -> {
                        Toast.makeText(this,
                                getString(R.string.anxiety_saved),
                                Toast.LENGTH_SHORT).show();
                        goToNextScreen();
                    },
                    error -> goToNextScreen()
            );

            request.setRetryPolicy(new DefaultRetryPolicy(
                    15000, 0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            Volley.newRequestQueue(this).add(request);
        } catch (Exception e) {
            e.printStackTrace();
            goToNextScreen();
        }
    }
}
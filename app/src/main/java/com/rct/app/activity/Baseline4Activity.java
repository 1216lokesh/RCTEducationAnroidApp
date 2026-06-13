package com.rct.app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.rct.app.R;
import com.rct.app.api.ApiConfig;
import com.rct.app.utils.LocaleHelper;
import com.rct.app.utils.SessionManager;
import org.json.JSONObject;

public class Baseline4Activity extends BaseActivity {

    RadioGroup rg1, rg2, rg3;
    Button btnNext;
    SessionManager session;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.applyLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baseline4);

        session = new SessionManager(this);
        saveAttendance("apt4");

        rg1     = findViewById(R.id.rg1);
        rg2     = findViewById(R.id.rg2);
        rg3     = findViewById(R.id.rg3);
        btnNext = findViewById(R.id.btnNext);

        btnNext.setOnClickListener(v -> {
            if (rg1.getCheckedRadioButtonId() == -1 ||
                    rg2.getCheckedRadioButtonId() == -1 ||
                    rg3.getCheckedRadioButtonId() == -1) {
                Toast.makeText(this,
                        "Please answer all questions",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(this,
                    ReinforcementActivity.class));
            finish();
        });
    }

    private void saveAttendance(String apt) {
        try {
            JSONObject body = new JSONObject();
            body.put("user_id", session.getUserId());
            body.put("apt", apt);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    ApiConfig.SAVE_ATTENDANCE,
                    body,
                    response -> {},
                    error -> {}
            );
            Volley.newRequestQueue(this).add(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
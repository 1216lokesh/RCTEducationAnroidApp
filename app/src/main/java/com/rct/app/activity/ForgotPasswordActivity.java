package com.rct.app.activity;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.rct.app.R;
import com.rct.app.api.ApiConfig;
import com.rct.app.utils.LocaleHelper;
import org.json.JSONObject;

public class ForgotPasswordActivity extends BaseActivity {

    EditText etEmail;
    Button btnSendReset;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.applyLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        etEmail      = findViewById(R.id.etEmail);
        btnSendReset = findViewById(R.id.btnSendReset);

        btnSendReset.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(this,
                        "Please enter your email",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            sendResetLink(email);
        });
    }

    private void sendResetLink(String email) {
        try {
            JSONObject body = new JSONObject();
            body.put("email", email);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    ApiConfig.FORGOT_PASSWORD,
                    body,
                    response -> {
                        try {
                            String status  = response.getString("status");
                            String message = response.getString("message");
                            Toast.makeText(this, message,
                                    Toast.LENGTH_LONG).show();
                            if (status.equals("success")) {
                                finish();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    },
                    error -> Toast.makeText(this,
                            "Connection error. Try again.",
                            Toast.LENGTH_SHORT).show()
            );

            request.setRetryPolicy(new DefaultRetryPolicy(
                    30000, 0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            Volley.newRequestQueue(this).add(request);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

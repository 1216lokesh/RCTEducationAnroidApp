package com.rct.app.activity;

import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rct.app.R;
import com.rct.app.api.ApiConfig;
import com.rct.app.utils.LocaleHelper;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class PatientListActivity extends BaseActivity {

    ListView listView;
    ArrayList<String> patientList;
    List<Integer> patientIds;
    List<String> patientNames;
    ArrayAdapter<String> adapter;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.applyLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_list);

        listView     = findViewById(R.id.listView);
        patientList  = new ArrayList<>();
        patientIds   = new ArrayList<>();
        patientNames = new ArrayList<>();

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, patientList);
        listView.setAdapter(adapter);

        loadPatients();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String name   = patientNames.get(position);
            int    userId = patientIds.get(position);
            showPatientDialog(name, userId);
        });
    }

    // ── Options dialog ────────────────────────────────────────
    private void showPatientDialog(String name, int userId) {
        new AlertDialog.Builder(this)
                .setTitle("Patient: " + name)
                .setMessage("What do you want to do?")
                .setPositiveButton("Reset Password", (dialog, which) ->
                        showResetPasswordDialog(name, userId))
                .setNegativeButton("Cancel", (dialog, which) ->
                        dialog.dismiss())
                .show();
    }

    // ── Password dialog (enter twice) ─────────────────────────
    private void showResetPasswordDialog(String name, int userId) {
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 10);

        EditText etNewPass = new EditText(this);
        etNewPass.setHint("New password");
        etNewPass.setInputType(
                android.text.InputType.TYPE_CLASS_TEXT |
                        android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(etNewPass);

        EditText etConfirmPass = new EditText(this);
        etConfirmPass.setHint("Confirm new password");
        etConfirmPass.setInputType(
                android.text.InputType.TYPE_CLASS_TEXT |
                        android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(etConfirmPass);

        new AlertDialog.Builder(this)
                .setTitle("Reset Password")
                .setMessage("Setting new password for: " + name)
                .setView(layout)
                .setPositiveButton("Reset", (dialog, which) -> {
                    String newPass     = etNewPass.getText().toString().trim();
                    String confirmPass = etConfirmPass.getText().toString().trim();

                    if (newPass.isEmpty() || confirmPass.isEmpty()) {
                        Toast.makeText(this, "Both fields are required", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (newPass.length() < 6) {
                        Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!newPass.equals(confirmPass)) {
                        Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    resetPassword(userId, newPass);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // ── Reset password API call ───────────────────────────────
    private void resetPassword(int userId, String newPassword) {
        Toast.makeText(this, "Resetting password...", Toast.LENGTH_SHORT).show();

        StringRequest request = new StringRequest(
                Request.Method.POST,
                ApiConfig.RESET_PASSWORD,
                rawResponse -> {
                    android.util.Log.d("RESET_DEBUG", "raw: " + rawResponse);
                    try {
                        int start = rawResponse.indexOf("{");
                        if (start == -1) {
                            Toast.makeText(this, "Server error. Check Logcat.", Toast.LENGTH_LONG).show();
                            return;
                        }
                        JSONObject json = new JSONObject(rawResponse.substring(start));
                        if (json.getString("status").equals("success")) {
                            Toast.makeText(this, "✅ Password reset successfully!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, "Failed: " + json.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        android.util.Log.e("RESET_DEBUG", "parse error: " + e.getMessage());
                        Toast.makeText(this, "Parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    android.util.Log.e("RESET_DEBUG", "connection error: " + error.toString());
                    Toast.makeText(this, "Connection error. Try again.", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            public byte[] getBody() {
                try {
                    JSONObject body = new JSONObject();
                    body.put("user_id",      userId);
                    body.put("new_password", newPassword);
                    return body.toString().getBytes("utf-8");
                } catch (Exception e) { return null; }
            }
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    // ── Load patients ─────────────────────────────────────────
    private void loadPatients() {
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                ApiConfig.GET_PATIENTS,
                null,
                response -> {
                    patientList.clear();
                    patientIds.clear();
                    patientNames.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            int    id    = obj.getInt("id");
                            String name  = obj.getString("name");
                            String email = obj.getString("email");
                            String phone = obj.getString("phone");

                            patientIds.add(id);
                            patientNames.add(name);
                            patientList.add(
                                    "👤 " + name +
                                            "\n📧 " + email +
                                            "\n📞 " + phone +
                                            "\n(Tap to reset password)"
                            );
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.notifyDataSetChanged();
                },
                error -> Toast.makeText(this, "Error loading patients", Toast.LENGTH_SHORT).show()
        );
        Volley.newRequestQueue(this).add(request);
    }
}
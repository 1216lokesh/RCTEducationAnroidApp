package com.rct.app.activity;

import android.content.Context;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.rct.app.R;
import com.rct.app.api.ApiConfig;
import com.rct.app.utils.LocaleHelper;
import com.rct.app.utils.SessionManager;
import org.json.JSONObject;
import java.util.ArrayList;

public class AssignProcedureActivity extends BaseActivity {

    Spinner spinnerPatient, spinnerProcedure;
    RadioGroup rgGroup;
    Button btnAssign;
    SessionManager session;

    ArrayList<String> patientNames   = new ArrayList<>();
    ArrayList<Integer> patientIds    = new ArrayList<>();
    ArrayList<String> procedureNames = new ArrayList<>();
    ArrayList<Integer> procedureIds  = new ArrayList<>();

    boolean patientsLoaded   = false;
    boolean proceduresLoaded = false;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.applyLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_procedure);

        session          = new SessionManager(this);
        spinnerPatient   = findViewById(R.id.spinnerPatient);
        spinnerProcedure = findViewById(R.id.spinnerProcedure);
        rgGroup          = findViewById(R.id.rgGroup);
        btnAssign        = findViewById(R.id.btnAssign);

        // Disable button until data loads
        btnAssign.setEnabled(false);
        btnAssign.setText("Loading...");

        loadPatients();
        loadProcedures();

        btnAssign.setOnClickListener(v -> assignProcedure());
    }

    private void checkIfReady() {
        if (patientsLoaded && proceduresLoaded) {
            btnAssign.setEnabled(true);
            btnAssign.setText("Assign Procedure");
        }
    }

    private void loadPatients() {
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                ApiConfig.GET_PATIENTS,
                null,
                response -> {
                    patientNames.clear();
                    patientIds.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            patientNames.add(obj.getString("name"));
                            patientIds.add(obj.getInt("id"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            this,
                            android.R.layout.simple_spinner_item,
                            patientNames);
                    adapter.setDropDownViewResource(
                            android.R.layout.simple_spinner_dropdown_item);
                    spinnerPatient.setAdapter(adapter);
                    patientsLoaded = true;
                    checkIfReady();
                },
                error -> {
                    Toast.makeText(this,
                            "Error loading patients. Check connection.",
                            Toast.LENGTH_LONG).show();
                    patientsLoaded = true;
                    checkIfReady();
                }
        );

        request.setRetryPolicy(new DefaultRetryPolicy(
                15000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(this).add(request);
    }

    private void loadProcedures() {
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                ApiConfig.GET_PROCEDURES,
                null,
                response -> {
                    procedureNames.clear();
                    procedureIds.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            procedureNames.add(
                                    obj.getString("category") +
                                            " - " + obj.getString("name"));
                            procedureIds.add(obj.getInt("id"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            this,
                            android.R.layout.simple_spinner_item,
                            procedureNames);
                    adapter.setDropDownViewResource(
                            android.R.layout.simple_spinner_dropdown_item);
                    spinnerProcedure.setAdapter(adapter);
                    proceduresLoaded = true;
                    checkIfReady();
                },
                error -> {
                    Toast.makeText(this,
                            "Error loading procedures. Check connection.",
                            Toast.LENGTH_LONG).show();
                    proceduresLoaded = true;
                    checkIfReady();
                }
        );

        request.setRetryPolicy(new DefaultRetryPolicy(
                15000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(this).add(request);
    }

    private void assignProcedure() {
        if (patientIds.isEmpty() || procedureIds.isEmpty()) {
            Toast.makeText(this,
                    "No patients or procedures available",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        int patientId   = patientIds.get(
                spinnerPatient.getSelectedItemPosition());
        int procedureId = procedureIds.get(
                spinnerProcedure.getSelectedItemPosition());

        String groupType = "intervention";
        if (rgGroup.getCheckedRadioButtonId() == R.id.rbComparator) {
            groupType = "comparator";
        }

        try {
            JSONObject body = new JSONObject();
            body.put("patient_id",   patientId);
            body.put("procedure_id", procedureId);
            body.put("assigned_by",  session.getUserId());
            body.put("group_type",   groupType);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    ApiConfig.ASSIGN_PROCEDURE,
                    body,
                    response -> {
                        try {
                            if (response.getString("status")
                                    .equals("success")) {
                                Toast.makeText(this,
                                        "Procedure assigned successfully!",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(this,
                                        "Error: " + response.getString(
                                                "message"),
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    },
                    error -> {
                        String msg = "Connection error: ";
                        if (error.networkResponse != null) {
                            msg += "Code " + error.networkResponse.statusCode;
                        } else if (error.getMessage() != null) {
                            msg += error.getMessage();
                        } else {
                            msg += "No response";
                        }
                        Toast.makeText(this, msg,
                                Toast.LENGTH_LONG).show();
                    }
            );

            request.setRetryPolicy(new DefaultRetryPolicy(
                    15000, 0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            Volley.newRequestQueue(this).add(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
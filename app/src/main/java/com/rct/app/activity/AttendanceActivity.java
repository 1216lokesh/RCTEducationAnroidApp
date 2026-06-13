package com.rct.app.activity;

import android.content.Context;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.rct.app.R;
import com.rct.app.api.ApiConfig;
import com.rct.app.utils.LocaleHelper;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class AttendanceActivity extends BaseActivity {

    ListView listView;

    ArrayList<String> attendanceList;
    ArrayAdapter<String> adapter;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.applyLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        listView       = findViewById(R.id.listView);

        attendanceList = new ArrayList<>();
        adapter        = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, attendanceList);
        listView.setAdapter(adapter);

        loadAttendance();
    }

    private void loadAttendance() {
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                ApiConfig.GET_ATTENDANCE,
                null,
                response -> {
                    attendanceList.clear();
                    if (response.length() == 0) {
                        attendanceList.add("No attendance data found");
                        adapter.notifyDataSetChanged();
                        return;
                    }
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            attendanceList.add(
                                    "Name: " + obj.getString("name") +
                                            "\nApt1: " + obj.getString("apt1") +
                                            " | Apt2: " + obj.getString("apt2") +
                                            " | Apt3: " + obj.getString("apt3") +
                                            " | Apt4: " + obj.getString("apt4")
                            );
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.notifyDataSetChanged();
                },
                error -> {
                    String errorMsg = "Error: ";
                    if (error.networkResponse != null) {
                        errorMsg += error.networkResponse.statusCode;
                    } else {
                        errorMsg += error.getMessage();
                    }
                    Toast.makeText(this, errorMsg,
                            Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        Volley.newRequestQueue(this).add(request);
    }
}
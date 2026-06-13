package com.rct.app.activity;

import android.content.Context;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.rct.app.R;
import com.rct.app.api.ApiConfig;
import com.rct.app.utils.LocaleHelper;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class OutcomesReportActivity extends BaseActivity {

    ListView listView;
    ArrayList<String> outcomeList;
    ArrayAdapter<String> adapter;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.applyLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outcomes_report);

        listView    = findViewById(R.id.listView);
        outcomeList = new ArrayList<>();
        adapter     = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                outcomeList);
        listView.setAdapter(adapter);

        loadOutcomes();
    }

    private void loadOutcomes() {
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                ApiConfig.GET_OUTCOMES,
                null,
                response -> {
                    outcomeList.clear();
                    if (response.length() == 0) {
                        outcomeList.add("No outcome data found");
                        adapter.notifyDataSetChanged();
                        return;
                    }
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            outcomeList.add(
                                    "👤 " + obj.getString("name") +
                                            "\n📋 " + obj.getString("procedure_name") +
                                            "\n👥 Group: " + obj.getString("group_type") +
                                            "\n⏱ Timepoint: " + obj.getString("timepoint") +
                                            "\n📝 Knowledge: " +
                                            obj.getString("pre_knowledge") +
                                            "/" + obj.getString("post_knowledge") +
                                            "\n😰 Anxiety: " +
                                            obj.getString("anxiety_score") +
                                            "\n😊 Satisfaction: " +
                                            obj.getString("satisfaction_score")
                            );
                        } catch (Exception e) {
                            e.printStackTrace();
                            outcomeList.add("Error reading record " + i);
                        }
                    }
                    adapter.notifyDataSetChanged();
                },
                error -> {
                    String msg = "Error: ";
                    if (error.networkResponse != null) {
                        msg += "Code " + error.networkResponse.statusCode;
                    } else if (error.getMessage() != null) {
                        msg += error.getMessage();
                    } else {
                        msg += "Connection failed";
                    }
                    Toast.makeText(this, msg,
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
                15000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(this).add(request);
    }
}
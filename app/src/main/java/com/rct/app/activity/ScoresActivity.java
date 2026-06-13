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

public class ScoresActivity extends BaseActivity {

    ListView listView;
    ArrayList<String> scoreList;
    ArrayAdapter<String> adapter;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.applyLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);

        listView  = findViewById(R.id.listView);
        scoreList = new ArrayList<>();
        adapter   = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, scoreList);
        listView.setAdapter(adapter);

        loadScores();
    }

    private void loadScores() {
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                ApiConfig.GET_SCORES,
                null,
                response -> {
                    scoreList.clear();
                    if (response.length() == 0) {
                        scoreList.add("No score data found");
                        adapter.notifyDataSetChanged();
                        return;
                    }
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            scoreList.add(
                                    "Name: " + obj.getString("name") +
                                            "\nQuiz1: " + obj.getString("quiz1") +
                                            " | Quiz2: " + obj.getString("quiz2") +
                                            " | Quiz3: " + obj.getString("quiz3")
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
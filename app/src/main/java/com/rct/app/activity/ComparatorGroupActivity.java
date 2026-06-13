package com.rct.app.activity;

import android.content.Context;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.rct.app.R;
import com.rct.app.api.ApiConfig;
import com.rct.app.utils.LocaleHelper;
import org.json.JSONObject;
import java.util.ArrayList;

public class ComparatorGroupActivity extends BaseActivity {

    ListView listView;
    ArrayList<String> comparatorList;
    ArrayAdapter<String> adapter;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.applyLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comparator_group);

        listView       = findViewById(R.id.listView);
        comparatorList = new ArrayList<>();
        adapter        = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                comparatorList);
        listView.setAdapter(adapter);

        loadComparators();
    }

    private void loadComparators() {
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                ApiConfig.GET_COMPARATOR,
                null,
                response -> {
                    comparatorList.clear();
                    if (response.length() == 0) {
                        comparatorList.add(
                                "No comparator group patients found");
                        adapter.notifyDataSetChanged();
                        return;
                    }
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            comparatorList.add(
                                    "👤 " + obj.getString("name") +
                                            "\n📧 " + obj.getString("email") +
                                            "\n📞 " + obj.getString("phone") +
                                            "\n📋 " + obj.getString("procedure_name") +
                                            "\n👥 Group: " + obj.getString("group_type") +
                                            "\n📅 Date: " + obj.getString("assigned_date")
                            );
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.notifyDataSetChanged();
                },
                error -> Toast.makeText(this,
                        "Error loading comparator group",
                        Toast.LENGTH_SHORT).show()
        );
        Volley.newRequestQueue(this).add(request);
    }
}
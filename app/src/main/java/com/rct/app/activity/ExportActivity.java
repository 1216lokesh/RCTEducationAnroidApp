package com.rct.app.activity;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rct.app.R;
import com.rct.app.api.ApiConfig;
import com.rct.app.utils.LocaleHelper;

public class ExportActivity extends BaseActivity {

    Button btnExportCSV;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.applyLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);

        btnExportCSV = findViewById(R.id.btnExportCSV);
        btnExportCSV.setOnClickListener(v -> exportData());
    }

    private void exportData() {
        StringRequest request = new StringRequest(
                Request.Method.GET,
                ApiConfig.EXPORT_DATA,
                response -> Toast.makeText(this,
                        "Export successful!", Toast.LENGTH_LONG).show(),
                error -> Toast.makeText(this,
                        "Export failed", Toast.LENGTH_SHORT).show()
        );
        Volley.newRequestQueue(this).add(request);
    }
}
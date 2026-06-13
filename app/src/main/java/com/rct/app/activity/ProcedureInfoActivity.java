package com.rct.app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import com.rct.app.R;
import com.rct.app.utils.LocaleHelper;

public class ProcedureInfoActivity extends BaseActivity {

    Button btnNext;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.applyLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_procedure_info);

        btnNext = findViewById(R.id.btnNext);
        btnNext.setOnClickListener(v -> {
            startActivity(new Intent(this,
                    Education1Activity.class));
            finish();
        });
    }
}
package com.rct.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.rct.app.R;

public class ReinforcementActivity extends BaseActivity {

    Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reinforcement);

        btnNext = findViewById(R.id.btnNext);
        btnNext.setOnClickListener(v ->
                startActivity(new Intent(this,
                        FinalAssessmentActivity.class)));
    }
}
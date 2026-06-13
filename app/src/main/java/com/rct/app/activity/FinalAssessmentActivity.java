package com.rct.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.rct.app.R;
import com.rct.app.utils.NotificationHelper;

public class FinalAssessmentActivity extends BaseActivity {

    RadioGroup rg1, rg2, rg3;
    Button btnFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_assessment);

        rg1 = findViewById(R.id.rg1);
        rg2 = findViewById(R.id.rg2);
        rg3 = findViewById(R.id.rg3);
        btnFinish = findViewById(R.id.btnFinish);

        btnFinish.setOnClickListener(v -> {
            if (rg1.getCheckedRadioButtonId() == -1 ||
                    rg2.getCheckedRadioButtonId() == -1 ||
                    rg3.getCheckedRadioButtonId() == -1) {
                Toast.makeText(this,
                        "Please answer all questions",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // Mark Follow Up completed
            getSharedPreferences("RCTSession", MODE_PRIVATE)
                    .edit()
                    .putBoolean("followup_completed", true)
                    .apply();

            // Show completion notifications
            NotificationHelper.showQuizCompletedNotification(this, 4);
            NotificationHelper.showAppointmentCompletedNotification(this, 4);

            Toast.makeText(this,
                    "Assessment complete! Proceeding to follow up. 🎉",
                    Toast.LENGTH_SHORT).show();

            // Go to 1 week follow up instead of Dashboard
            startActivity(new Intent(this, FollowUp1WeekActivity.class));
            finish();
        });
    }
}

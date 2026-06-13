package com.rct.app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.rct.app.R;
import com.rct.app.utils.LocaleHelper;
import com.rct.app.utils.SessionManager;

public class AdminDashboardActivity extends BaseActivity {

    TextView tvWelcome;
    Button btnPatientList, btnScores,
            btnConsent, btnAttendance,
            btnExport, btnAssignProcedure,
            btnOutcomes, btnComparator,
            btnLogout;
    SessionManager session;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.applyLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        session = new SessionManager(this);

        tvWelcome          = findViewById(R.id.tvWelcome);
        btnPatientList     = findViewById(R.id.btnPatientList);
        btnScores          = findViewById(R.id.btnScores);
        btnConsent         = findViewById(R.id.btnConsent);
        btnAttendance      = findViewById(R.id.btnAttendance);
        btnExport          = findViewById(R.id.btnExport);
        btnAssignProcedure = findViewById(R.id.btnAssignProcedure);
        btnOutcomes        = findViewById(R.id.btnOutcomes);
        btnComparator      = findViewById(R.id.btnComparator);
        btnLogout          = findViewById(R.id.btnLogout);

        tvWelcome.setText("Admin: " + session.getName());

        btnPatientList.setOnClickListener(v ->
                startActivity(new Intent(this,
                        PatientListActivity.class)));

        btnScores.setOnClickListener(v ->
                startActivity(new Intent(this,
                        ScoresActivity.class)));

        btnConsent.setOnClickListener(v ->
                startActivity(new Intent(this,
                        ConsentStatusActivity.class)));

        btnAttendance.setOnClickListener(v ->
                startActivity(new Intent(this,
                        AttendanceActivity.class)));

        btnExport.setOnClickListener(v ->
                startActivity(new Intent(this,
                        ExportActivity.class)));

        btnAssignProcedure.setOnClickListener(v ->
                startActivity(new Intent(this,
                        AssignProcedureActivity.class)));

        btnOutcomes.setOnClickListener(v ->
                startActivity(new Intent(this,
                        OutcomesReportActivity.class)));

        btnComparator.setOnClickListener(v ->
                startActivity(new Intent(this,
                        ComparatorGroupActivity.class)));

        btnLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes, Logout", (dialog, which) -> {
                    session.logout();
                    Intent intent = new Intent(this,
                            LanguageActivity.class);
                    intent.setFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK |
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", (dialog, which) ->
                        dialog.dismiss())
                .setCancelable(true)
                .show();
    }
}

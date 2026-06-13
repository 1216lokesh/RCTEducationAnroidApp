package com.rct.app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.rct.app.R;
import com.rct.app.utils.LocaleHelper;
import com.rct.app.utils.SessionManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.applyLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SessionManager session = new SessionManager(this);

        new Handler().postDelayed(() -> {
            if (session.isLoggedIn()) {
                String role = session.getRole();
                if (role.equals("admin")) {
                    startActivity(new Intent(this,
                            AdminDashboardActivity.class));
                } else {
                    startActivity(new Intent(this,
                            DashboardActivity.class));
                }
            } else {
                startActivity(new Intent(this,
                        LanguageActivity.class));
            }
            finish();
        }, 3000);
    }
}
package com.rct.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.rct.app.R;
import com.rct.app.utils.LocaleHelper;
import com.rct.app.utils.SessionManager;

public class LanguageActivity extends BaseActivity {

    Button btnEnglish, btnTamil, btnHindi, btnTelugu;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        session    = new SessionManager(this);
        btnEnglish = findViewById(R.id.btnEnglish);
        btnTamil   = findViewById(R.id.btnTamil);
        btnHindi   = findViewById(R.id.btnHindi);
        btnTelugu  = findViewById(R.id.btnTelugu);

        btnEnglish.setOnClickListener(v -> setLanguage("en"));
        btnTamil.setOnClickListener(v   -> setLanguage("ta"));
        btnHindi.setOnClickListener(v   -> setLanguage("hi"));
        btnTelugu.setOnClickListener(v  -> setLanguage("te"));
    }

    private void setLanguage(String langCode) {
        // Save language in session
        session.saveLanguage(langCode);

        // Apply locale
        LocaleHelper.setLocale(this, langCode);

        // Go to login, clear back stack
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
package com.rct.app.activity;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import com.rct.app.utils.LocaleHelper;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.applyLocale(newBase));
    }
}
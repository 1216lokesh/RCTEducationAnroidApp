package com.rct.app.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import com.rct.app.R;
import com.rct.app.utils.LocaleHelper;

public class Education1Activity extends BaseActivity {

    Button btnNext;
    ImageView imgThumbnail;
    String videoId = "oZU9Wd_cpYY";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.applyLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education1);

        btnNext      = findViewById(R.id.btnNext);
        imgThumbnail = findViewById(R.id.imgThumbnail);

        imgThumbnail.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/watch?v=" + videoId));
            startActivity(intent);
        });

        btnNext.setOnClickListener(v -> {
            Intent intent = new Intent(this,
                    AnxietyAssessmentActivity.class);
            intent.putExtra("appointment_no", 1);
            startActivity(intent);
            finish();
        });
    }
}
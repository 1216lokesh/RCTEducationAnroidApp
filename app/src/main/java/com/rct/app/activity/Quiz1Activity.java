package com.rct.app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.rct.app.R;
import com.rct.app.api.ApiConfig;
import com.rct.app.utils.LocaleHelper;
import com.rct.app.utils.NotificationHelper;
import com.rct.app.utils.SessionManager;
import org.json.JSONObject;

public class Quiz1Activity extends BaseActivity {

    RadioGroup rg1, rg2, rg3;
    Button btnSubmit;
    SessionManager session;

    final int correct1 = R.id.rb1b;
    final int correct2 = R.id.rb2b;
    final int correct3 = R.id.rb3b;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.applyLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz1);

        session   = new SessionManager(this);
        rg1       = findViewById(R.id.rg1);
        rg2       = findViewById(R.id.rg2);
        rg3       = findViewById(R.id.rg3);
        btnSubmit = findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(v -> {
            if (rg1.getCheckedRadioButtonId() == -1 ||
                    rg2.getCheckedRadioButtonId() == -1 ||
                    rg3.getCheckedRadioButtonId() == -1) {
                Toast.makeText(this,
                        "Please answer all questions",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            int score = 0;
            if (rg1.getCheckedRadioButtonId() == correct1) score++;
            if (rg2.getCheckedRadioButtonId() == correct2) score++;
            if (rg3.getCheckedRadioButtonId() == correct3) score++;

            btnSubmit.setEnabled(false);
            saveScore(score);
        });
    }

    private void showScoreDialog(int score) {
        Context localizedContext = LocaleHelper.applyLocale(this);

        String emoji;
        String message;

        if (score == 3) {
            emoji = "🎉";
            message = localizedContext.getString(R.string.quiz_msg_excellent);
        } else if (score == 2) {
            emoji = "👍";
            message = localizedContext.getString(R.string.quiz_msg_good);
        } else if (score == 1) {
            emoji = "📖";
            message = localizedContext.getString(R.string.quiz_msg_keep);
        } else {
            emoji = "💪";
            message = localizedContext.getString(R.string.quiz_msg_retry);
        }

        String title      = emoji + " " + localizedContext.getString(
                R.string.quiz_result_title);
        String scoreText  = localizedContext.getString(
                R.string.quiz_score_label) + ": " + score + " / 3\n\n" + message;
        String continueBtn = localizedContext.getString(R.string.quiz_continue);

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(scoreText)
                .setPositiveButton(continueBtn, (dialog, which) -> {
                    NotificationHelper.showQuizCompletedNotification(
                            this, 1);
                    startActivity(new Intent(this,
                            Counselling1Activity.class));
                    finish();
                })
                .setCancelable(false)
                .show();
    }

    private void saveScore(int score) {
        try {
            JSONObject body = new JSONObject();
            body.put("user_id", session.getUserId());
            body.put("quiz",    "quiz1");
            body.put("score",   score);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    ApiConfig.SAVE_SCORE,
                    body,
                    response -> {
                        try {
                            if (response.getString("status")
                                    .equals("success")) {
                                showScoreDialog(score);
                            } else {
                                // Still show dialog even if error
                                showScoreDialog(score);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            showScoreDialog(score);
                        }
                    },
                    error -> {
                        // Even if connection fails still show dialog
                        showScoreDialog(score);
                    }
            );

            request.setRetryPolicy(new DefaultRetryPolicy(
                    15000, 0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            Volley.newRequestQueue(this).add(request);
        } catch (Exception e) {
            e.printStackTrace();
            showScoreDialog(score);
        }
    }
}
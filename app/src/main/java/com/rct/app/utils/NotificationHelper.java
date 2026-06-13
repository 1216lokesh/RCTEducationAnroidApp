package com.rct.app.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.rct.app.R;
import com.rct.app.activity.DashboardActivity;
import com.rct.app.activity.LoginActivity;

public class NotificationHelper {

    private static final String CHANNEL_ID = "rct_channel";
    private static final String CHANNEL_NAME = "RCT Notifications";
    private static final int QUIZ_NOTIFICATION_ID = 1001;
    private static final int APPOINTMENT_NOTIFICATION_ID = 1002;
    private static final int REMINDER_NOTIFICATION_ID = 1003;
    private static final int LOGIN_NOTIFICATION_ID = 2001;
    private static final int REGISTER_NOTIFICATION_ID = 2002;

    // Create notification channel
    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("RCT Education App Notifications");
            NotificationManager manager = context.getSystemService(
                    NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    // Quiz completion notification - uses selected language
    public static void showQuizCompletedNotification(Context context,
                                                     int appointmentNo) {
        // Apply saved language to context
        Context localizedContext = LocaleHelper.applyLocale(context);
        String title = localizedContext.getString(
                R.string.notif_quiz_title);
        String message = localizedContext.getString(
                R.string.notif_quiz_message, appointmentNo);
        showNotification(context,
                QUIZ_NOTIFICATION_ID + appointmentNo,
                title, message, DashboardActivity.class);
    }

    // Appointment completed notification - uses selected language
    public static void showAppointmentCompletedNotification(Context context,
                                                            int appointmentNo) {
        Context localizedContext = LocaleHelper.applyLocale(context);
        String title = localizedContext.getString(
                R.string.notif_apt_title, appointmentNo);
        String message = localizedContext.getString(
                R.string.notif_apt_message, appointmentNo);
        showNotification(context,
                APPOINTMENT_NOTIFICATION_ID + appointmentNo,
                title, message, DashboardActivity.class);
    }

    // Pending appointment reminder - uses selected language
    public static void showPendingAppointmentNotification(Context context,
                                                          int appointmentNo) {
        Context localizedContext = LocaleHelper.applyLocale(context);
        String title = localizedContext.getString(
                R.string.notif_pending_title);
        String message = localizedContext.getString(
                R.string.notif_pending_message, appointmentNo);
        showNotification(context,
                REMINDER_NOTIFICATION_ID + appointmentNo,
                title, message, DashboardActivity.class);
    }

    // Daily reminder - uses selected language
    public static void showDailyReminderNotification(Context context) {
        Context localizedContext = LocaleHelper.applyLocale(context);
        String title = localizedContext.getString(
                R.string.notif_daily_title);
        String message = localizedContext.getString(
                R.string.notif_daily_message);
        showNotification(context, REMINDER_NOTIFICATION_ID,
                title, message, DashboardActivity.class);
    }

    // Login notification - uses selected language
    public static void showLoginNotification(Context context) {
        Context localizedContext = LocaleHelper.applyLocale(context);
        String title = localizedContext.getString(
                R.string.notif_login_title);
        String message = localizedContext.getString(
                R.string.notif_login_message);
        showNotification(context, LOGIN_NOTIFICATION_ID,
                title, message, DashboardActivity.class);
    }

    // Registration notification - uses selected language
    public static void showRegisterNotification(Context context) {
        Context localizedContext = LocaleHelper.applyLocale(context);
        String title = localizedContext.getString(
                R.string.notif_register_title);
        String message = localizedContext.getString(
                R.string.notif_register_message);
        showNotification(context, REGISTER_NOTIFICATION_ID,
                title, message, LoginActivity.class);
    }

    // Core notification method
    private static void showNotification(Context context,
                                         int notificationId, String title, String message,
                                         Class<?> targetActivity) {
        createNotificationChannel(context);

        Intent intent = new Intent(context, targetActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, notificationId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT |
                        PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(message))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

        NotificationManager manager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(notificationId, builder.build());
        }
    }
}
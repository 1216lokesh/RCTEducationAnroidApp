package com.rct.app.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class ReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action == null) return;

        SharedPreferences prefs = context.getSharedPreferences(
                "RCTSession", Context.MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);

        // Only show notifications if user is logged in
        if (!isLoggedIn) return;

        if (action.equals("android.intent.action.BOOT_COMPLETED")) {
            // Restart daily reminder after phone reboot
            ReminderScheduler.scheduleDailyReminder(context);
        } else if (action.equals("com.rct.app.DAILY_REMINDER")) {
            // Show daily reminder notification
            NotificationHelper.showDailyReminderNotification(context);

            // Check pending appointments
            checkPendingAppointments(context, prefs);
        }
    }

    private void checkPendingAppointments(Context context,
                                          SharedPreferences prefs) {
        boolean apt1Done = prefs.getBoolean("apt1_completed", false);
        boolean apt2Done = prefs.getBoolean("apt2_completed", false);
        boolean apt3Done = prefs.getBoolean("apt3_completed", false);
        boolean followupDone = prefs.getBoolean("followup_completed", false);

        if (!apt1Done) {
            NotificationHelper.showPendingAppointmentNotification(
                    context, 1);
        } else if (!apt2Done) {
            NotificationHelper.showPendingAppointmentNotification(
                    context, 2);
        } else if (!apt3Done) {
            NotificationHelper.showPendingAppointmentNotification(
                    context, 3);
        } else if (!followupDone) {
            NotificationHelper.showPendingAppointmentNotification(
                    context, 4);
        }
    }
}

package com.rct.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;

    private static final String PREF_NAME = "RCTSession";
    private static final String KEY_ID       = "user_id";
    private static final String KEY_NAME     = "user_name";
    private static final String KEY_ROLE     = "user_role";
    private static final String KEY_LANGUAGE = "language";

    public SessionManager(Context context) {
        this.context = context;
        pref   = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void saveSession(int id, String name, String role) {
        editor.putInt(KEY_ID, id);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_ROLE, role);
        editor.apply();
    }

    public void saveLanguage(String lang) {
        editor.putString(KEY_LANGUAGE, lang);
        editor.apply();
    }

    public String getLanguage() {
        return pref.getString(KEY_LANGUAGE, "en");
    }

    public int getUserId() {
        return pref.getInt(KEY_ID, 0);
    }

    public String getName() {
        return pref.getString(KEY_NAME, "");
    }

    public String getRole() {
        return pref.getString(KEY_ROLE, "");
    }

    public void logout() {
        // Keep language even after logout
        String lang = getLanguage();
        editor.clear();
        editor.putString(KEY_LANGUAGE, lang);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return pref.getInt(KEY_ID, 0) != 0;
    }
}
package com.rct.app.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import java.util.Locale;

public class LocaleHelper {

    public static Context applyLocale(Context context) {
        SharedPreferences pref = context.getSharedPreferences(
                "RCTSession", Context.MODE_PRIVATE);
        String lang = pref.getString("language", "en");
        return setLocale(context, lang);
    }

    public static Context setLocale(Context context, String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration(
                context.getResources().getConfiguration());
        config.setLocale(locale);
        return context.createConfigurationContext(config);
    }
}

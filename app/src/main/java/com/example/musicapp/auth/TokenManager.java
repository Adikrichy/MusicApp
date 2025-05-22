package com.example.musicapp.auth;

import android.content.Context;
import android.content.SharedPreferences;


public class TokenManager {
    private static final String PREF_NAME = "auth_prefs";
    private static final String KEY_ACCESS = "ACCESS_TOKEN";
    private static final String KEY_REFRESH = "REFRESH_TOKEN";

    private final SharedPreferences prefs;

    public TokenManager(Context ctx) {
        prefs = ctx.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveTokens(String access, String refresh) {
        prefs.edit()
                .putString(KEY_ACCESS, access)
                .putString(KEY_REFRESH, refresh)
                .apply();
    }

    public String getAccessToken() {
        return prefs.getString(KEY_ACCESS, null);
    }

    public String getRefreshToken() {
        return prefs.getString(KEY_REFRESH, null);
    }

    public void clear() {
        prefs.edit().clear().apply();
    }
}


package com.example.slatkishka;

import android.content.Context;
import android.content.SharedPreferences;

// потребна ни е за да имаме податоци за моментално логираниот корисник!

public class SessionManager {
    private static final String PREF_NAME = "SlatkishkaPrefs";
    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_ROLE = "userRole";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context context;

    // конструктор
    public SessionManager(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE); // приватна поставка
        editor = prefs.edit();
    }

    // отварање нова сесија при успешно најавување
    public void createLoginSession(String username, String role) {
        editor.putBoolean(KEY_IS_LOGGEDIN, true);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_ROLE, role);
        editor.apply();
    }

    public boolean isLoggedIn() { // дали е логиран
        return prefs.getBoolean(KEY_IS_LOGGEDIN, false);
    }

    public String getUsername() { // да врати корисничко име
        return prefs.getString(KEY_USERNAME, null);
    }

    public String getRole() { // да врати дали е клиент или бизнис
        return prefs.getString(KEY_ROLE, null);
    }

    public void logout() { // се бришат податоците при одјавување
        editor.clear();
        editor.apply();
    }
}
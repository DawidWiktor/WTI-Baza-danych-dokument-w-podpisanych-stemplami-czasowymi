package com.example.dawid.wtistemple;

import android.app.Application;

/**
 * Created by Dawid on 08.05.2017.
 */

public class GlobalValue extends Application {
    private String LoginGlobal, PasswordGlobal;

    public String getLoginGlobal() {
        return LoginGlobal;
    }

    public void setLoginGlobal(String str) {
        LoginGlobal = str;
    }

    public String getPasswordGlobal() {
        return PasswordGlobal;
    }

    public void setPasswordGlobal(String str) {
        PasswordGlobal = str;
    }
}

package com.example.dawid.wtistemple;

import android.app.Application;

/**
 * Created by Dawid on 08.05.2017.
 */

public class GlobalValue extends Application {
    public static String LoginGlobal, PasswordGlobal, TokenGlobal;

    public static String getLoginGlobal() {
        return LoginGlobal;
    }
    public static void setTokenGlobal(String token) { TokenGlobal = token;}
    public static String getTokenGlobal () {return TokenGlobal;}
    public static  void setLoginGlobal(String str) {
        LoginGlobal = str;
    }

    public static String getPasswordGlobal() {
        return PasswordGlobal;
    }

    public static void setPasswordGlobal(String str) {
        PasswordGlobal = str;
    }
}

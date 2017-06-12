package com.example.dawid.wtistemple;

import android.app.Application;
import android.app.ListActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dawid on 08.05.2017.
 */

public class GlobalValue extends Application {
    public static String LoginGlobal, PasswordGlobal, TokenGlobal;
    public static final String ipAdres = "192.168.137.1:8000";
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
    public static String sciezkaPobrania;
    public static ArrayList<SzczegolyDokumentow> listaArchiwum = new ArrayList<SzczegolyDokumentow>();
    public static void setListaArchiwum(ArrayList<SzczegolyDokumentow> list)
    {
        listaArchiwum= list;
    }
    public static ArrayList<SzczegolyDokumentow> getListaArchiwum(){
        return listaArchiwum;
    }
    public static int WybranyDokument;
    public static SzczegolyDokumentow sprawdzonyPlik;
    public  static String PlikPath;

}

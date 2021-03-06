package com.example.dawid.wtistemple;

/**
 * Created by Dawid on 11.06.2017.
 */

import android.app.Activity;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Created by Dawid on 30.04.2017.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.List;
import java.util.Objects;

import javax.crypto.KeyAgreement;
import javax.crypto.spec.DHParameterSpec;


/**
 * Created by Dawid on 29.04.2017.
 */

public class PobieraniePlikuZMagnetycznegoAsync extends AsyncTask<String, Void, String> {

    private Activity activity;

    public PobieraniePlikuZMagnetycznegoAsync(Activity activity)
    {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        return wgrajPlik(params[0]);

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Toast.makeText(activity, "Plik został pobrany w\n" + GlobalValue.sciezkaPobrania, Toast.LENGTH_SHORT).show();
    }

    private String wgrajPlik(String plikPath)
    {
        String wiadomosc = "";

        String haszPliku = "";
        String wynik = upload(plikPath);
        /*String status ="";
        Log.d("cosssssss", "asdasdasd");
        if(wynik.equals(""))
        {
            JSONObject jsonObj = null;
            try {
                jsonObj = new JSONObject(wynik);
                JSONObject objectjso = jsonObj.getJSONObject("upload");
                status = objectjso.getString("status");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(status.equals("error"))
            {
                wiadomosc = wiadomosc + "Błąd operacji";
                Snackbar.make(activity.getCurrentFocus(), wiadomosc, Snackbar.LENGTH_LONG).show();
                return wiadomosc;
            }
            else if(status.equals("file exists"))
            {
                wiadomosc = "Taki plik już istnieje:\n" + plikPath;
                Snackbar snackbar = Snackbar.make(activity.getCurrentFocus(), wiadomosc, Snackbar.LENGTH_INDEFINITE);
                View snackbarView = snackbar.getView();
                TextView tv= (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                tv.setMaxLines(30);
                snackbar.show();
                return wiadomosc;
            }
            else if(status.equals("ok"))
            {
                wiadomosc = wiadomosc + "Plik został podpisany stemplem czasowym";
                Snackbar.make(activity.getCurrentFocus(), wiadomosc, Snackbar.LENGTH_LONG).show();
                return wiadomosc;
            }
        }
        try {
            haszPliku = AlgorytmSHA256.hashFile(plikPath);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Snackbar snackbar =  Snackbar.make(activity.getCurrentFocus(), haszPliku,Snackbar.LENGTH_INDEFINITE);
        View snackbarView = snackbar.getView();
        TextView tv= (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setMaxLines(30);
        snackbar.show();*/

        return wiadomosc;
    }


    public static String upload(String pathFile){
        String charset = "UTF-8";
        File uploadFile1 = new File(pathFile);
        String wynik = "";
        String requestURL = "http://"+GlobalValue.ipAdres+"/api/download_file/";
        Log.d("ttatat", "asdasd");
        try {
            MultipartUtilitySprawdzenie multipart = new MultipartUtilitySprawdzenie(requestURL, charset);
            multipart.addFormField("token", GlobalValue.getTokenGlobal());
            multipart.addFilePart("file", uploadFile1);
            Log.d("asdasdasf"," ASDasd");
            String response = multipart.finish();
            Log.d("axvr",response);
        } catch (IOException ex) {
            System.err.println(ex);
        }
        return  wynik;
    }
}

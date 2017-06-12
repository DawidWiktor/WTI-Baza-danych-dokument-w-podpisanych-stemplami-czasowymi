package com.example.dawid.wtistemple;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

import static android.R.attr.data;
import static android.R.attr.theme;

/**
 * Created by Dawid on 29.04.2017.
 */

public class LogowanieAsync extends AsyncTask<String, String, String> {

    private Activity activity;
    private ProgressBar progressBar;
    private EditText login, haslo;

    public LogowanieAsync(Activity activity)
    {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar = (ProgressBar) activity.findViewById(R.id.logowaniePB);
        progressBar.setVisibility(View.VISIBLE);
        login = (EditText) activity.findViewById(R.id.loginLogowanie);

        haslo = (EditText) activity.findViewById(R.id.hasloLogowanie);


    }


    @Override
    protected String doInBackground(String... params) {
        return sprawdzenieDanych();

    }



    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        progressBar.setVisibility(View.INVISIBLE);

    }
    public static String sha256(String base) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]).toUpperCase();
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }


    private String sprawdzenieDanych(){
        String wiadomosc = "";

        String logi = login.getText().toString();
        String hasl = haslo.getText().toString();


        if(logi.isEmpty() || hasl.isEmpty())
        {
            wiadomosc = wiadomosc + "Wszystkie pola muszą być uzupełnione";
            Snackbar.make(activity.getCurrentFocus(), wiadomosc, Snackbar.LENGTH_LONG).show();
        }
        else {
            String token = "";
            String wynik = laczenie();
            //odczytanie jsona
            if (wynik != null) {
                try {
                    JSONObject jsonObj = new JSONObject(wynik);
                    JSONObject objectjso = jsonObj.getJSONObject("login");
                    token = objectjso.getString("token");
                    //String a = GlobalValue.getLoginGlobal();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (token.equals("error") || token.length() < 10) {
                wiadomosc = wiadomosc + "Zły login lub hasło";
                Snackbar.make(activity.getCurrentFocus(), wiadomosc, Snackbar.LENGTH_LONG).show();
                return wiadomosc;
            } else {
                GlobalValue.setTokenGlobal(token);
                Intent intent = new Intent(activity, menuActivity.class);
                activity.startActivity(intent);
            }

        }
        return wiadomosc;
    }

    public String laczenie(){
        String requestURL = "http://"+GlobalValue.ipAdres+"/api/login/";
        URL url;
        String response = "";
        try {
            url = new URL(requestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);


            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("username", login.getText().toString())
                    .appendQueryParameter("password", haslo.getText().toString());

            String query = builder.build().getEncodedQuery();

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));

            writer.write(query);
            writer.flush();
            writer.close();
            os.close();
            int responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            }
            else {
                response="";

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }
}


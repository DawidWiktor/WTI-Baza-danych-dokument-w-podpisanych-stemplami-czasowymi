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

public class ZmianaEmailAsync extends AsyncTask<String, String, String> {

    private Activity activity;
    private EditText email;
    public ZmianaEmailAsync(Activity activity)
    {
        this.activity = activity;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        email = (EditText) activity.findViewById(R.id.zmienEmailET);
    }


    @Override
    protected String doInBackground(String... params) {
        return sprawdzenieDanych();

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }



    private String sprawdzenieDanych() {
        String newEmail = email.getText().toString();
        String wiadomosc = "";
        if(!isValidEmailAddress(newEmail))
        {
            wiadomosc = "Wpisany e-mail jest niepoprawny";
            Snackbar.make(activity.getCurrentFocus(), wiadomosc, Snackbar.LENGTH_LONG).show();
            return wiadomosc;
        }

        String status = "";
        String wynik = laczenie();


        if (wynik != null) {
            try {
                JSONObject jsonObj = new JSONObject(wynik);
                JSONObject objectjso = jsonObj.getJSONObject("mail");
                status = objectjso.getString("status");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (status.equals("error") || status.length() > 3) {
            wiadomosc = wiadomosc + "Błąd operacji";
            Snackbar.make(activity.getCurrentFocus(), wiadomosc, Snackbar.LENGTH_LONG).show();
            return wiadomosc;
        } else {
            Intent intent = new Intent(activity, logowanieActivity.class);
            activity.startActivity(intent);
        }

        return wiadomosc;
    }

    public String laczenie(){
        String newEmail = email.getText().toString();
        String requestURL = "http://192.168.137.1:8000/api/change_mail/";
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
                    .appendQueryParameter("token", GlobalValue.getTokenGlobal())
                    .appendQueryParameter("email", newEmail);

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

    private boolean isValidEmailAddress(String email) {  // sprawdzenie czy wprowadzono poprawny adres email
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }
}


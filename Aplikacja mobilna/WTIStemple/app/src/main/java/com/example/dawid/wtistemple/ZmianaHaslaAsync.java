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
import android.widget.Toast;

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

public class ZmianaHaslaAsync extends AsyncTask<String, String, String> {
    private boolean wykonano = false;
    private Activity activity;
    private EditText aktualneHaslo, noweHaslo, noweHaslo1;
    public ZmianaHaslaAsync(Activity activity)
    {
        this.activity = activity;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        aktualneHaslo = (EditText) activity.findViewById(R.id.aktualneHasloET);
        noweHaslo = (EditText) activity.findViewById(R.id.noweHasloET);
        noweHaslo1 = (EditText) activity.findViewById(R.id.noweHaslo1ET);
    }


    @Override
    protected String doInBackground(String... params) {
        return sprawdzenieDanych();

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(wykonano== true)
        {
            Toast.makeText(activity, "Zmieniono hasło do konta", Toast.LENGTH_SHORT).show();
        }
    }



    private String sprawdzenieDanych() {
        String noweHasl =noweHaslo.getText().toString();
        String noweHasl1 = noweHaslo1.getText().toString();
        String wiadomosc = "";
        if(!noweHasl.equals(noweHasl1))
        {
            wiadomosc = "Wpisane hasła nie są takie same";
            Snackbar.make(activity.getCurrentFocus(), wiadomosc, Snackbar.LENGTH_LONG).show();
            return wiadomosc;
        }

        String status = "";
        String wynik = laczenie();


        if (wynik != null) {
            try {
                JSONObject jsonObj = new JSONObject(wynik);
                JSONObject objectjso = jsonObj.getJSONObject("password");
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
            wykonano = true;
            Intent intent = new Intent(activity, menuActivity.class);
            activity.startActivity(intent);
        }

        return wiadomosc;
    }

    public String laczenie(){
        String aktualneHasl = aktualneHaslo.getText().toString();
        String noweHasl =noweHaslo.getText().toString();
        String requestURL = "http://"+GlobalValue.ipAdres+"/api/change_password/";
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
                    .appendQueryParameter("old_password", aktualneHasl)
                    .appendQueryParameter("new_pass", noweHasl);

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


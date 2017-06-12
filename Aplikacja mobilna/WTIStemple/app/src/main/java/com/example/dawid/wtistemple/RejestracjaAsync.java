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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Dawid on 29.04.2017.
 */

public class RejestracjaAsync extends AsyncTask<Void, Void, String> {

    private Activity activity;
    private ProgressBar progressBar;
    private EditText login, email, haslo, phaslo;

    public RejestracjaAsync(Activity activity)
    {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar = (ProgressBar) activity.findViewById(R.id.rejestracjaPB);
        progressBar.setVisibility(View.VISIBLE);
        login = (EditText) activity.findViewById(R.id.loginET);
        email = (EditText) activity.findViewById(R.id.emailET);
        haslo = (EditText) activity.findViewById(R.id.hasloET);
        phaslo = (EditText) activity.findViewById(R.id.phasloET);

    }

    @Override
    protected String doInBackground(Void... params) {
        return sprawdzenieDanych();

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        progressBar.setVisibility(View.INVISIBLE);

    }

    private String sprawdzenieDanych()
    {
        String wiadomosc = "";
        String hasl = haslo.getText().toString();
        String phasl = phaslo.getText().toString();
        String emai = email.getText().toString();
        String logi = login.getText().toString();
        Boolean fHaslo = true, fEmail = true;

        if(hasl.isEmpty() || phasl.isEmpty() || emai.isEmpty() || logi.isEmpty() )
        {
            wiadomosc = wiadomosc + "Wszystkie pola muszą być uzupełnione";
            Snackbar.make(activity.getCurrentFocus(), wiadomosc, Snackbar.LENGTH_LONG).show();
            return wiadomosc;
        }

        if(!Objects.equals(hasl, phasl))
        {
            wiadomosc = wiadomosc + "Wpisane hasła nie są takie same";
            fHaslo = false;
        }

        if(!isValidEmailAddress(emai))
        {
            wiadomosc = wiadomosc + "\nWpisany e-mail jest niepoprawny";
            fEmail = false;
        }

        if(fHaslo && fEmail)
        {
            String wynik = laczenie();
            String status ="";
            if (wynik != null) {
                try {
                    JSONObject jsonObj = new JSONObject(wynik);
                    JSONObject objectjso = jsonObj.getJSONObject("register");
                    status = objectjso.getString("status");



                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (status.equals("Username exists") || status.length() > 3) {
                wiadomosc = "Taki użytkownik istnieje";
                Snackbar.make(activity.getCurrentFocus(), wiadomosc, Snackbar.LENGTH_LONG).show();
                return wiadomosc;
            } else {

                wiadomosc = "Konto zostało zarejestrowane. Link aktywacyjny został wysłany na e-mail.";
                Snackbar.make(activity.getCurrentFocus(), wiadomosc, Snackbar.LENGTH_LONG).show();
                return wiadomosc;
            }

        }
        else
        {
            Snackbar.make(activity.getCurrentFocus(), wiadomosc, Snackbar.LENGTH_LONG).show();
        }

        return wiadomosc;
    }
    private boolean isValidEmailAddress(String email) {  // sprawdzenie czy wprowadzono poprawny adres email
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    public String laczenie(){
        String requestURL = "http://"+GlobalValue.ipAdres+"/api/register/";
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
                    .appendQueryParameter("password", haslo.getText().toString())
                    .appendQueryParameter("email", email.getText().toString());

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


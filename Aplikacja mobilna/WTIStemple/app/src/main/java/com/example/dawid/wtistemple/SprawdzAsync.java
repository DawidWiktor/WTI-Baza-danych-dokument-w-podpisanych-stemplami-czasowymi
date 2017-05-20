package com.example.dawid.wtistemple;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Dawid on 08.05.2017.
 */

public class SprawdzAsync extends AsyncTask<String, String, String> {
    private Activity activity;
    private ProgressBar progressBar;

    public SprawdzAsync(Activity activity)
    {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar = (ProgressBar) activity.findViewById(R.id.menuPB);
        progressBar.setVisibility(View.VISIBLE);


    }
    @Override
    protected String doInBackground(String... params) {
        return sprawdz();

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        progressBar.setVisibility(View.INVISIBLE);

    }

    public  String sprawdz(){
        String wynik = "";
        wynik = aba();

        //odczytanie jsona
        if (wynik != null) {
            try {
                JSONObject jsonObj = new JSONObject(wynik);
                JSONObject token = jsonObj.getJSONObject("login");
                String tok = token.getString("token");
                Snackbar.make(activity.getCurrentFocus(), tok, Snackbar.LENGTH_INDEFINITE).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return wynik;
    }

    public String aba(){
        String requestURL = "http://192.168.137.1:8000/api/login/";
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
                    .appendQueryParameter("username", "Dawid")
                    .appendQueryParameter("password", "dawid1234");

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

       // Snackbar.make(activity.getCurrentFocus(), response, Snackbar.LENGTH_LONG).show();
        return response;
    }

}

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

import org.json.JSONArray;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

import static android.R.attr.data;
import static android.R.attr.theme;

/**
 * Created by Dawid on 29.04.2017.
 */

public class ArchiwumAsync extends AsyncTask<String, String, String> {

    private Activity activity;


    public ArchiwumAsync(Activity activity)
    {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }


    @Override
    protected String doInBackground(String... params) {
        return sprawdzenieDanych();

    }



    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

    }

    private String sprawdzenieDanych(){
        String wiadomosc = "";
        wiadomosc = laczenie();
        SzczegolyDokumentow[] listDokumenty;

        if(!wiadomosc.equals(""));
        {
            JSONObject jsonObj = null;
            try {
                jsonObj = new JSONObject(wiadomosc);
                JSONArray docs = jsonObj.getJSONArray("docs");
                listDokumenty = new SzczegolyDokumentow[docs.length()];
                for(int i = 0; i < docs.length(); i++)
                {
                    JSONObject objectJS = docs.getJSONObject(i);
                    int ID = objectJS.getInt("id");
                    String nazwa = objectJS.getString("nazwa");
                    String Temptimestamp = objectJS.getString("timestamp");
                    String timestamp[] = Temptimestamp.split("\\.");
                    String autor = objectJS.getString("autor");
                    String linkPobierz = objectJS.getString("pobierz");
                    SzczegolyDokumentow dokument = new SzczegolyDokumentow(ID, nazwa, timestamp[0], autor, linkPobierz);
                    listDokumenty[i]=  dokument;
                    GlobalValue.listaArchiwum.add(dokument);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return wiadomosc;
    }

    public String laczenie(){
        String requestURL = "http://"+GlobalValue.ipAdres+"/api/archives/";
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
                    .appendQueryParameter("token", GlobalValue.getTokenGlobal());

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








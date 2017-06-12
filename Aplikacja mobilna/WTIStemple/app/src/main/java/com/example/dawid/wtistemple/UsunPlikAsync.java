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

public class UsunPlikAsync extends AsyncTask<String, String, String> {
    boolean cofnij = false;
    private Activity activity;

    public UsunPlikAsync(Activity activity)
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
        if(cofnij == true) {
            Toast.makeText(activity, "Plik został usunięty", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(activity, ArchwiwumActivity.class);
            activity.startActivity(intent);
            ((Activity) activity).finish();
        }

    }

    private String sprawdzenieDanych(){
        String wiadomosc = laczenie();



        if(wiadomosc == null)
        {
            wiadomosc = "Błąd w nawiązywaniu połączenia";
            Snackbar.make(activity.getCurrentFocus(), wiadomosc, Snackbar.LENGTH_LONG).show();
        }
        else {
            String status = "";
            //odczytanie jsona
            if (wiadomosc != null) {
                try {
                    JSONObject jsonObj = new JSONObject(wiadomosc);
                    JSONObject objectjso = jsonObj.getJSONObject("del");
                    status = objectjso.getString("status");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if(!status.equals("error"))
            {
                GlobalValue.listaArchiwum.remove(GlobalValue.WybranyDokument);
                cofnij = true;
            }else
            {
                wiadomosc = "Błąd w trakcie usuwania pliku";
                Snackbar.make(activity.getCurrentFocus(), wiadomosc, Snackbar.LENGTH_LONG).show();
            }

        }
        return wiadomosc;
    }

    public String laczenie(){
        String requestURL = "http://"+GlobalValue.ipAdres+"/api/del_file/";
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

            Integer id = GlobalValue.listaArchiwum.get(GlobalValue.WybranyDokument).id;

            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("token", GlobalValue.getTokenGlobal())
                    .appendQueryParameter("file_id", id.toString());

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


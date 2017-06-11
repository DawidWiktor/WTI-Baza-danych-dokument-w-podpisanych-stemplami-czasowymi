package com.example.dawid.wtistemple;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
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
import java.io.File;
import java.io.FileOutputStream;
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

public class PobieraniePlikuAsync extends AsyncTask<String, String, String> {
    private boolean pobrano = false;
    private String sciezka = "";
    private String rozszerzenie ="";
    private Activity activity;

    public PobieraniePlikuAsync(Activity activity)
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
        if(pobrano == true)
        {
            Toast.makeText(activity, "Plik został pobrany w\n"+ sciezka, Toast.LENGTH_SHORT).show();
        }

    }



    private String sprawdzenieDanych(){
        String nazwaPliku = GlobalValue.listaArchiwum.get(GlobalValue.WybranyDokument).nazwa;
        String[] splitNazwa = nazwaPliku.split("\\.");
        rozszerzenie = splitNazwa[splitNazwa.length-1];
        String wiadomosc = laczenie();


        return wiadomosc;
    }

    public String laczenie(){
        String requestURL = "http://" + GlobalValue.listaArchiwum.get(GlobalValue.WybranyDokument).linkPobierz;
        InputStream input = null;
        OutputStream output = null;
        URL url;
        String response = "";
        try {
            url = new URL(requestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
                sciezka = Environment.getExternalStorageDirectory() + "/" + GlobalValue.listaArchiwum.get(GlobalValue.WybranyDokument).nazwa + "." + rozszerzenie;
                input = conn.getInputStream();
                output = new FileOutputStream(new File(sciezka));
                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....

                    output.write(data, 0, count);
                }
                output.close();
                input.close();
                pobrano = true;

        } catch (Exception e) {
            e.printStackTrace();
        }


        return response;
    }
}


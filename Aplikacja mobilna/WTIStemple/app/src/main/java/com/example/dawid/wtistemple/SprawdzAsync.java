package com.example.dawid.wtistemple;

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

public class SprawdzAsync extends AsyncTask<String, Void, String> {
    private boolean przejscie = false;
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
        return wgrajPlik(params[0]);

    }

    @Override
    protected void onPostExecute(String s) {

        progressBar.setVisibility(View.INVISIBLE);
        if(przejscie == true) {
            Intent intent = new Intent(activity, SzczegolySprawdzonegoPlikuActivity.class);
            activity.startActivity(intent);
            ((Activity) activity).finish();
        }

    }

    private String wgrajPlik(String plikPath) {
        String wiadomosc = "";
        String wynik = upload(plikPath);
        String status = "";
        if (!wynik.equals("")) {
            JSONObject jsonObj = null;
            try {
                jsonObj = new JSONObject(wynik);

                String nazwaPliku = jsonObj.getString("nazwa");
                int id = Integer.parseInt(jsonObj.getString("id"));
                String Temptimestamp = jsonObj.getString("timestamp");
                String timestamp[] = Temptimestamp.split("\\.");
                String autor = jsonObj.getString("autor");

                GlobalValue.sprawdzonyPlik = new SzczegolyDokumentow(id, nazwaPliku, timestamp[0], autor, "link");
                przejscie = true;
                return wiadomosc;

            } catch (JSONException e) {
                e.printStackTrace();
            }


            try {
                jsonObj = new JSONObject(wynik);
                if (jsonObj.isNull("magnet")) {
                    Log.d("globbbb", " magneeeet");
                    JSONObject objectjso = jsonObj.getJSONObject("magnet");
                    Log.d("maaaadasdasdasd", "Asdasdasd");
                    status = objectjso.getString("status");
                    if (status.equals("error")) {
                        wiadomosc = "Taki plik nie istnieje";
                        Snackbar.make(activity.getCurrentFocus(), wiadomosc, Snackbar.LENGTH_LONG).show();
                        return wiadomosc;
                    } else if (status.equals("file not exists")) {
                        wiadomosc = "Taki plik już istnieje:\n" + plikPath;
                        Snackbar snackbar = Snackbar.make(activity.getCurrentFocus(), wiadomosc, Snackbar.LENGTH_INDEFINITE);
                        View snackbarView = snackbar.getView();
                        TextView tv = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                        tv.setMaxLines(30);
                        snackbar.show();
                        return wiadomosc;
                    }
                    if (status.equals("bad sign")) {
                        wiadomosc = "Złe znaki:\n" + plikPath;
                        Snackbar snackbar = Snackbar.make(activity.getCurrentFocus(), wiadomosc, Snackbar.LENGTH_INDEFINITE);
                        View snackbarView = snackbar.getView();
                        TextView tv = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                        tv.setMaxLines(30);
                        snackbar.show();
                        return wiadomosc;
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("globbbb", "cosss");


            return wiadomosc;
        }
        return wiadomosc;
    }
    public static String upload(String pathFile){
        String charset = "UTF-8";
        File uploadFile1 = new File(pathFile);
        String wynik = "";
        String requestURL = "http://"+GlobalValue.ipAdres+"/api/check_magnet/";

        try {
            MultipartUtility multipart = new MultipartUtility(requestURL, charset);
            multipart.addFormField("token", GlobalValue.getTokenGlobal());
            multipart.addFilePart("file", uploadFile1);

            List<String> response = multipart.finish();
            for (String line : response) {
                wynik = wynik + line;
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }
        return  wynik;
    }
}

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
import java.util.Objects;

import javax.crypto.KeyAgreement;
import javax.crypto.spec.DHParameterSpec;


/**
 * Created by Dawid on 29.04.2017.
 */

public class WgrywanieAsync extends AsyncTask<String, Void, String> {

    private Activity activity;
    private ProgressBar progressBar;

    public WgrywanieAsync(Activity activity)
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
        super.onPostExecute(s);
        progressBar.setVisibility(View.INVISIBLE);

    }

    private String wgrajPlik(String plikPath)
    {
        String wiadomosc = "";

        String haszPliku = "";

        try {
            haszPliku = AlgorytmSHA256.hashFile(plikPath);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        Snackbar snackbar =  Snackbar.make(activity.getCurrentFocus(), "hasz pliku: " + haszPliku,Snackbar.LENGTH_INDEFINITE);
        View snackbarView = snackbar.getView();
        TextView tv= (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setMaxLines(30);
        snackbar.show();
        // TODO: polaczenie z serwerem

        return wiadomosc;
    }








}



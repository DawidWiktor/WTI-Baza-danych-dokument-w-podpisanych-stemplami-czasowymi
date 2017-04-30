package com.example.dawid.wtistemple;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Objects;

import static android.R.attr.data;

/**
 * Created by Dawid on 29.04.2017.
 */

public class LogowanieAsync extends AsyncTask<Void, Void, String> {

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
    protected String doInBackground(Void... params) {
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

        //timetTest();
        AlgorytmPBKDF2 alg = new AlgorytmPBKDF2();
        String a = "nic nie ma";
        try {
            a = alg.test();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        Log.d("haslo", a);
        String logi = login.getText().toString();
        String hasl = haslo.getText().toString();
        if(logi.isEmpty() || hasl.isEmpty())
        {
            wiadomosc = wiadomosc + "Wszystkie pola muszą być uzupełnione";
            Snackbar.make(activity.getCurrentFocus(), wiadomosc, Snackbar.LENGTH_LONG).show();
        }
        else{

            Intent intent = new Intent(activity, menuActivity.class);
            activity.startActivity(intent);


        }
        /*String h = haslo.getText().toString();
        String ph = phaslo.getText().toString();
        String em = email.getText().toString();
        Boolean fHaslo = true, fEmail = true;
        Log.d("funkcja", "funkcja");
        timetTest();

        if(!Objects.equals(h, ph))
        {
            wiadomosc = wiadomosc + "Wpisane hasła nie są takie same";
            fHaslo = false;
        }

        if(!isValidEmailAddress(em))
        {
            wiadomosc = wiadomosc + "\nWpisany e-mail jest niepoprawny";
            fEmail = false;
        }

        if(fHaslo && fEmail)
        {
            Intent intent = new Intent(activity, menuActivity.class);
            activity.startActivity(intent);
        }
        else
        {
            Snackbar.make(activity.getCurrentFocus(), wiadomosc, Snackbar.LENGTH_LONG).show();
        }*/

        return wiadomosc;
    }


    public void timetTest()
    {
        Log.d("cos", "cos");
        for(int i = 0; i < 80000; i++) {
            long a = 958643 * 354861 * 4315 / 5132 * 513 + 13 + 88123 - 588442 * 3;
            long b = 958643 * 354861 * 4315 / 5132 * 513 + 13 + 88123 - 588442 * 3;
            long c = 958643 * 354861 * 4315 / 5132 * 513 + 13 + 88123 - 588442 * 3;
            long d = 958643 * 354861 * 4315 / 5132 * 513 + 13 + 88123 - 588442 * 3;
            long e = 958643 * 354861 * 4315 / 5132 * 513 + 13 + 88123 - 588442 * 3;
            long f = 958643 * 354861 * 4315 / 5132 * 513 + 13 + 88123 - 588442 * 3;
            long g = 958643 * 354861 * 4315 / 5132 * 513 + 13 + 88123 - 588442 * 3;
            long h = 958643 * 354861 * 4315 / 5132 * 513 + 13 + 88123 - 588442 * 3;
            long z = 958643 * 354861 * 4315 / 5132 * 513 + 13 + 88123 - 588442 * 3;
            long j = 958643 * 354861 * 4315 / 5132 * 513 + 13 + 88123 - 588442 * 3;
            long k = 958643 * 354861 * 4315 / 5132 * 513 + 13 + 88123 - 588442 * 3;
            long l = 958643 * 354861 * 4315 / 5132 * 513 + 13 + 88123 - 588442 * 3;


            long w = a - b + c + d - e + f - g - j + k;

            String plaintext = "your text here";
            MessageDigest m = null;
            try {
                m = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e1) {
                e1.printStackTrace();
            }
            m.reset();
            m.update(plaintext.getBytes());
            byte[] digest = m.digest();
            BigInteger bigInt = new BigInteger(1, digest);
            String hashtext = bigInt.toString(16);
// Now we need to zero pad it if you actually want the full 32 chars.
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
        }


    }
}


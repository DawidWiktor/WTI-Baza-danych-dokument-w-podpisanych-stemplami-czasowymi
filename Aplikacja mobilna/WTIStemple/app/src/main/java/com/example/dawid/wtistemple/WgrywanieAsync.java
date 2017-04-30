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

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;


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
timetTest();

        // TODO: polaczenie z serwerem

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



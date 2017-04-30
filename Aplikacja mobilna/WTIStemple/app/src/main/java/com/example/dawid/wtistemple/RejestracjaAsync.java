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
import java.util.Objects;

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
        Log.d("funkcja", "funkcja");
        timetTest();

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
            Intent intent = new Intent(activity, menuActivity.class);
            activity.startActivity(intent);
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


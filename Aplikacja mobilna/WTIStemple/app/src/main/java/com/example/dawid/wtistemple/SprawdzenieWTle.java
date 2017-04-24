package com.example.dawid.wtistemple;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import java.util.Objects;

/**
 * Created by Dawid on 24.04.2017.
 */

public class SprawdzenieWTle extends AsyncTask<Void, Void, String>  {

    private Activity activity;
    private ProgressBar progressBar;
    private EditText login, email, haslo, phaslo;

    public SprawdzenieWTle(Activity activity)
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
        String h = haslo.getText().toString();
        String ph = phaslo.getText().toString();
        String em = email.getText().toString();
        Boolean fHaslo = true, fEmail = true;

        if(!Objects.equals(h, ph)) { // sprawdzenie czy hasla sa identyczne oraz email
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
        }
        return wiadomosc;
    }
    private boolean isValidEmailAddress(String email) {  // sprawdzenie czy wprowadzono poprawny adres email
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }
}

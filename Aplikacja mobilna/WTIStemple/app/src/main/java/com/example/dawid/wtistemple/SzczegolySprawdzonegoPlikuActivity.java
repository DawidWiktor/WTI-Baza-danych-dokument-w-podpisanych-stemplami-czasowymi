package com.example.dawid.wtistemple;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class SzczegolySprawdzonegoPlikuActivity extends AppCompatActivity {
    TextView nazwa, autor, timestamp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_szczegoly_sprawdzonego_pliku);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        nazwa = (TextView) findViewById(R.id.nazwaSArtykuluTV);
        timestamp = (TextView) findViewById(R.id.timestampSTV);
        autor = (TextView) findViewById(R.id.autorNazwaSTV);
        nazwa.setText(GlobalValue.sprawdzonyPlik.nazwa);
        autor.setText(GlobalValue.sprawdzonyPlik.autor);
        timestamp.setText(GlobalValue.sprawdzonyPlik.timestamp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void pobierzClick(View view) {
        new PobieraniePlikuZMagnetycznegoAsync(this).execute(GlobalValue.PlikPath);
    }
}

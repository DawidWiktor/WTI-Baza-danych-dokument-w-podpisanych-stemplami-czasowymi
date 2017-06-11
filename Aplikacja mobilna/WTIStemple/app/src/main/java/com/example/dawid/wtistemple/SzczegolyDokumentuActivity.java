package com.example.dawid.wtistemple;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;

public class SzczegolyDokumentuActivity extends AppCompatActivity {
    TextView nazwa, timestamp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_szczegoly_dokumentu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        nazwa = (TextView) findViewById(R.id.nazwaArtykuluTV);
        timestamp = (TextView) findViewById(R.id.timestampTV);
        nazwa.setText(GlobalValue.listaArchiwum.get(GlobalValue.WybranyDokument).nazwa);
        timestamp.setText(GlobalValue.listaArchiwum.get(GlobalValue.WybranyDokument).timestamp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


    public void pobierzClick(View view) {

        new PobieraniePlikuAsync(this).execute("aba");


    }
    public void pobierzPlikMagnetycznyClick(View view) {

        new PobieraniePlikuMagnetycznegoAsync(this).execute("aba");



    }
    public void usunClick(View view) {
        new UsunPlikAsync(this).execute("aba");

    }


}

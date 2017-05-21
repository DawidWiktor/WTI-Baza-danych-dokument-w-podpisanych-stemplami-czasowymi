package com.example.dawid.wtistemple;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class MojProfilActivity extends AppCompatActivity {


    public Button zmienHasl, zmienEmai, usunKont;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moj_profil);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        zmienEmai = (Button) findViewById(R.id.zmienEmailBTN);
        zmienHasl = (Button) findViewById(R.id.zmienHasloBTN);
        usunKont = (Button) findViewById(R.id.usunKontoBTN);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void zmienHaslo(View view) {
    }

    public void zmienEmail(View view) {
        new ZmianaEmailAsync(this).execute();
    }

    public void usunKonto(View view) {
    }
}

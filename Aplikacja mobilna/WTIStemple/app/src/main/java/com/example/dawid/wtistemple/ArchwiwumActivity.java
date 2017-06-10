package com.example.dawid.wtistemple;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

public class ArchwiwumActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archwiwum);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




        final String elementyListy[] = new String[GlobalValue.listaArchiwum.size()];
        for(int i = 0; i < elementyListy.length; i++) {
            elementyListy[i] = GlobalValue.listaArchiwum.get(i).nazwa;
        }
        ListView prosta_lista = (ListView) findViewById(R.id.archiwumLV);
        ArrayAdapter adapter_listy = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, elementyListy);
        prosta_lista.setAdapter(adapter_listy);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        prosta_lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView,
                                    View view, int position, long rowId) {

                GlobalValue.WybranyDokument = position;
                Intent intent =  new Intent(ArchwiwumActivity.this, SzczegolyDokumentuActivity.class);
                startActivity(intent);

            }
        });
    }

    public void click(View view){
        String elementyListy[] = new String[GlobalValue.listaArchiwum.size()];
        for(int i = 0; i < elementyListy.length; i++) {
            elementyListy[i] = GlobalValue.listaArchiwum.get(i).nazwa;
        }
        ListView prosta_lista = (ListView) findViewById(R.id.archiwumLV);
        ArrayAdapter adapter_listy = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, elementyListy);
        prosta_lista.setAdapter(adapter_listy);
    }








}





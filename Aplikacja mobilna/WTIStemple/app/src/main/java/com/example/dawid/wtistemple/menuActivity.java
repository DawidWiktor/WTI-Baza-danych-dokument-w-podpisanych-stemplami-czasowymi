package com.example.dawid.wtistemple;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class menuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public Button wybierzPlik, wgraj, sprawdz;
    private String plikPath = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        wybierzPlik = (Button) findViewById(R.id.wybierzPlikBTN);
        wgraj = (Button) findViewById(R.id.wgrajBTN);
        sprawdz = (Button) findViewById(R.id.sprawdzBTN);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;
        if (id == R.id.nav_camera) {

            intent = new Intent(menuActivity.this, ArchwiwumActivity.class);
            startActivity(intent);
        }
        else if(id == R.id.nav_view){
            intent = new Intent(menuActivity.this, MojProfilActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_send) {
            new WylogowanieAsync(this).execute();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

/////////////////////////////////////////file manager////////////////////////////////////////////////

    private static final String TAG = "MainActivity";

    private static final int REQUEST_CODE = 6384; // onActivityResult request

    private void showChooser() {
        // Use the GET_CONTENT intent from the utility class
        Intent target = FileUtils.createGetContentIntent();
        // Create the chooser Intent
        Intent intent = Intent.createChooser(
                target, getString(R.string.chooser_title));
        try {
            startActivityForResult(intent, REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            // The reason for the existence of aFileChooser
        }
    }

    public void wybierzPlikClcik(View v)
    {
        showChooser();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_CODE:
                // If the file selection was successful
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        // Get the URI of the selected file
                        final Uri uri = data.getData();
                        Log.i(TAG, "Uri = " + uri.toString());
                        try {
                            // Get the file path from the URI
                            final String path = FileUtils.getPath(this, uri);
                            plikPath = path;
                            Snackbar snackbar =  Snackbar.make(getCurrentFocus(), "Wybrany plik: " + plikPath,Snackbar.LENGTH_INDEFINITE);
                            View snackbarView = snackbar.getView();
                            TextView tv= (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                            tv.setMaxLines(6);
                            snackbar.show();
                        } catch (Exception e) {
                            //Log.e("FileSelectorTestActivity", "File select error", e);
                        }
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void wgrajClick(View v)
    {
        new WgrywanieAsync(this).execute(plikPath);
    }

    public void sprawdzClick(View w)
    {
        Snackbar.make(getCurrentFocus(), GlobalValue.getTokenGlobal(), Snackbar.LENGTH_LONG).show();
       // new SprawdzAsync(this).execute();
        //new JsonTask(this).execute("http://192.168.137.1:8000/api/test_get/");
    }
}

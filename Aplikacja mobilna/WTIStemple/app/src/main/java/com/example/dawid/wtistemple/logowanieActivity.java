package com.example.dawid.wtistemple;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class logowanieActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logowanie);
    }

    public void click(View view) {
        Intent intent;

        switch (view.getId())
        {
            case R.id.logowanieBtn:
                new LogowanieAsync(this).execute();
                break;
            case R.id.rejestracjaBtn:
                intent = new Intent(logowanieActivity.this, rejestracjaActivity.class);
                startActivity(intent);
                break;
        }
    }
}

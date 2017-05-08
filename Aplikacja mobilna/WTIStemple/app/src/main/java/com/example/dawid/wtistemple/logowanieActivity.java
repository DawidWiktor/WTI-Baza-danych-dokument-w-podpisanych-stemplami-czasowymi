package com.example.dawid.wtistemple;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class logowanieActivity extends AppCompatActivity {
    private EditText login, haslo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logowanie);
        login = (EditText) findViewById(R.id.loginLogowanie);

        haslo = (EditText) findViewById(R.id.hasloLogowanie);

    }

    public void click(View view) {
        Intent intent;
        GlobalValue GlobValue = ((GlobalValue) getApplicationContext());
        switch (view.getId())
        {
            case R.id.logowanieBtn:
                GlobValue.setPasswordGlobal(haslo.getText().toString());
                GlobValue.setLoginGlobal(login.getText().toString());

                new LogowanieAsync(this).execute("aba");
                break;
            case R.id.rejestracjaBtn:
                intent = new Intent(logowanieActivity.this, rejestracjaActivity.class);
                startActivity(intent);
                break;
        }
    }
}

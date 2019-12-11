package com.example.teamprojekt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class AutonomerModusActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autonomer_modus);
        getSupportActionBar().setTitle("Autonomer Modus");
    }
}

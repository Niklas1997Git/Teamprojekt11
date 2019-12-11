package com.example.teamprojekt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class HauptmenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hauptmenu);
        getSupportActionBar().setTitle("Hauptmenü");
    }

    /** Called when the user taps the Einstellungen button */
    public void zuEinstellungen(View view) {
        Intent intent = new Intent(this, EinstellungenActivity.class);
        startActivity(intent);
    }

    /** Called when the user taps the FerngesteuerterModus button */
    public void zuFerngesteuerterModus(View view) {
        if(EinstellungenActivity.googleDriveHelper == null){
            Toast.makeText(this, "Bevor Sie eine Aufnahme starten können, müssen Sie sich in den Einstellungen bei Google Drive anmelden", Toast.LENGTH_LONG).show();
        }
        Intent intent = new Intent(this, FerngesteuerterModusActivity.class);
        startActivity(intent);
    }

    /** Called when the user taps the AutonomerModus button */
    public void zuAutonomerModus(View view) {
        Intent intent = new Intent(this, AutonomerModusActivity.class);
        startActivity(intent);
    }
}

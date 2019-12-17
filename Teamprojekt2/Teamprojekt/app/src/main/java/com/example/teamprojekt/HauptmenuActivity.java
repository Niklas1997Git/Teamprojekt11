package com.example.teamprojekt;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

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
        if(isLogedIn()){
            Intent intent = new Intent(this, FerngesteuerterModusActivity.class);
            startActivity(intent);
        }else{
            Toast.makeText(HauptmenuActivity.this,
                    "Sie müssen sich erst in den Einstellungen bei Google Drive anmelden", Toast.LENGTH_LONG)
                    .show();
        }
    }

    /** Called when the user taps the AutonomerModus button */
    public void zuAutonomerModus(View view) {
        if(isLogedIn()){
            Intent intent = new Intent(this, AutonomerModusActivity.class);
            startActivity(intent);
        }else{
            Toast.makeText(HauptmenuActivity.this,
                    "Sie müssen sich erst in den Einstellungen bei Google Drive anmelden", Toast.LENGTH_LONG)
                    .show();
        }
    }


    private boolean isLogedIn(){
        GoogleSignInAccount alreadyloggedAccount = GoogleSignIn.getLastSignedInAccount(this);

        if (alreadyloggedAccount != null) {
            return true;
        } else {
            return false;
        }
    }

}

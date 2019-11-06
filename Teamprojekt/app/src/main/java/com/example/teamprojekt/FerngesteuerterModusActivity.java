package com.example.teamprojekt;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class FerngesteuerterModusActivity extends AppCompatActivity {

    Camera camera;
    FrameLayout frameLayout;
    ShowCamera showCamera;
    TakePictures takePictures;
    boolean aufnahme;
    TextView bilderAufnehmenText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ferngesteuerter_modus);
        getSupportActionBar().setTitle("Ferngesteuerter Modus");

        bilderAufnehmenText = findViewById(R.id.textView3);
        frameLayout = findViewById((R.id.frameLayout));

        //open Camera
        camera = Camera.open();
        takePictures = new TakePictures(camera);

        //Preview wird angezeigt
        showCamera = new ShowCamera(this, camera);
        frameLayout.addView(showCamera);

        //takePictures();
    }


    private void takePictures(){
        //Wartet eine Sekunde bis der Code in run() wieder ausgeführt wird
        /*
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    if(aufnahme){
                    //nimmt Bild auf
                        takePictures.captureImage(frameLayout);
                    }
                }
            }, 1000);   // 1 seconds
        */
        takePictures.captureImage(frameLayout);
    }


    public void toggleAufnahme(View view){
        takePictures();
        /*
        aufnahme = !aufnahme;
        if(aufnahme){
            bilderAufnehmenText.setText("Bilder werden aufgenommen");
            takePictures();
        }else{
            bilderAufnehmenText.setText("Es werden keine Bilder aufgenommen");
        }
         */
    }



}

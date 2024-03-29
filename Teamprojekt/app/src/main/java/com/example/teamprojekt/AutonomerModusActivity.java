package com.example.teamprojekt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

public class AutonomerModusActivity extends AppCompatActivity {

    private Camera camera;
    private ShowCamera showCamera;
    private Button startStopButton;
    private FrameLayout cameraBildFramelayout;
    public ImageView capturePreview;
    private TakePicturesAutonom takePicturesAutonom;
    private boolean startStopFahren = false;
    private int anzahlBilder;
    Context context;


    private final String prefName = "MyPref";
    private final String pref_anzahlBilder = "anzahlBilder";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autonomer_modus);
        getSupportActionBar().setTitle("Autonomer Modus");

        context=this;
        //TODO
        anzahlBilder = this.getSharedPreferences(prefName, 0).getInt(pref_anzahlBilder, 10);

        cameraBildFramelayout = findViewById((R.id.frameLayoutAutonom));
        //capturePreview = findViewById(R.id.);
        startStopButton = findViewById(R.id.buttonStartStop);
        startStopButton.setText("Start");



        //open Camera
        camera = Camera.open();
        takePicturesAutonom = new TakePicturesAutonom(camera, this);

        //Preview wird angezeigt
        showCamera = new ShowCamera(this, camera);
        cameraBildFramelayout.addView(showCamera);
    }

    public void startStopFahren(View view){
        //Schaltet die Reihenaufnahme ein oder aus
        takePicturesAutonom.captureImageAutonom(cameraBildFramelayout);
        /*
        startStopFahren = !startStopFahren;
        if(startStopFahren){
            startStopButton.setText("Stop");
            autonomesFahren();
        }else {
            startStopButton.setText("Start");
        }
        */
    }

    private void autonomesFahren(){
        //Wartet eine Sekunde bis der Code in run() wieder ausgeführt wird
        Timer timer = new Timer();
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                if(startStopFahren){
                    takePicturesAutonom.captureImageAutonom(cameraBildFramelayout);
                }
            }
        }, 0, 1000l/ (long)anzahlBilder);
    }
}

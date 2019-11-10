package com.example.teamprojekt;

import androidx.appcompat.app.AppCompatActivity;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class FerngesteuerterModusActivity extends AppCompatActivity {

    Camera camera;
    FrameLayout frameLayout;
    ShowCamera showCamera;
    TakePicturesFerngesteuert takePicturesFerngesteuert;
    public static String datei_name;
    boolean reihenAufnahme = false;
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
        takePicturesFerngesteuert = new TakePicturesFerngesteuert(camera);

        //Preview wird angezeigt
        showCamera = new ShowCamera(this, camera);
        frameLayout.addView(showCamera);
    }


    private void takePictures(){
        //Wartet eine Sekunde bis der Code in run() wieder ausgeführt wird
        Timer timer = new Timer();
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                if(reihenAufnahme){
                    takePicturesFerngesteuert.captureImage(frameLayout);
                    String[] s = {"90", "50", "Time"};
                    saveJSONFile(s[0], s[1], s[2]);
                }
            }
        }, 0, 1000l/10l);
    }

    private void saveJSONFile(String steering, String throttle, String timestamp) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("steering", steering);
            jsonObject.put("throttle", throttle);
            jsonObject.put("timestamp", timestamp);
            jsonObject.put("image", datei_name+".jpg");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String state = Environment.getExternalStorageState();
        if(!state.equals(Environment.MEDIA_MOUNTED)){
            return;
        }else {
            //Ausgabe Order entspricht "GUI" im internen Speicher
            File folder_json = new File(Environment.getExternalStorageDirectory() + File.separator + "A_Project" + File.separator + "JSON");

            if (!folder_json.exists()) {
                folder_json.mkdirs();

            }
            try {
                File jsonFile = new File(folder_json, datei_name + ".json");
                FileWriter fileWriter = new FileWriter(jsonFile);
                fileWriter.write(jsonObject.toString());
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    public void toggleAufnahme(View view){
        //Schaltet die Reihenaufnahme ein oder aus
        reihenAufnahme = !reihenAufnahme;
        //Startet die Bildaufnahme
        takePictures();
    }




}

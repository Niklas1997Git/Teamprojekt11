package com.example.teamprojekt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
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

    Interpreter tflite;

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

        try{
            tflite = new Interpreter(loadModelFile());
        }catch(Exception ex){
            ex.printStackTrace();
        }

        //open Camera
        int camBackId = Camera.CameraInfo.CAMERA_FACING_BACK;
        camera = Camera.open(camBackId);
        Camera.Parameters params = camera.getParameters();
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        camera.setParameters(params);

        takePicturesAutonom = new TakePicturesAutonom(camera, this, tflite);

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

    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor = this.getAssets().openFd("Filename");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
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

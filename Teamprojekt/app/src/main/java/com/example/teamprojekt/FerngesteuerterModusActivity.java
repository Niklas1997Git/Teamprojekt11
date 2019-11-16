package com.example.teamprojekt;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FerngesteuerterModusActivity extends AppCompatActivity {

    Camera camera;
    FrameLayout frameLayout;
    ShowCamera showCamera;
    TakePicturesFerngesteuert takePicturesFerngesteuert;
    public static String datei_name = "";
    boolean reihenAufnahme = false;
    boolean abbrechen =false;
    TextView bilderAufnehmenText;

    private static final int BUFFER = 8192 ;
    int counter;
    Calendar kalender = Calendar.getInstance();
    SimpleDateFormat datumsformat = new SimpleDateFormat("dd.MM.yyyy_HH:mm:ss.SSS");

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
        //FerngesteuerterModusActivity.datei_name = datumsformat.format(kalender.getTime());

        counter=1;
        Timer timer = new Timer();
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                if(reihenAufnahme && !abbrechen){
                    System.out.println("Aufnahme " + counter++);
                    aufnahmen();
                    if(abbrechen){
                        reihenAufnahme = false;
                        abbrechen = false;
                        System.out.println("Aufnahme beenden");
                    }
                }
            }
        }, 0, 1000l/10l);


        /*
        new Handler().postDelayed(() -> zipFileAtPath(
                Environment.getExternalStorageDirectory() + File.separator + "A_Project",
                Environment.getExternalStorageDirectory() + File.separator + "A_Project.zip"),
                1000); // Millisecond 1000 = 1 sec

         */

    }

    private synchronized void aufnahmen(){
        //takePicturesFerngesteuert.captureImage(frameLayout);
        System.out.println("Bild aufgenommen");
        String[] s = {"90", "50", "Time"};
        //saveJSONFile(s[0], s[1], s[2]);
        System.out.println("JSON gespeichert");
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
        if(reihenAufnahme){
            abbrechen=true;
            reihenAufnahme = false;
            System.out.println("Aufnahme abbrechen");
        }else{
            reihenAufnahme = true;
            abbrechen = false;
            System.out.println("Aufnahme Beginn");
        }
        //Startet die Bildaufnahme
        takePictures();
    }



    public boolean zipFileAtPath(String sourcePath, String toLocation) {
        final int BUFFER = 2048;

        File sourceFile = new File(sourcePath);
        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(toLocation);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            if (sourceFile.isDirectory()) {
                zipSubFolder(out, sourceFile, sourceFile.getParent().length());
            } else {
                byte data[] = new byte[BUFFER];
                FileInputStream fi = new FileInputStream(sourcePath);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(getLastPathComponent(sourcePath));
                entry.setTime(sourceFile.lastModified()); // to keep modification time after unzipping
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        DeleteRecursive(new File(sourcePath));
        return true;
    }

    private void zipSubFolder(ZipOutputStream out, File folder,
                              int basePathLength) throws IOException {

        final int BUFFER = 2048;

        File[] fileList = folder.listFiles();
        BufferedInputStream origin = null;
        for (File file : fileList) {
            if (file.isDirectory()) {
                zipSubFolder(out, file, basePathLength);
            } else {
                byte data[] = new byte[BUFFER];
                String unmodifiedFilePath = file.getPath();
                String relativePath = unmodifiedFilePath
                        .substring(basePathLength);
                FileInputStream fi = new FileInputStream(unmodifiedFilePath);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(relativePath);
                entry.setTime(file.lastModified()); // to keep modification time after unzipping
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }
        }
    }

    public String getLastPathComponent(String filePath) {
        String[] segments = filePath.split("/");
        if (segments.length == 0)
            return "";
        String lastPathComponent = segments[segments.length - 1];
        return lastPathComponent;
    }

    void DeleteRecursive(File dir)
    {
        System.out.println("DELETEPREVIOUS TOP");
        //Log.d("DeleteRecursive", "DELETEPREVIOUS TOP" + dir.getPath());
        if (dir.isDirectory())
        {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++)
            {
                File temp = new File(dir, children[i]);
                if (temp.isDirectory())
                {
                    System.out.println("Recursive Call");
                    //Log.d("DeleteRecursive", "Recursive Call" + temp.getPath());
                    DeleteRecursive(temp);
                }
                else
                {
                    System.out.println("Delete File\"");
                    //Log.d("DeleteRecursive", "Delete File" + temp.getPath());
                    boolean b = temp.delete();
                    if (b == false)
                    {
                        System.out.println("DELETE FAIL");
                        //Log.d("DeleteRecursive", "DELETE FAIL");
                    }
                }
            }

        }
        dir.delete();
    }
}

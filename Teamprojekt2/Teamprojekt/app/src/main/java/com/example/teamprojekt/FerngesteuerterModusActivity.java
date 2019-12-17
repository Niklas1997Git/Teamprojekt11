package com.example.teamprojekt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.example.teamprojekt.EinstellungenActivity.googleDriveHelper;


public class FerngesteuerterModusActivity extends AppCompatActivity {

    private  int BOARD_PORT = 10001;
    private String prefName = "MyPref";
    private final String pref_automatischHochladen = "hochladen";
    private final String pref_boardPort = "boardPort";
    boolean automatischHochladen = true;
    Camera camera;
    FrameLayout frameLayout;
    ShowCamera showCamera;
    TakePicturesFerngesteuert takePicturesFerngesteuert;
    public static String datei_name = "";
    boolean reihenAufnahme = false;

    TextView bilderAufnehmenText;

    boolean abbrechen =false;

    String lenkwinkel_string;
    String geschwindigkeit_string;

    TextView lenkwinkelText;
    TextView geschwindigkeitText;
    TextView lenkrichtungText;

    Button bilderAufnehmenButton;

    SharedPreferences preferences;

    UDPClientListen clientListen;
    String nachricht ="";
    String[] nachrichtWerte = new String[2];
    GoogleSignInAccount account;
    private String applicationName = "Teamprojekt";

    Calendar kalender;
    SimpleDateFormat datumsformat = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss");
    int counter;
    final File folder_json = new File(Environment.getExternalStorageDirectory() + File.separator + "A_Project" + File.separator + "JSON");


    //Wenn der user fertig mit der Json Datei ist und die umbenennen will ,
    //dann setzt man die Variable auf true
    private boolean isUserFinish = false;


    private GoogleSignInAccount checkLogedIn(){
        GoogleSignInAccount alreadyloggedAccount = GoogleSignIn.getLastSignedInAccount(this);
        prefName ="MyPref" + alreadyloggedAccount.getId();
        preferences = getSharedPreferences(prefName, 0);
        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(FerngesteuerterModusActivity.this, Collections.singleton(DriveScopes.DRIVE_FILE));
        credential.setSelectedAccount(alreadyloggedAccount.getAccount());
        Drive googleDriveService = new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential)
                .setApplicationName(applicationName)
                .build();
        googleDriveHelper = new GoogleDriveHelper(googleDriveService, preferences);

        return alreadyloggedAccount;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ferngesteuerter_modus);
        account = checkLogedIn();
        System.out.println(account.getEmail());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Ferngesteuerter Modus");
        }

        BOARD_PORT = preferences.getInt(pref_boardPort, 10001);
        automatischHochladen = preferences.getBoolean(pref_automatischHochladen, true);

        lenkwinkelText = findViewById(R.id.textView8);
        geschwindigkeitText = findViewById(R.id.textView9);
        lenkrichtungText = findViewById(R.id.textView10);

        bilderAufnehmenButton = findViewById(R.id.button4);

        frameLayout = findViewById((R.id.frameLayout));

        lenkwinkelText.setText(R.string.Lenkwinkel);
        geschwindigkeitText.setText(R.string.Geschwindigkeit);
        lenkrichtungText.setText(R.string.Lenkrichtung);

        //open Camera
        int camBackId = Camera.CameraInfo.CAMERA_FACING_BACK;
        camera = Camera.open(camBackId);
        Camera.Parameters params = camera.getParameters();
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        camera.setParameters(params);

        takePicturesFerngesteuert = new TakePicturesFerngesteuert(camera, this);

        //Preview wird angezeigt
        showCamera = new ShowCamera(this, camera);
        frameLayout.addView(showCamera);


        clientListen = new UDPClientListen(BOARD_PORT, nachrichtWerte);
    }


    private void ferngesteuertesFahren(){
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
                        //bilderAufnehmenButton.setText(R.string.btn_bilderaufnahme_start_text);
                        System.out.println("Aufnahme beenden");
                    }
                }
            }
        }, 0, 1000L / 10L);



        new Handler().postDelayed(() -> zipFileAtPath(
                Environment.getExternalStorageDirectory() + File.separator + "A_Project",
                Environment.getExternalStorageDirectory() + File.separator + "A_Project.zip"),
                1000); // Millisecond 1000 = 1 sec

        if(automatischHochladen){
            uploadFile();
        }else{
            HochladenDialog hochladenDialog = new HochladenDialog();
            hochladenDialog.show(getSupportFragmentManager(), "Hochladen");
        }

        deleteRecursive(new File(Environment.getExternalStorageDirectory() + File.separator + "A_Project"));

    }

    public  void uploadFile(){
        ProgressDialog progressDialog = new ProgressDialog(FerngesteuerterModusActivity.this);
        progressDialog.setTitle("Uploading to Google Drive");
        progressDialog.setMessage("Please wait....");



        String filePath = Environment.getExternalStorageDirectory() + File.separator + "A_Project.zip";


        googleDriveHelper.createFileInFolder().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                progressDialog.dismiss();

                Toast.makeText(getApplicationContext(), "Uploaded Successfully", Toast.LENGTH_LONG).show();
            }
        })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Konnte nicht hochgeladen werden", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void setText(){
        runOnUiThread(() -> updateWerte(lenkwinkel_string, geschwindigkeit_string));
    }

    private synchronized void aufnahmen(){
        takePicturesFerngesteuert.captureImage(frameLayout);
        System.out.println("Bild aufgenommen");
        //Daten vom Board empfangen
        clientListen.run();
        //TODO UDP-Nachricht verarbeiten in String nachricht entahlten

        System.out.println(nachricht);
        //System.out.println(getWertJSON(nachricht,"steering"));
        lenkwinkel_string = nachrichtWerte[0];
        geschwindigkeit_string = nachrichtWerte[1];
        saveJSONFile(lenkwinkel_string, geschwindigkeit_string);
        /*
        //Teststring zur Erstellung einer json datei
        String[] s = {"90", "50", "Time"};
        //JSON-Datei erstellen und speichern
        saveJSONFile(s[0], s[1], s[2]);
        System.out.println("JSON gespeichert");
        lenkwinkel_string = s[0];
        geschwindigkeit_string = s[1];
         */
        setText();

    }

    private String getWertJSON(String json, String wert){
        System.out.println("_____________getWert_____________");
        System.out.println(json);
        String value = "";
        String[] eintraege = json.split(",");
        System.out.println(eintraege.toString());
        for(String eintrag: eintraege){
            System.out.println(eintrag);
            System.out.println("------------enthält wert nicht------------");
            if(eintrag.contains(wert)){
                System.out.println("------------enthält wert------------");
                String[] wertEintrag = eintrag.split(":");
                System.out.println(wertEintrag.toString());
                if(wertEintrag[1].contains("}")){
                    wertEintrag[1].replace("}","");
                    value = wertEintrag[1];
                }
                System.out.println("_____________getWert_____________");
                return  wertEintrag[1];
            }
        }
        System.out.println("_____________getWert_____________");
        return value;
    }

    //Updaten der Texte in der Szene
    private void updateWerte(String lenkwert, String geschwindigkeit){
        lenkwinkelText.setText("Lenkwinkel: " + lenkwert);
        geschwindigkeitText.setText("Geschwindigkeit: " + geschwindigkeit);
        /*
        int winkel = Integer.parseInt(lenkwert);
        if(winkel < 90){
            lenkrichtungText.setText("Lenkrichtung: Links");
        }else if(winkel > 90){
            lenkrichtungText.setText("Lenkrichtung: Rechts");
        }else{
            lenkrichtungText.setText("Lenkrichtung: Geradeaus");
        }

         */
    }


    //JSON Datei erstellen und speichern
    //TODO eventuell anpassen, je nachdem welche Daten vom Board übergeben werden
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
            //JSON Ordner erstellen, falls noch nicht vorhanden
            if (!folder_json.exists()) {
                folder_json.mkdirs();

            }
            try {
                //JSON-Datei speichern
                File jsonFile = new File(folder_json, datei_name + ".json");
                FileWriter fileWriter = new FileWriter(jsonFile);
                fileWriter.write(jsonObject.toString());
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    int jsonCounter=0;
    private void saveJSONFile(String steering, String throttle) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("steering", steering);
            jsonObject.put("throttle", throttle);
            //jsonObject.put("image", datei_name+".jpg");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String state = Environment.getExternalStorageState();
        if(!state.equals(Environment.MEDIA_MOUNTED)){
            return;
        }else {
            //JSON Ordner erstellen, falls noch nicht vorhanden
            if (!folder_json.exists()) {
                folder_json.mkdirs();

            }
            try {
                //JSON-Datei speichern
                File jsonFile = new File(folder_json, datei_name + ".json");
                //File jsonFile = new File(folder_json, jsonCounter + ".json");
                FileWriter fileWriter = new FileWriter(jsonFile);
                fileWriter.write(jsonObject.toString());
                System.out.println("gespeichert");
                //jsonCounter++;
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    //Aufnahme starten oder beenden
    public void toggleAufnahme(View view){
        //Schaltet die Reihenaufnahme ein oder aus
        if(reihenAufnahme){
            abbrechen=true;
            reihenAufnahme = false;
            bilderAufnehmenButton.setText(R.string.btn_bilderaufnahme_start_text);
            System.out.println("Aufnahme abbrechen");
        }else{
            reihenAufnahme = true;
            abbrechen = false;
            bilderAufnehmenButton.setText(R.string.btn_bilderaufnahme_stop_text);
            System.out.println("Aufnahme Beginn");
        }
        //Startet die Bildaufnahme
        ferngesteuertesFahren();
    }



    //.Zip datei erstellen
    public boolean zipFileAtPath(String sourcePath, String toLocation) {
        final int BUFFER = 2048;

        File sourceFile = new File(sourcePath);
        try {
            BufferedInputStream origin;
            FileOutputStream dest = new FileOutputStream(toLocation);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            if (sourceFile.isDirectory()) {
                zipSubFolder(out, sourceFile, Objects.requireNonNull(sourceFile.getParent()).length());
            } else {
                byte[] data = new byte[BUFFER];
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
        //DeleteRecursive(new File(sourcePath));
        return true;
    }

    private void zipSubFolder(ZipOutputStream out, File folder,
                              int basePathLength) throws IOException {

        final int BUFFER = 2048;

        File[] fileList = folder.listFiles();
        BufferedInputStream origin;
        if(fileList!=null){
            for (File file : fileList) {
                if (file.isDirectory()) {
                    zipSubFolder(out, file, basePathLength);
                } else {
                    byte[] data = new byte[BUFFER];
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
    }

    public String getLastPathComponent(String filePath) {
        String[] segments = filePath.split("/");
        if (segments.length == 0)
            return "";
        return segments[segments.length - 1];
    }


    //Ordner löschen
    void deleteRecursive(File dir)
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
                    deleteRecursive(temp);
                }
                else
                {
                    System.out.println("Delete File\"");
                    //Log.d("DeleteRecursive", "Delete File" + temp.getPath());
                    boolean b = temp.delete();
                    if (!b)
                    {
                        System.out.println("DELETE FAIL");
                        //Log.d("DeleteRecursive", "DELETE FAIL");
                    }
                }
            }

        }
        dir.delete();
    }


    /*-------------------------DATEI UMBENENNEN----------------------*/
    //Wir geben den User die Möglichkeit die Datei umzubennnen
    //Dabei sollen wir zuerst überprüfen, ob er damit fertig ist!!!!!
    // Wenn man testen will, könnte man schon die Varible auf true setzen. und die Methode renameFile()
    //an der richtigen Stelle aufrufen

    private void renameFile(){
        if(isUserFinish){
            createPopUp();
        }
    }


    //PopUp zur Dateiumbennenung
    private void createPopUp(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FerngesteuerterModusActivity.this);
        alertDialog.setTitle("Datei Umbenennen");
        alertDialog.setMessage("Geben Sie bitte einen neuen Namen ein");

        final EditText input = new EditText(FerngesteuerterModusActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        alertDialog.setPositiveButton("Umbenennen",
                (dialog, which) -> {
                    if(!input.getText().toString().equals("")){
                        datei_name = input.getText().toString();
                        Log.d("\t**********DATEI_NAME:", datei_name);
                        Toast.makeText(getApplicationContext(), "Datei erfolgreich umbenannt", Toast.LENGTH_LONG).show();
                    }else{
                        createPopUp();
                        Toast.makeText(getApplicationContext(), "Geben Sie einen Naemen!", Toast.LENGTH_LONG).show();
                    }
                });

        alertDialog.setNegativeButton("Abbrechen",
                (dialog, which) -> dialog.cancel());

        alertDialog.show();
    }


}
package com.example.teamprojekt;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;
import android.os.Environment;
import android.view.View;

import androidx.annotation.RequiresApi;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class TakePicturesFerngesteuert {


    //https://www.youtube.com/watch?v=_wZvds9CfuE

    Camera camera;
    SimpleDateFormat datumsformat = new SimpleDateFormat("dd.MM.yyyy_HH:mm:ss.SSSS");
    Calendar kalender;
    final File folder_gui = new File(Environment.getExternalStorageDirectory() + File.separator + "A_Project" + File.separator + "Bilder");
    private final String prefName = "MyPref";
    private final String pref_lo_left = "lo_left";
    private final String pref_lo_top = "lo_top";
    private final String pref_ru_left = "ru_left";
    private final String pref_ru_top = "ru_top";
    private final String pref_left_faktor = "left_faktor";
    private final String pref_top_faktor = "top_faktor";

    int zeileOben;
    int zeileUnten;
    int spalteLinks;
    int spalteRechts;


    public TakePicturesFerngesteuert(Camera camera, Context context){
        this.camera = camera;
        SharedPreferences sharedPreferences = context.getSharedPreferences(prefName, 0);
        float left_Faktor = sharedPreferences.getFloat(pref_left_faktor, 1);
        float top_Faktor = sharedPreferences.getFloat(pref_top_faktor, 1);
        zeileOben = (int) (sharedPreferences.getInt(pref_lo_top, 100) * top_Faktor);
        zeileUnten = (int) (sharedPreferences.getInt(pref_ru_top, 200) * top_Faktor);
        spalteLinks = (int) (sharedPreferences.getInt(pref_ru_left, 200) * left_Faktor);
        spalteRechts = (int) (sharedPreferences.getInt(pref_lo_left, 100) * left_Faktor);
    }

    /**
     * wird durch captureImage() aufgerufen
     */
    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback(){

        //Methode, die vom Button ausgelöst wird
        @Override
        public void onPictureTaken(byte[] data, Camera camera){
            //Ausgabepfad erstellen mit Dateinamen
            File picture_file = getOutputMediaFile();
            if(picture_file == null){
                return;
            }else{
                try {
                    FileOutputStream fos = new FileOutputStream(picture_file);
                    //Bild in Pfad schreiben
                    fos.write(skalieren(data));
                    fos.close();
                    //Bildstream fortsetzen
                    camera.startPreview();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    };


    /**
     * Erstellt eine Datei mit einem Ausgabepfad
     * @return Ausgabepfad
     */
    private File getOutputMediaFile(){
        String state = Environment.getExternalStorageState();
        if(!state.equals(Environment.MEDIA_MOUNTED)){
            return null;
        }else{
            //Überprüft ob der Pfad folder_gui vorhanden ist
            //Sonst erstellt er diesen
            if(!folder_gui.exists()){
                folder_gui.mkdirs();

            }

            //Jedes Bild erhält als Kennung das Datum der Aufnahme und die Uhrzeit bis zu den Millisekunden,
            //damit kein Bild überschrieben wird.
            //Kalender für Zeitabfrage erstellen
            kalender = Calendar.getInstance();
            //Uhrzeit weitergeben für die Erstellung der .json-Datei
            FerngesteuerterModusActivity.datei_name = "train-"+datumsformat.format(kalender.getTime());
            //Bilddatei benennen
            File outputFile = new File(folder_gui,  "train-"+datumsformat.format(kalender.getTime()) + ".jpg");
            return outputFile;
        }
    }


    /**
     * Schneidet das aufgenommene Bild auf die Größe 150x60px zu
     * Außerdem wird dabei nur der Bildbereich betrachetet, der in den Einstellungen ausgewählt wurde
     * Das Bild wird zum Schluss rotiert, da das Bild sonst gedreht ist und wieder zu einem byte-Array umgewandelt
     *
     * @param data Aufgenommenes Bild
     * @return Bild als byte-Array
     */
    private byte[] skalieren(byte[] data){
        //byte-Array zu Bitmap dekodieren
        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
        //Bitmap zuschneiden
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(zuschneiden(bmp), 50, 160, false);
        //Matrix zum rotieren des Bildes erstellen
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        //Bitmap um 90° rotieren
        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        //Bitmap wieder zu byte-Array umwandeln
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();

    }

    /**
     * Das Bild wird auf den ausgewählten Bereich aus den Einstellungen zugeschnietten
     * @param origialBitmap Bitmap des aufgenommenen Bildes
     * @return zugeschnittenes Bild
     */
    private Bitmap zuschneiden(Bitmap origialBitmap) {
        //Bitmap origialBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.original);


        assert (zeileOben>=0 && zeileOben<zeileUnten && zeileUnten<=origialBitmap.getWidth());
        assert (spalteRechts>=0 && spalteRechts<spalteLinks && spalteLinks<=origialBitmap.getHeight());

        Bitmap cutBitmap = Bitmap.createBitmap(origialBitmap.getWidth() / 2,
                origialBitmap.getHeight() / 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(cutBitmap);
        Rect desRect = new Rect(0, 0, origialBitmap.getWidth() / 2, origialBitmap.getHeight() / 2);
        //Bildbereich der ausgeschnitten werden soll
        // zeileOben -> Obere Grenze
        // zeileUben -> Untere Grenze
        // spalteLinks -> Linke Grenze
        // spalteRechts -> Rechte Grenze
        Rect srcRect = new Rect(zeileOben, spalteRechts, zeileUnten, spalteLinks);
        canvas.drawBitmap(origialBitmap, srcRect, desRect, null);
        return cutBitmap;
    }


    /**
     * Methode für den Button zum auslösen
     * @param v View
     */
    public void captureImage(View v){
        if(camera !=null){
            try{
                camera.takePicture(null,null, mPictureCallback);
            }catch (RuntimeException e){

            }
        }
    }



}

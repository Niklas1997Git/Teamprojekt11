package com.example.teamprojekt;

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

    public TakePicturesFerngesteuert(Camera camera){
        this.camera = camera;
    }

    //wird durch captureImage() aufgerufen
    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback(){

        //Methode, die vom Button ausgelöst wird
        @Override
        public void onPictureTaken(byte[] data, Camera camera){
            File picture_file = getOutputMediaFile();
            if(picture_file == null){
                return;
            }else{
                try {
                    FileOutputStream fos = new FileOutputStream(picture_file);
                    fos.write(skalieren(data));
                    fos.close();
                    camera.startPreview();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    };

    //Erstellt eine Datei mit einem Ausgabepfad

    private File getOutputMediaFile(){
        String state = Environment.getExternalStorageState();
        if(!state.equals(Environment.MEDIA_MOUNTED)){
            return null;
        }else{
            //Ausgabe Order entspricht "GUI" im internen Speicher


            if(!folder_gui.exists()){
                folder_gui.mkdirs();

            }

            //Jedes Bild erhält als Kennung das Datum der Aufnahme und die Uhrzeit bis zu den Millisekunden,
            //damit kein Bild überschrieben wird.

            kalender = Calendar.getInstance();

            FerngesteuerterModusActivity.datei_name = datumsformat.format(kalender.getTime());
            File outputFile = new File(folder_gui,  datumsformat.format(kalender.getTime()) + ".jpg");
            return outputFile;
        }
    }




    private byte[] skalieren(byte[] data){
        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(zuschneiden(bmp), 50, 160, false);
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();

    }

    private Bitmap zuschneiden(Bitmap origialBitmap) {
        //Bitmap origialBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.original);

        int zeileOben = 100;
        int zeileUnten = 200;
        assert (zeileOben>=0 && zeileOben<zeileUnten && zeileUnten<=origialBitmap.getWidth());
        int spalteLinks = 200;
        int spalteRechts = 100;
        assert (spalteRechts>=0 && spalteRechts<spalteLinks && spalteLinks<=origialBitmap.getHeight());
        Bitmap cutBitmap = Bitmap.createBitmap(origialBitmap.getWidth() / 2,
                origialBitmap.getHeight() / 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(cutBitmap);
        Rect desRect = new Rect(0, 0, origialBitmap.getWidth() / 2, origialBitmap.getHeight() / 2);

        Rect srcRect = new Rect(zeileOben, spalteRechts,
                zeileUnten,
                spalteLinks);
        canvas.drawBitmap(origialBitmap, srcRect, desRect, null);
        return cutBitmap;
    }


    //Methode für den Button zum auslösen
    public void captureImage(View v){
        if(camera !=null){
            try{
                camera.takePicture(null,null, mPictureCallback);
            }catch (RuntimeException e){

            }
        }
    }



}

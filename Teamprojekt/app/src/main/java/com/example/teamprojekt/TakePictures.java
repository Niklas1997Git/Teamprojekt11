package com.example.teamprojekt;

import android.hardware.Camera;
import android.os.Environment;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TakePictures {


    //https://www.youtube.com/watch?v=_wZvds9CfuE

    Camera camera;

    public TakePictures(Camera camera){
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
                    fos.write(data);
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
            File folder_gui = new File(Environment.getExternalStorageDirectory() +File.separator + "GUI");

            if(!folder_gui.exists()){
                folder_gui.mkdirs();

            }

            /*
            Jedes Bild erhält als Kennung das Datum der Aufnahme und die Uhrzeit bis zu den Millisekunden,
            damit kein Bild überschrieben wird.
             */
            Calendar kalender = Calendar.getInstance();
            SimpleDateFormat datumsformat = new SimpleDateFormat("dd.MM.yyyy_HH:mm:ss.SSS");
            File outputFile = new File(folder_gui,  datumsformat.format(kalender.getTime()) + ".jpg");
            return outputFile;
        }
    }


    //Methode für den Button zum auslösen
    public void captureImage(View v){
        if(camera !=null){
            camera.takePicture(null,null, mPictureCallback);
        }
    }
}

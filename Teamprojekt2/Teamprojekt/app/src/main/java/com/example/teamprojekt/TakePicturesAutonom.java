package com.example.teamprojekt;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.hardware.Camera;
import android.util.Log;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import org.tensorflow.lite.Interpreter;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.MappedByteBuffer;

public class TakePicturesAutonom {

    Camera camera;
    AutonomerModusActivity autonomerModus;

    Interpreter tflite;


    private final String prefName = "MyPref";
    private final String pref_lo_left = "lo_left";
    private final String pref_lo_top = "lo_top";
    private final String pref_ru_left = "ru_left";
    private final String pref_ru_top = "ru_top";

    int zeileOben;
    int zeileUnten;
    int spalteLinks;
    int spalteRechts;

    public TakePicturesAutonom(Camera camera, AutonomerModusActivity autonomerModusActivity, Interpreter model){
        this.camera = camera;
        this.autonomerModus = autonomerModusActivity;
        SharedPreferences sharedPreferences = autonomerModusActivity.getSharedPreferences(prefName, 0);
        zeileOben = sharedPreferences.getInt(pref_lo_top, 100);
        zeileUnten = sharedPreferences.getInt(pref_ru_top, 200);
        spalteLinks = sharedPreferences.getInt(pref_ru_left, 200);
        spalteRechts = sharedPreferences.getInt(pref_lo_left, 100);

        tflite = model;
    }

    //wird durch captureImage() aufgerufen
    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback(){

        //Methode, die vom Button ausgelöst wird
        @Override
        public void onPictureTaken(byte[] data, Camera camera){
            Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(cutOut(bmp), 60, 150, false);
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
            String[] output=new String[14];
            tflite.run(bitmapToMatrix(rotatedBitmap), output);
            //TODO:Amins Funktion hier einfügen mit Output als Parameter
            //TODO: "Value" durch Ergebnis der Funktion ersetzen
            JSONObject json = createJsonData("value");
            new Thread(new UDPClientSend(10000, "", json)).start();
            /*
            int mPhotoWidth = bmp.getWidth();
            int mPhotoHeight = bmp.getHeight();
            int[] pix = new int[mPhotoWidth * mPhotoHeight];
             */
            /*
            //  Here I want to slice a piece "out of bounds" starting at -50, -25
            //  Given an endposition of 150, 75 you will get a result of 200x100px
            Rect rect = new Rect(-50, -25, 150, 75);
            //  Be sure that there is at least 1px to slice.
            assert(rect.left < rect.right && rect.top < rect.bottom);
            //  Create our resulting image (150--50),(75--25) = 200x100px
            Bitmap resultBmp = Bitmap.createBitmap(rect.right-rect.left, rect.bottom-rect.top, Bitmap.Config.ARGB_8888);
            //  draw source bitmap into resulting image at given position:
            new Canvas(resultBmp).drawBitmap(bmp, -rect.left, -rect.top, null);
             */
            //autonomerModus.capturePreview.setImageBitmap(rotatedBitmap);
            camera.startPreview();
        }
    };






    private Bitmap cutOut(Bitmap origialBitmap) {
        //Bitmap origialBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.original);
        assert (zeileOben>=0 && zeileOben<zeileUnten && zeileUnten<=origialBitmap.getWidth());
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


    private int[][][] bitmapToMatrix(Bitmap bitmap){
        int[][][] matrix = new int[bitmap.getWidth()][bitmap.getHeight()][3];
        for (int w = 0; w<bitmap.getWidth(); w++){
            for (int h = 0; h<bitmap.getHeight();h++){
                matrix[w][h][0] = Color.red(bitmap.getPixel(w,h));
                matrix[w][h][0] = Color.green(bitmap.getPixel(w,h));
                matrix[w][h][0] = Color.blue(bitmap.getPixel(w,h));
            }
        }
        return matrix;
    }


    //Methode für den Button zum auslösen
    public void captureImageAutonom(View v){
        if(camera !=null){
            camera.takePicture(null,null, mPictureCallback);
        }
    }

    private JSONObject createJsonData(String jsonValue){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("steering", jsonValue);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Log.i("********JSON******", jsonObject.toString());
        return jsonObject;
    }
}

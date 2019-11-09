package com.example.teamprojekt;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SaveClass implements Serializable {

    private static SaveClass saveClass;
    private boolean automatischHochladen;
    private String email;
    private String passwort;
    private int anzahlBilder;

    private SaveClass(){
    }

    public static SaveClass getInstance(Context context){
        if(saveClass==null){
            saveClass = load(context);
            if(saveClass==null){
                saveClass=new SaveClass();
            }
        }
        return saveClass;
    }

    public boolean isAutomatischHochladen() {
        return automatischHochladen;
    }

    public void setAutomatischHochladen(boolean automatischHochladen, Context context) {
        this.automatischHochladen = automatischHochladen;
        save(context);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email, Context context) {
        this.email = email;
        save(context);
    }

    public String getPasswort() {
        return passwort;
    }

    public void setPasswort(String passwort, Context context) {
        this.passwort = passwort;
        save(context);
    }

    public int getAnzahlBilder() {
        return anzahlBilder;
    }

    public void setAnzahlBilder(int nummer, Context context) {
        this.anzahlBilder = nummer;
        save(context);
    }


    public void save(Context context) {
        try {
            FileOutputStream fos = context.openFileOutput("settings", Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(this);
            os.close();
            fos.close();
            System.out.println("The Object  was succesfully written to a file");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static SaveClass load(Context context){
        SaveClass saveClass =null;
        try {
            FileInputStream fileInputStream = context.openFileInput("settings");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            saveClass = (SaveClass) objectInputStream.readObject();

            objectInputStream.close();
            fileInputStream.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return saveClass;
    }
}

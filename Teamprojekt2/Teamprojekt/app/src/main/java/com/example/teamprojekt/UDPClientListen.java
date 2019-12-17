package com.example.teamprojekt;


import android.os.Environment;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;

import static com.example.teamprojekt.FerngesteuerterModusActivity.datei_name;

public class UDPClientListen implements Runnable {

    int port;
    String s;
    String[] sArr;
    final File folder_json = new File(Environment.getExternalStorageDirectory() + File.separator + "A_Project" + File.separator + "JSON");

    public UDPClientListen(int port, String[] s){
        this.port = port;
        this.sArr = s;
    }

    @Override
    public void run() {
        boolean run = true;
        DatagramSocket udpSocket = null;

            try {
                udpSocket = new DatagramSocket(port);
                byte[] message = new byte[8000];
                DatagramPacket packet = new DatagramPacket(message,message.length);
                Log.i("UDP client: ", "about to wait to receive");
                udpSocket.setSoTimeout(100);
                udpSocket.receive(packet);
                s = new String(message, 0, packet.getLength());
                sArr[0] = getWertJSON(s,"steering");
                sArr[1] = getWertJSON(s,"throttle");
                //saveJSONFile(getWertJSON(s,"steering"), getWertJSON(s, "throttle"));
                Log.d("Received data", s);
            } catch (java.net.SocketTimeoutException e) {
                e.printStackTrace();
                System.out.println("nachricht:" + s);
                sArr[0] = getWertJSON(s,"steering");
                sArr[1] = getWertJSON(s,"throttle");
                //saveJSONFile(getWertJSON(s,"steering"), getWertJSON(s, "throttle"));
                Log.e("Timeout Exception","UDP Connection:",e);
                run = false;
            } catch (IOException e) {
                sArr[0] = getWertJSON(s,"steering");
                sArr[1] = getWertJSON(s,"throttle");
                //saveJSONFile(getWertJSON(s,"steering"), getWertJSON(s, "throttle"));
                Log.e("UDPclientHasIOException", "error: ", e);
                run = false;
            }
            finally {
                udpSocket.close();
            }

    }

    private String getWertJSON(String json, String wert){
        if(json ==null||wert == null){
            return"";
        }
        System.out.println("_____________getWert_____________");
        System.out.println(json);
        String value = "";
        String[] eintraege = json.split(",");
        System.out.println(eintraege.toString());
        for(String eintrag: eintraege){
            System.out.println(eintrag);

            if(eintrag.contains(wert)){
                System.out.println("------------enthält wert------------");
                String[] wertEintrag = eintrag.split(":");
                System.out.println(wertEintrag.toString());
                if(wertEintrag[1].contains("}")){
                    wertEintrag[1].replace("}","");
                    value = wertEintrag[1];
                }
                System.out.println(wertEintrag[1]);
                System.out.println("_____________getWert_____________");
                return  wertEintrag[1];
            }else{
                System.out.println("------------enthält wert nicht------------");
            }
        }
        System.out.println("_____________getWert_____________");
        return value;
    }

    private void saveJSONFile(String steering, String throttle) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("steering", steering);
            jsonObject.put("throttle", throttle);
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
                System.out.println("gespeichert");
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

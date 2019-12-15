package com.example.teamprojekt;

import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPClientSend implements Runnable {
    int port;
    String ip;
    String d;
    JSONObject json;
    public UDPClientSend(int port, String ip, JSONObject json){
        this.port = port;
        this.ip = ip;
        this.json = json;
    }

    @Override
    public void run() {
        DatagramSocket udpSocket = null;
        try {
            udpSocket = new DatagramSocket(port);
            InetAddress serverAddr = InetAddress.getByName(ip);
            byte[] buf = json.toString().getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length,serverAddr, port);
            udpSocket.send(packet);
        } catch (SocketException e) {
            Log.e("Udp:", "Socket Error:", e);
        } catch (IOException e) {
            Log.e("Udp Send:", "IO Error:", e);
        }finally {
            udpSocket.close();
        }
    }
}

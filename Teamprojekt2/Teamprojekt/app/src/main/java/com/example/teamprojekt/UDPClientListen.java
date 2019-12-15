package com.example.teamprojekt;


import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;

public class UDPClientListen implements Runnable {

    int port;

    public UDPClientListen(int port){
        this.port = port;
    }

    @Override
    public void run() {
        boolean run = true;
        DatagramSocket udpSocket = null;
        while (run) {
            try {
                udpSocket = new DatagramSocket(port);
                byte[] message = new byte[8000];
                DatagramPacket packet = new DatagramPacket(message,message.length);
                Log.i("UDP client: ", "about to wait to receive");
                //udpSocket.setSoTimeout(10000);
                udpSocket.receive(packet);
                String text = new String(message, 0, packet.getLength());
                Log.d("Received data", text);
            } catch (SocketTimeoutException e) {
                Log.e("Timeout Exception","UDP Connection:",e);
                run = false;
            } catch (IOException e) {
                Log.e("UDPclientHasIOException", "error: ", e);
                run = false;
            }
            finally {
                udpSocket.close();
            }
        }
    }
}

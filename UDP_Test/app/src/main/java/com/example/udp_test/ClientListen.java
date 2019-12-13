package com.example.udp_test;

import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;

public class ClientListen implements Runnable {


    int port;
    TextView textv;

    public ClientListen(int port, TextView textView, Button b){
        this.port = port;
        textv = textView;
    }

    @Override
    public void run() {
        boolean run = true;
        while (run) {
            try {
                DatagramSocket udpSocket = new DatagramSocket(port);
                byte[] message = new byte[8000];
                DatagramPacket packet = new DatagramPacket(message,message.length);
                Log.i("UDP client: ", "about to wait to receive");
                udpSocket.setSoTimeout(10000);
                udpSocket.receive(packet);
                String text = new String(message, 0, packet.getLength());
                textv.setText(text);
                Log.d("Received data", text);
            } catch (SocketTimeoutException e) {
                Log.e("Timeout Exception","UDP Connection:",e);
                run = false;
            } catch (IOException e) {
                Log.e("UDPclientHasIOException", "error: ", e);
                run = false;
            }
        }
    }
}

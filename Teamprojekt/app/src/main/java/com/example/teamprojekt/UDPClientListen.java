package com.example.teamprojekt;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPClientListen implements Runnable {

    int port;
    String text;

    public UDPClientListen(int port){
        this.port = port;
    }
    @Override
    public void run() {
        boolean run = true;
        while (run) {
            try {
                DatagramSocket udpSocket = new DatagramSocket(port);
                byte[] message = new byte[8000];
                DatagramPacket packet = new DatagramPacket(message,message.length);
                System.out.println("UDP client: about to wait to receive");
                udpSocket.receive(packet);
                text = new String(message, 0, packet.getLength());
                System.out.println("Received data" + text);
                return;
            }catch (IOException e) {
                System.out.println("UDP client has IOException");
                run = false;
            }
        }
    }

    public String getText() {
        return text;
    }
}

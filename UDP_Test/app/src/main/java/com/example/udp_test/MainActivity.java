package com.example.udp_test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private int listenPort;
    private int sendPort;

    private String sendData;
    private String ip;

    private EditText editListenPort;
    private EditText editSendPort;
    private EditText editSendData;
    private EditText editIP;

    private TextView receiveText;

    private Button buttonSend;
    private Button buttonReceive;

    public static boolean isReceiving;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listenPort = 0;
        sendPort = 0;
        sendData = null;
        ip = null;

        editListenPort = findViewById(R.id.listenPort);
        editSendPort =findViewById(R.id.sendPort);
        editSendData = findViewById(R.id.send_Data);
        editIP = findViewById(R.id.ipAdresse);

        receiveText = findViewById(R.id.receiveData);

        buttonSend = findViewById(R.id.button_SendData);
        buttonReceive = findViewById(R.id.button_Receive);

        isReceiving = false;


        editListenPort.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                listenPort = Integer.parseInt(editListenPort.getText().toString());
            }
        });

        editSendPort.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                sendPort = Integer.parseInt(editSendPort.getText().toString());
            }
        });

        editSendData.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                sendData = editSendData.getText().toString();
            }
        });

        editIP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ip = editIP.getText().toString();
            }
        });


        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sendPort!=0&&ip!=null&& sendData!=null){
                    new Thread(new ClientSend(sendPort, ip, sendData)).start();
                }


            }
        });

        buttonReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listenPort!=0){
                    new Thread(new ClientListen(listenPort, receiveText, buttonReceive)).start();
                }


            }
        });
    }
}

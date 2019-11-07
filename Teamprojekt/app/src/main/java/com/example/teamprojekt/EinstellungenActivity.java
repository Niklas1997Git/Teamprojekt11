package com.example.teamprojekt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

public class EinstellungenActivity extends AppCompatActivity {

    private Switch automatischHochladen;
    private EditText email;
    private EditText passwort;
    private TextView nummer;
    private SeekBar anzahlBilder;

    private SaveClass saveClass;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_einstellungen);
        getSupportActionBar().setTitle("Einstellungen");

        context=this;
        automatischHochladen = findViewById(R.id.switch1);
        email = findViewById(R.id.editText2);
        passwort = findViewById(R.id.editText3);
        nummer = findViewById(R.id.textView4);
        anzahlBilder = findViewById(R.id.seekBar);
        saveClass = SaveClass.getInstance(context);

        automatischHochladen.setChecked(saveClass.isAutomatischHochladen());
        email.setText(saveClass.getEmail());
        passwort.setText(saveClass.getPasswort());
        int progress = saveClass.getNummer();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            anzahlBilder.setProgress(progress, true);
        }
        nummer.setText("" + progress + " mal die Sekunde");



        automatischHochladen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                saveClass.setAutomatischHochladen(automatischHochladen.isChecked(), context);
            }
        });

        email.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                saveClass.setEmail(email.getText().toString(), context);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        passwort.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                saveClass.setPasswort(passwort.getText().toString(), context);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        anzahlBilder.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                nummer.setText("" + anzahlBilder.getProgress() + " mal die Sekunde");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                saveClass.setNummer(seekBar.getProgress(), context);
            }
        });
    }






}

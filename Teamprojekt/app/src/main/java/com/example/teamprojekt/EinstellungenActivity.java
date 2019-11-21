package com.example.teamprojekt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

public class EinstellungenActivity extends AppCompatActivity {

    private Switch automatischHochladen;
    private EditText email;
    private EditText passwort;
    private TextView nummer;
    private SeekBar anzahlBilder;
    private Spinner cameraSizes;

    private String[] supportedCameraSizes;


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
        int progress = saveClass.getAnzahlBilder();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            anzahlBilder.setProgress(progress, true);
        }
        nummer.setText("" + progress + " mal die Sekunde");

        cameraSizes = findViewById(R.id.spinner);
        setSupportedCameraSizes();
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, supportedCameraSizes);
        //set the spinners adapter to the previously created one.
        cameraSizes.setAdapter(adapter);
        cameraSizes.setSelection(saveClass.getAufloesung_position());



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
                saveClass.setAnzahlBilder(seekBar.getProgress(), context);
            }
        });

        cameraSizes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] selected = parent.getItemAtPosition(position).toString().split("x");
                System.out.println(selected[0] + "x" + selected[1] + " gespeichert");
                saveClass.setAufloesung_position(position);
                saveClass.setAufloesung_hoehe(Integer.parseInt(selected[1]));
                saveClass.setAufloesung_breite(Integer.parseInt(selected[0]));
                saveClass.save(context);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    public void setSupportedCameraSizes(){
        Camera camera = Camera.open();
        Camera.Parameters params;
        params = camera.getParameters();
        List<Camera.Size> sizes = params.getSupportedPictureSizes();
        supportedCameraSizes = new String[sizes.size()];

        for(int i=0; i<sizes.size();i++){
            //System.out.println("Size width: "+ size.width +", Size height: " + size.height);
            supportedCameraSizes[i] = sizes.get(i).width + "x" + sizes.get(i).height;
        }
        camera.release();
    }





}

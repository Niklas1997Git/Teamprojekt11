package com.example.teamprojekt;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

public class EinstellungenActivity extends AppCompatActivity {

    //UI Elemente
    private Switch automatischHochladen;
    private EditText email;
    private EditText passwort;
    private TextView nummer;
    private SeekBar anzahlBilder;
    private Spinner cameraSizes;
    private CustomScrollView customScrollView;
    private Button button_Bildbereich;

    //Rechteck deklarierung
    private ImageView rechteckLinksOben;
    private ImageView rechteckLinksUnten;
    private ImageView rechteckRechtsUnten;
    private ImageView rechteckRechtsOben;

    //
    private int xDelta;
    private int yDelta;
    private int x_neu;
    private int y_neu;

    private final int SQUARE_SIZE=25;
    private int MAX_LEFT_MARGIN = 480 - SQUARE_SIZE;
    private int MAX_TOP_MARGIN = 640 - SQUARE_SIZE;
    private final int STANDARD_MARGIN = 0;

    private final int LEFT_LEFT_MARGIN = 60;
    private final int RIGHT_LEFT_MARGIN = 380;
    private final int TOP_TOP_MARGIN = 200;
    private final int BOTTOM_TOP_MARGIN = 320;

    //SharedPreferences Konstanten
    private final String prefName = "MyPref";
    private final String pref_automatischHochladen = "hochladen";
    private final String pref_email = "email";
    private final String pref_passwort = "passwort";
    private final String pref_anzahlBilder = "anzahlBilder";
    private final String pref_aufloesung_breite = "breite";
    private final String pref_aufloesung_hoehe = "hoehe";
    private final String pref_aufloesung_position = "position";
    private final String pref_left_faktor = "left_faktor";
    private final String pref_top_faktor = "top_faktor";

    //TODO
    private final String pref_lo_left = "lo_left";
    private final String pref_lo_top = "lo_top";
    private final String pref_lu_left = "lu_left";
    private final String pref_lu_top = "lu_top";
    private final String pref_ru_left = "ru_left";
    private final String pref_ru_top = "ru_top";
    private final String pref_ro_left = "ro_left";
    private final String pref_ro_top = "ro_top";

    private SharedPreferences sharedPreferences;

    private String[] gewaehlte_Aufloesung;
    private int aufloesung_Position = 0;

    //Kamera-auflösungen für die Auswahl
    private String[] supportedCameraSizes;


    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_einstellungen);
        getSupportActionBar().setTitle("Einstellungen");

        context=this;

        automatischHochladen = findViewById(R.id.switch_Hochladen);
        email = findViewById(R.id.editText_Email);
        passwort = findViewById(R.id.editText_Passwort);
        nummer = findViewById(R.id.textView_AnzahlBilder);
        anzahlBilder = findViewById(R.id.seekBar_AnzahlBilder);
        cameraSizes = findViewById(R.id.spinner_aufloesung);
        customScrollView = findViewById(R.id.customScrollView);
        button_Bildbereich = findViewById(R.id.button_Bildbereich);

        ImageView bild = findViewById(R.id.imageView4);
        MAX_LEFT_MARGIN = bild.getLayoutParams().width - SQUARE_SIZE;
        MAX_TOP_MARGIN = bild.getLayoutParams().height - SQUARE_SIZE;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            System.out.println("Width "+ bild.getLayoutParams().width);
            System.out.println("Height" + bild.getLayoutParams().height);
        }
        System.out.println("Width "+ MAX_LEFT_MARGIN);
        System.out.println("Height" + MAX_TOP_MARGIN);

        rechteckLinksOben = findViewById(R.id.imageView_lo);
        rechteckLinksUnten = findViewById(R.id.imageView_lu);
        rechteckRechtsUnten = findViewById(R.id.imageView_ru);
        rechteckRechtsOben = findViewById(R.id.imageView_ro);


        setSupportedCameraSizes();
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, supportedCameraSizes);
        //set the spinners adapter to the previously created one.
        cameraSizes.setAdapter(adapter);

        //Speicherung
        sharedPreferences = getSharedPreferences(prefName, 0);


        automatischHochladen.setChecked(sharedPreferences.getBoolean(pref_automatischHochladen, false));
        email.setText(sharedPreferences.getString(pref_email, ""));
        passwort.setText(sharedPreferences.getString(pref_passwort, ""));
        int progress = sharedPreferences.getInt(pref_anzahlBilder, 10);
        //Werte aus Speicherung setzen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            anzahlBilder.setProgress(progress, true);
        }
        nummer.setText("" + progress + " mal die Sekunde");
        aufloesung_Position = sharedPreferences.getInt(pref_aufloesung_position, 0);
        cameraSizes.setSelection(aufloesung_Position);


        rechteckLinksOben.setOnTouchListener(onTouchListener());
        rechteckLinksUnten.setOnTouchListener(onTouchListener());
        rechteckRechtsUnten.setOnTouchListener(onTouchListener());
        rechteckRechtsOben.setOnTouchListener(onTouchListener());


        rechteckLinksOben.getLayoutParams().height = SQUARE_SIZE;
        rechteckLinksOben.getLayoutParams().width = SQUARE_SIZE;
        rechteckLinksOben.requestLayout();

        rechteckLinksUnten.getLayoutParams().height = SQUARE_SIZE;
        rechteckLinksUnten.getLayoutParams().width = SQUARE_SIZE;
        rechteckLinksUnten.requestLayout();

        rechteckRechtsUnten.getLayoutParams().height = SQUARE_SIZE;
        rechteckRechtsUnten.getLayoutParams().width = SQUARE_SIZE;
        rechteckRechtsUnten.requestLayout();

        rechteckRechtsOben.getLayoutParams().height = SQUARE_SIZE;
        rechteckRechtsOben.getLayoutParams().width = SQUARE_SIZE;
        rechteckRechtsOben.requestLayout();


        //ImageView Startwerte setzen
        //Links oben
        RelativeLayout.LayoutParams lp_lo = (RelativeLayout.LayoutParams) rechteckLinksOben.getLayoutParams();
        lp_lo.leftMargin = sharedPreferences.getInt(pref_lo_left, LEFT_LEFT_MARGIN);
        lp_lo.topMargin = sharedPreferences.getInt(pref_lo_top, TOP_TOP_MARGIN);
        lp_lo.rightMargin = STANDARD_MARGIN;
        lp_lo.bottomMargin = STANDARD_MARGIN;
        rechteckLinksOben.setLayoutParams(lp_lo);

        //Links unten
        RelativeLayout.LayoutParams lp_lu = (RelativeLayout.LayoutParams) rechteckLinksUnten.getLayoutParams();
        lp_lu.leftMargin = sharedPreferences.getInt(pref_lu_left, LEFT_LEFT_MARGIN);
        lp_lu.topMargin = sharedPreferences.getInt(pref_lu_top, BOTTOM_TOP_MARGIN);
        lp_lu.rightMargin = STANDARD_MARGIN;
        lp_lu.bottomMargin = STANDARD_MARGIN;
        rechteckLinksUnten.setLayoutParams(lp_lu);

        //Rechts unten
        RelativeLayout.LayoutParams lp_ru = (RelativeLayout.LayoutParams) rechteckRechtsUnten.getLayoutParams();
        lp_ru.leftMargin = sharedPreferences.getInt(pref_ru_left, RIGHT_LEFT_MARGIN);
        lp_ru.topMargin = sharedPreferences.getInt(pref_ru_top, BOTTOM_TOP_MARGIN);
        lp_ru.rightMargin = STANDARD_MARGIN;
        lp_ru.bottomMargin = STANDARD_MARGIN;
        rechteckRechtsUnten.setLayoutParams(lp_ru);

        //Rechts oben
        RelativeLayout.LayoutParams lp_ro = (RelativeLayout.LayoutParams) rechteckRechtsOben.getLayoutParams();
        lp_ro.leftMargin = sharedPreferences.getInt(pref_ro_left, RIGHT_LEFT_MARGIN);
        lp_ro.topMargin = sharedPreferences.getInt(pref_ro_top, TOP_TOP_MARGIN);
        lp_ro.rightMargin = STANDARD_MARGIN;
        lp_ro.bottomMargin = STANDARD_MARGIN;
        rechteckRechtsOben.setLayoutParams(lp_ro);


        button_Bildbereich.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Speicherung des Bildbereichs
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) rechteckLinksOben.getLayoutParams();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(pref_lo_left, lp.leftMargin);
                editor.putInt(pref_lo_top, lp.topMargin);
                lp = (RelativeLayout.LayoutParams) rechteckLinksUnten.getLayoutParams();
                editor.putInt(pref_lu_left, lp.leftMargin);
                editor.putInt(pref_lu_top, lp.topMargin);
                lp = (RelativeLayout.LayoutParams) rechteckRechtsUnten.getLayoutParams();
                editor.putInt(pref_ru_left, lp.leftMargin);
                editor.putInt(pref_ru_top, lp.topMargin);
                lp = (RelativeLayout.LayoutParams) rechteckRechtsOben.getLayoutParams();
                editor.putInt(pref_ro_left, lp.leftMargin);
                editor.putInt(pref_ro_top, lp.topMargin);
                editor.commit();
            }
        });

        automatischHochladen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                //saveClass.setAutomatischHochladen(automatischHochladen.isChecked(), context);
            }
        });

        email.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                //TODO: Login zu google drive prüfen
                //saveClass.setEmail(email.getText().toString(), context);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        passwort.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                //TODO: Login zu google drive prüfen
                //saveClass.setPasswort(passwort.getText().toString(), context);
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
            }
        });

        cameraSizes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gewaehlte_Aufloesung = parent.getItemAtPosition(position).toString().split("x");
                System.out.println(gewaehlte_Aufloesung[0] + "x" + gewaehlte_Aufloesung[1] + " gespeichert");
                aufloesung_Position = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    View.OnLongClickListener longclckListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            ClipData data = ClipData.newPlainText("1", "2");
            View.DragShadowBuilder myShadowBuilder = new View.DragShadowBuilder(v);
            v.startDrag(data,myShadowBuilder, v,0);
            return false;
        }
    };


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




    private View.OnTouchListener onTouchListener() {
        return new View.OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                final int x = (int) event.getRawX();
                final int y = (int) event.getRawY();

                switch (event.getAction() & MotionEvent.ACTION_MASK) {

                    case MotionEvent.ACTION_DOWN:
                        RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams)
                                view.getLayoutParams();

                        xDelta = x - lParams.leftMargin;
                        yDelta = y - lParams.topMargin;
                        customScrollView.setEnableScrolling(false); // disable scrolling
                        break;

                    case MotionEvent.ACTION_UP:
                        /*
                        Toast.makeText(EinstellungenActivity.this,
                                "I'm here!", Toast.LENGTH_SHORT)
                                .show();
                        */
                        customScrollView.setEnableScrolling(true); // disable scrolling
                        break;

                    case MotionEvent.ACTION_MOVE:
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view
                                .getLayoutParams();
                        System.out.println(context.getResources().getResourceEntryName(view.getId()));

                        x_neu = x - xDelta;
                        if(x_neu<STANDARD_MARGIN){
                            x_neu = STANDARD_MARGIN;
                        }else if(x_neu>MAX_LEFT_MARGIN){
                            x_neu = MAX_LEFT_MARGIN;
                        }

                        y_neu = y - yDelta;
                        if(y_neu<STANDARD_MARGIN){
                            y_neu=STANDARD_MARGIN;
                        } else if(y_neu>MAX_TOP_MARGIN){
                            y_neu=MAX_TOP_MARGIN;
                        }
                        layoutParams.leftMargin = x_neu;
                        layoutParams.topMargin = y_neu;
                        layoutParams.rightMargin = STANDARD_MARGIN;
                        layoutParams.bottomMargin = STANDARD_MARGIN;

                        setPartnerPositions(context.getResources().getResourceEntryName(view.getId()), x_neu, y_neu);
                        System.out.println("left: " +layoutParams.leftMargin);
                        System.out.println("top: " +layoutParams.topMargin);

                        view.setLayoutParams(layoutParams);
                        break;
                }


                //mainLayout.invalidate();
                return true;
            }
        };
    }

    public void setPartnerPositions(String name, int left, int top){
        RelativeLayout.LayoutParams lp1;
        RelativeLayout.LayoutParams lp2;

        switch (name){
            case "imageView_lo":
                lp1 = (RelativeLayout.LayoutParams) rechteckRechtsOben.getLayoutParams();
                lp2 = (RelativeLayout.LayoutParams) rechteckLinksUnten.getLayoutParams();

                lp1.topMargin = top;
                lp2.leftMargin = left;
                rechteckRechtsOben.setLayoutParams(lp1);
                rechteckLinksUnten.setLayoutParams(lp2);
                break;
            case "imageView_lu":
                lp1 = (RelativeLayout.LayoutParams) rechteckLinksOben.getLayoutParams();
                lp2 = (RelativeLayout.LayoutParams) rechteckRechtsUnten.getLayoutParams();

                lp1.leftMargin = left;
                lp2.topMargin = top;
                rechteckLinksOben.setLayoutParams(lp1);
                rechteckRechtsUnten.setLayoutParams(lp2);
                break;
            case "imageView_ru":
                lp1 = (RelativeLayout.LayoutParams) rechteckLinksUnten.getLayoutParams();
                lp2 = (RelativeLayout.LayoutParams) rechteckRechtsOben.getLayoutParams();

                lp1.topMargin = top;
                lp2.leftMargin = left;
                rechteckLinksUnten.setLayoutParams(lp1);
                rechteckRechtsOben.setLayoutParams(lp2);
                break;
            case "imageView_ro":
                lp1 = (RelativeLayout.LayoutParams) rechteckLinksOben.getLayoutParams();
                lp2 = (RelativeLayout.LayoutParams) rechteckRechtsUnten.getLayoutParams();

                lp1.topMargin = top;
                lp2.leftMargin = left;
                rechteckLinksOben.setLayoutParams(lp1);
                rechteckRechtsUnten.setLayoutParams(lp2);
                break;
        }
        partnerDistanceControll(name);
    }



    public void partnerDistanceControll(String name){
        RelativeLayout.LayoutParams lp;
        RelativeLayout.LayoutParams lp1;
        RelativeLayout.LayoutParams lp2;

        switch (name){
            case "imageView_lo":
                lp = (RelativeLayout.LayoutParams) rechteckLinksOben.getLayoutParams();
                lp1 = (RelativeLayout.LayoutParams) rechteckRechtsOben.getLayoutParams();
                lp2 = (RelativeLayout.LayoutParams) rechteckLinksUnten.getLayoutParams();

                if(lp2.topMargin-lp.topMargin<SQUARE_SIZE){
                    lp.topMargin = lp2.topMargin-SQUARE_SIZE;
                    lp1.topMargin = lp2.topMargin-SQUARE_SIZE;
                }
                if(lp1.leftMargin-lp.leftMargin<SQUARE_SIZE){
                    lp.leftMargin = lp1.leftMargin-SQUARE_SIZE;
                    lp2.leftMargin = lp1.leftMargin-SQUARE_SIZE;
                }

                rechteckLinksOben.setLayoutParams(lp);
                rechteckRechtsOben.setLayoutParams(lp1);
                rechteckLinksUnten.setLayoutParams(lp2);
                break;
            case "imageView_lu":
                lp = (RelativeLayout.LayoutParams) rechteckLinksUnten.getLayoutParams();
                lp1 = (RelativeLayout.LayoutParams) rechteckLinksOben.getLayoutParams();
                lp2 = (RelativeLayout.LayoutParams) rechteckRechtsUnten.getLayoutParams();

                if(lp.topMargin - lp1.topMargin<SQUARE_SIZE){
                    lp.topMargin = lp1.topMargin+SQUARE_SIZE;
                    lp2.topMargin = lp.topMargin;
                }

                if(lp2.leftMargin-lp.leftMargin<SQUARE_SIZE){
                    lp.leftMargin = lp2.leftMargin-SQUARE_SIZE;
                    lp1.leftMargin = lp.leftMargin;
                }

                rechteckLinksUnten.setLayoutParams(lp);
                rechteckLinksOben.setLayoutParams(lp1);
                rechteckRechtsUnten.setLayoutParams(lp2);
                break;
            case "imageView_ru":
                lp = (RelativeLayout.LayoutParams) rechteckRechtsUnten.getLayoutParams();
                lp1 = (RelativeLayout.LayoutParams) rechteckLinksUnten.getLayoutParams();
                lp2 = (RelativeLayout.LayoutParams) rechteckRechtsOben.getLayoutParams();

                if(lp.topMargin-lp2.topMargin<SQUARE_SIZE){
                    lp.topMargin = lp2.topMargin+SQUARE_SIZE;
                    lp1.topMargin=lp.topMargin;
                }

                if(lp.leftMargin-lp1.leftMargin<SQUARE_SIZE){
                    lp.leftMargin = lp1.leftMargin +SQUARE_SIZE;
                    lp2.leftMargin = lp.leftMargin;
                }

                rechteckRechtsUnten.setLayoutParams(lp);
                rechteckLinksUnten.setLayoutParams(lp1);
                rechteckRechtsOben.setLayoutParams(lp2);
                break;
            case "imageView_ro":
                lp = (RelativeLayout.LayoutParams) rechteckRechtsOben.getLayoutParams();
                lp1 = (RelativeLayout.LayoutParams) rechteckLinksOben.getLayoutParams();
                lp2 = (RelativeLayout.LayoutParams) rechteckRechtsUnten.getLayoutParams();

                if(lp2.topMargin-lp.topMargin<SQUARE_SIZE){
                    lp.topMargin=lp2.topMargin-SQUARE_SIZE;
                    lp1.topMargin=lp.topMargin;
                }

                if(lp.leftMargin-lp1.leftMargin<SQUARE_SIZE){
                    lp.leftMargin = lp1.leftMargin+SQUARE_SIZE;
                    lp2.leftMargin = lp.leftMargin;
                }

                rechteckRechtsOben.setLayoutParams(lp);
                rechteckLinksOben.setLayoutParams(lp1);
                rechteckRechtsUnten.setLayoutParams(lp2);
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(pref_anzahlBilder, anzahlBilder.getProgress());
        editor.putInt(pref_aufloesung_breite, Integer.parseInt(gewaehlte_Aufloesung[0]));
        editor.putInt(pref_aufloesung_hoehe, Integer.parseInt(gewaehlte_Aufloesung[1]));
        editor.putInt(pref_aufloesung_position, aufloesung_Position);
        editor.putBoolean(pref_automatischHochladen, automatischHochladen.isChecked());
        editor.putString(pref_email, email.getText().toString());
        editor.putString(pref_passwort, passwort.getText().toString());
        editor.putFloat(pref_left_faktor, Float.parseFloat(gewaehlte_Aufloesung[1]) / (float) MAX_LEFT_MARGIN);
        editor.putFloat(pref_top_faktor, Float.parseFloat(gewaehlte_Aufloesung[0]) / (float) MAX_TOP_MARGIN);
        editor.commit();
    }
}

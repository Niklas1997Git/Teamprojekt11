package com.example.teamprojekt;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
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
    private final int MAX_LEFT_MARGIN = 480 - SQUARE_SIZE;
    private final int MAX_TOP_MARGIN = 640 - SQUARE_SIZE;
    private final int MIN_LEFT_MARGIN = 0;
    private final int MIN_TOP_MARGIN = 0;
    private final int STANDARD_MARGIN = 0;


    //Kamera-auflösungen für die Auswahl
    private String[] supportedCameraSizes;

    //Speicherungsklasse
    private SaveClass saveClass;

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
        cameraSizes = findViewById(R.id.spinner_NN);
        customScrollView = findViewById(R.id.customScrollView);


        rechteckLinksOben = findViewById(R.id.imageView_lo);
        rechteckLinksUnten = findViewById(R.id.imageView_lu);
        rechteckRechtsUnten = findViewById(R.id.imageView_ru);
        rechteckRechtsOben = findViewById(R.id.imageView_ro);


        saveClass = SaveClass.getInstance(context);


        automatischHochladen.setChecked(saveClass.isAutomatischHochladen());
        email.setText(saveClass.getEmail());
        passwort.setText(saveClass.getPasswort());
        int progress = saveClass.getAnzahlBilder();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            anzahlBilder.setProgress(progress, true);
        }
        nummer.setText("" + progress + " mal die Sekunde");


        setSupportedCameraSizes();
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, supportedCameraSizes);
        //set the spinners adapter to the previously created one.
        cameraSizes.setAdapter(adapter);
        cameraSizes.setSelection(saveClass.getAufloesung_position());

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
        lp_lo.leftMargin = MIN_LEFT_MARGIN;
        lp_lo.topMargin = MIN_TOP_MARGIN;
        lp_lo.rightMargin = STANDARD_MARGIN;
        lp_lo.bottomMargin = STANDARD_MARGIN;
        rechteckLinksOben.setLayoutParams(lp_lo);

        //Links unten
        RelativeLayout.LayoutParams lp_lu = (RelativeLayout.LayoutParams) rechteckLinksUnten.getLayoutParams();
        lp_lu.leftMargin = MIN_LEFT_MARGIN;
        lp_lu.topMargin = MAX_TOP_MARGIN;
        lp_lu.rightMargin = STANDARD_MARGIN;
        lp_lu.bottomMargin = STANDARD_MARGIN;
        rechteckLinksUnten.setLayoutParams(lp_lu);

        //Rechts unten
        RelativeLayout.LayoutParams lp_ru = (RelativeLayout.LayoutParams) rechteckRechtsUnten.getLayoutParams();
        lp_ru.leftMargin = MAX_LEFT_MARGIN;
        lp_ru.topMargin = MAX_TOP_MARGIN;
        lp_ru.rightMargin = STANDARD_MARGIN;
        lp_ru.bottomMargin = STANDARD_MARGIN;
        rechteckRechtsUnten.setLayoutParams(lp_ru);

        //Rechts oben
        RelativeLayout.LayoutParams lp_ro = (RelativeLayout.LayoutParams) rechteckRechtsOben.getLayoutParams();
        lp_ro.leftMargin = MAX_LEFT_MARGIN;
        lp_ro.topMargin = MIN_TOP_MARGIN;
        lp_ro.rightMargin = STANDARD_MARGIN;
        lp_ro.bottomMargin = STANDARD_MARGIN;
        rechteckRechtsOben.setLayoutParams(lp_ro);


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
    }






}

package com.example.teamprojekt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

public class EinstellungenActivity extends AppCompatActivity {

    private static final String TAG = "AndroidClarified";
    private SignInButton googleSignInButton;
    public static final String GOOGLE_ACCOUNT = "google_account";
    private boolean loggedIn;
    Button signOut;
    TextView account_email_textView;
    public static GoogleDriveHelper googleDriveHelper;
    GoogleSignInAccount account;

    private String applicationName = "Teamprojekt";

    //UI Elemente
    private Switch automatischHochladen;
    private TextView nummer;
    private SeekBar anzahlBilder;
    private Spinner cameraSizes;
    private CustomScrollView customScrollView;
    private Button button_Bildbereich;
    private com.google.android.material.textfield.TextInputEditText editText_boardPort;
    private com.google.android.material.textfield.TextInputEditText editText_appPort;
    private com.google.android.material.textfield.TextInputEditText editText_ipAdresse;

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

    private final int SQUARE_SIZE=30;
    private int MAX_LEFT_MARGIN = 480 - SQUARE_SIZE;
    private int MAX_TOP_MARGIN = 640 - SQUARE_SIZE;
    private final int STANDARD_MARGIN = 0;

    private final int LEFT_LEFT_MARGIN = 60;
    private final int RIGHT_LEFT_MARGIN = 380;
    private final int TOP_TOP_MARGIN = 200;
    private final int BOTTOM_TOP_MARGIN = 320;

    //SharedPreferences Konstanten
    private String prefName = "MyPref";
    private final String pref_automatischHochladen = "hochladen";
    private final String pref_anzahlBilder = "anzahlBilder";
    private final String pref_aufloesung_breite = "breite";
    private final String pref_aufloesung_hoehe = "hoehe";
    private final String pref_aufloesung_position = "position";

    private final String pref_boardPort = "boardPort";
    private final String pref_appPort = "appPort";
    private final String pref_ipAdresse = "ipAdresse";
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
    private int boardPort;
    private int appPort;
    private String ipAdresse;

    //Kamera-auflösungen für die Auswahl
    private String[] supportedCameraSizes;


    Context context;
    String s = "https://drive.google.com/file/d/1xpILvnNnOeT0XmWEMeUuX96oyREmGqBp/view?usp=sharing";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_einstellungen);
        getSupportActionBar().setTitle("Einstellungen");

        loggedIn = false;
        googleSignInButton = findViewById(R.id.sign_in_button);
        signOut = findViewById(R.id.sign_out_button);
        account_email_textView = findViewById(R.id.account_email);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                .build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 101);
            }
        });
        checkLogedIn();
        switchLogtinLogoutButtons();
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
          /*
          Sign-out is initiated by simply calling the googleSignInClient.signOut API. We add a
          listener which will be invoked once the sign out is the successful
           */
                googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //On Succesfull signout we navigate the user back to LoginActivity
                        signOut();
                    }
                });
            }
        });

        context=this;

        automatischHochladen = findViewById(R.id.switch_Hochladen);
        nummer = findViewById(R.id.textView_AnzahlBilder);
        anzahlBilder = findViewById(R.id.seekBar_AnzahlBilder);
        cameraSizes = findViewById(R.id.spinner_aufloesung);
        customScrollView = findViewById(R.id.customScrollView);
        button_Bildbereich = findViewById(R.id.button_Bildbereich);
        editText_boardPort = findViewById(R.id.boardPort);
        editText_appPort = findViewById(R.id.appPort);
        editText_ipAdresse = findViewById(R.id.ipAdresse);


        ImageView bild = findViewById(R.id.imageView4);
        MAX_LEFT_MARGIN = bild.getLayoutParams().width - SQUARE_SIZE;
        MAX_TOP_MARGIN = bild.getLayoutParams().height - SQUARE_SIZE;

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
        if(!loggedIn){
            sharedPreferences = getSharedPreferences(prefName, 0);
        }else{
            sharedPreferences = getSharedPreferences(prefName + account.getId(), 0);
        }


        //Update GUI

        /*
        automatischHochladen.setChecked(sharedPreferences.getBoolean(pref_automatischHochladen, false));
        int progress = sharedPreferences.getInt(pref_anzahlBilder, 10);
        //Werte aus Speicherung setzen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            anzahlBilder.setProgress(progress, true);
        }
        nummer.setText("" + progress + " mal die Sekunde");
        aufloesung_Position = sharedPreferences.getInt(pref_aufloesung_position, 0);
        cameraSizes.setSelection(aufloesung_Position);
        */

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

        updateGUI();
        /*
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
        */



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

    private void updateGUI(){
        automatischHochladen.setChecked(sharedPreferences.getBoolean(pref_automatischHochladen, false));
        int progress = sharedPreferences.getInt(pref_anzahlBilder, 10);
        //Werte aus Speicherung setzen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            anzahlBilder.setProgress(progress, true);
        }

        nummer.setText("" + progress + " mal die Sekunde");
        aufloesung_Position = sharedPreferences.getInt(pref_aufloesung_position, 0);
        cameraSizes.setSelection(aufloesung_Position);
        appPort = sharedPreferences.getInt(pref_appPort,10000);
        editText_appPort.setText("" + appPort);
        boardPort = sharedPreferences.getInt(pref_boardPort, 10001);
        editText_boardPort.setText("" + boardPort);
        editText_ipAdresse.setText(sharedPreferences.getString(pref_ipAdresse, ""));

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
        if(anzahlBilder.getProgress()==0){
            editor.putInt(pref_anzahlBilder, 1);
        }else {
            editor.putInt(pref_anzahlBilder, anzahlBilder.getProgress());
        }
        if(!editText_boardPort.getText().toString().equals("")){
            editor.putInt(pref_boardPort, Integer.parseInt(editText_boardPort.getText().toString()));
        }
        if(!editText_appPort.getText().toString().equals("")){
            editor.putInt(pref_appPort, Integer.parseInt(editText_appPort.getText().toString()));
        }
        editor.putString(pref_ipAdresse, editText_ipAdresse.getText().toString());

        editor.putInt(pref_aufloesung_breite, Integer.parseInt(gewaehlte_Aufloesung[0]));
        editor.putInt(pref_aufloesung_hoehe, Integer.parseInt(gewaehlte_Aufloesung[1]));
        editor.putInt(pref_aufloesung_position, aufloesung_Position);
        editor.putBoolean(pref_automatischHochladen, automatischHochladen.isChecked());
        editor.putFloat(pref_left_faktor, Float.parseFloat(gewaehlte_Aufloesung[1]) / (float) MAX_LEFT_MARGIN);
        editor.putFloat(pref_top_faktor, Float.parseFloat(gewaehlte_Aufloesung[0]) / (float) MAX_TOP_MARGIN);
        editor.commit();
    }



    public  void uploadFile(View v){
        ProgressDialog progressDialog = new ProgressDialog(EinstellungenActivity.this);
        progressDialog.setTitle("Uploading to Google Drive");
        progressDialog.setMessage("Please wait....");



        String filePath = Environment.getExternalStorageDirectory() + File.separator + "A_Project.zip";


        googleDriveHelper.createFile(filePath).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                progressDialog.dismiss();

                Toast.makeText(getApplicationContext(), "Uploaded Successfully", Toast.LENGTH_LONG).show();
            }
        })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Check your google Drive Api key", Toast.LENGTH_LONG).show();
                    }
                });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case 101:
                    try {
                        // The Task returned from this call is always completed, no need to attach
                        // a listener.
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        account = task.getResult(ApiException.class);
                        onLoggedIn(account);
                        loggedIn = true;
                        switchLogtinLogoutButtons(account);

                        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(EinstellungenActivity.this, Collections.singleton(DriveScopes.DRIVE_FILE));

                        credential.setSelectedAccount(account.getAccount());

                        Drive googleDriveService = new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential)
                                .setApplicationName(applicationName)
                                .build();

                        sharedPreferences = getSharedPreferences(prefName + account.getId(), 0);
                        googleDriveHelper = new GoogleDriveHelper(googleDriveService, sharedPreferences);


                        ProgressDialog progressDialog = new ProgressDialog(EinstellungenActivity.this);
                        progressDialog.setTitle("Uploading to Google Drive");
                        progressDialog.setMessage("Please wait....");
                        googleDriveHelper.createFolder().addOnSuccessListener(new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(String s) {
                                progressDialog.dismiss();

                                Toast.makeText(getApplicationContext(), "Uploaded Successfully", Toast.LENGTH_LONG).show();
                            }
                        })

                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "Check your google Drive Api key", Toast.LENGTH_LONG).show();
                                    }
                                });
                        googleDriveHelper.createAnroidSubFolder();
                        googleDriveHelper.createIosSubFolder();
                        googleDriveHelper.createTrainingsdatenSubFolder();
                        //googleDriveHelper.createFileInFolder();
                        //googleDriveHelper.createFileInFolder(googleDriveHelper.createFolder().toString());

                    } catch (ApiException e) {
                        // The ApiException status code indicates the detailed failure reason.
                        Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
                    }
                    break;
            }
    }







    private void onLoggedIn(GoogleSignInAccount googleSignInAccount) {
        Intent intent = new Intent(this, EinstellungenActivity.class);
        intent.putExtra(EinstellungenActivity.GOOGLE_ACCOUNT, googleSignInAccount);

        startActivity(intent);
        finish();
    }

    /*
    @Override
    public void onStart() {
        super.onStart();
        GoogleSignInAccount alreadyloggedAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (alreadyloggedAccount != null) {
            Toast.makeText(this, "Already Logged In", Toast.LENGTH_SHORT).show();
            onLoggedIn(alreadyloggedAccount);
        } else {
            Log.d(TAG, "Not logged in");
        }
    }
     */

    private void checkLogedIn(){
        GoogleSignInAccount alreadyloggedAccount = GoogleSignIn.getLastSignedInAccount(this);

        if (alreadyloggedAccount != null) {
            loggedIn = true;
            switchLogtinLogoutButtons(alreadyloggedAccount);
            Toast.makeText(this, "Already Logged In", Toast.LENGTH_SHORT).show();
            account = alreadyloggedAccount;
            GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(EinstellungenActivity.this, Collections.singleton(DriveScopes.DRIVE_FILE));
            credential.setSelectedAccount(account.getAccount());
            Drive googleDriveService = new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential)
                    .setApplicationName(applicationName)
                    .build();
            sharedPreferences = getSharedPreferences(prefName + account.getId(), 0);
            googleDriveHelper = new GoogleDriveHelper(googleDriveService, sharedPreferences);
            //onLoggedIn(alreadyloggedAccount);
        } else {
            Log.d(TAG, "Not logged in");
            loggedIn = false;
            switchLogtinLogoutButtons();
        }
    }



    private void signOut(){
        Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
        loggedIn = false;
        sharedPreferences = getSharedPreferences(prefName, 0);
        switchLogtinLogoutButtons();
    }


    void switchLogtinLogoutButtons(){
        if(loggedIn){
            signOut.setVisibility(View.VISIBLE);
            googleSignInButton.setVisibility(View.GONE);
            account_email_textView.setVisibility(View.VISIBLE);
        }else {
            signOut.setVisibility(View.GONE);
            googleSignInButton.setVisibility(View.VISIBLE);
            account_email_textView.setVisibility(View.GONE);
        }
    }

    void switchLogtinLogoutButtons(GoogleSignInAccount account){
        switchLogtinLogoutButtons();
        if(account!=null){
            account_email_textView.setText("Angemeldet mit der E-Mail: \n"+account.getEmail());
        }
    }
}

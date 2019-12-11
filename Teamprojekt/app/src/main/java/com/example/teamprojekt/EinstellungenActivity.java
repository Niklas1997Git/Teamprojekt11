package com.example.teamprojekt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
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
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonGenerator;
import com.google.api.client.json.JsonParser;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Collections;

public class EinstellungenActivity extends AppCompatActivity {

    private static final String TAG = "AndroidClarified";
    private SignInButton googleSignInButton;
    public static final String GOOGLE_ACCOUNT = "google_account";
    private boolean loggedIn;
    Button signOut;
    TextView account_email_textView;
    public static GoogleDrive googleDrive;
    GoogleSignInAccount account;

    private Switch automatischHochladen;
    private EditText email;
    private EditText passwort;
    private TextView nummer;
    private SeekBar anzahlBilder;


    private SaveClass saveClass;
    Context context;
    private String applicationName = "Teamprojekt";

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
    }







    public  void uploadFile(View v){
        ProgressDialog progressDialog = new ProgressDialog(EinstellungenActivity.this);
        progressDialog.setTitle("Uploading to Google Drive");
        progressDialog.setMessage("Please wait....");



        String filePath = Environment.getExternalStorageDirectory() + File.separator + "A_Project.zip";


        googleDrive.createFile(filePath).addOnSuccessListener(new OnSuccessListener<String>() {
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


                        googleDrive = new GoogleDrive(googleDriveService);


                        googleDrive.createFolder();


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

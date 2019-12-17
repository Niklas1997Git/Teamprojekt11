package com.example.teamprojekt;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.File;

import static com.example.teamprojekt.EinstellungenActivity.googleDriveHelper;

public class HochladenDialog extends AppCompatDialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInsatnceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Information")
                .setMessage("Sollen die Trainingsdaten ins Google Drive hochgeladen werden?")
                .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        uploadFile();
                    }
                })
                .setNegativeButton("Nein", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        return builder.create();
    }

    public  void uploadFile(){
        googleDriveHelper.createFileInFolder();
    }

}

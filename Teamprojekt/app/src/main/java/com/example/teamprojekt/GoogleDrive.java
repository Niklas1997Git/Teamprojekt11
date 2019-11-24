package com.example.teamprojekt;


import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.google.android.gms.tasks.Tasks;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.Drive;

public class GoogleDrive {


    private final Executor mExecutor = Executors.newSingleThreadExecutor();
    private Drive mDriveService;

    public GoogleDrive(Drive mDriveService){
        this.mDriveService = mDriveService;
    }

    public Task<String> createFile(String filePath){
        return Tasks.call(mExecutor, () ->{

            File fileMetaData = new File();
            fileMetaData.setName("MyPDF");

            java.io.File file = new java.io.File(filePath);

            FileContent mediaContent = new FileContent("application/zip", file);

            File myFile = null;

            try{
                myFile = mDriveService.files().create(fileMetaData, mediaContent).execute();
            }catch(Exception e){
                e.printStackTrace();
            }


            if(myFile==null){
                throw new IOException("Null result when requesting file creation");
            }


            return myFile.getId();

        });
    }
}

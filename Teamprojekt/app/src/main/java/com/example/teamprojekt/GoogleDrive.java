package com.example.teamprojekt;


import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.Collections;
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

/*
            File fileMetaData = new File()
                    .setName("MyPDF")
                    .setParents(Collections.singletonList("root"));

            java.io.File file = new java.io.File(filePath);

            FileContent mediaContent = new FileContent("image/jpeg", file);

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
*/

            File fileMetadata = new File();;
            fileMetadata.setName("A_Project");
            fileMetadata.setMimeType("application/zip");

            // For mime type of specific file visit Drive Doucumentation

            java.io.File  file2 = new java.io.File(filePath);

            FileContent mediaContent = new FileContent("application/zip.zip",file2);


            File file = mDriveService.files().create(fileMetadata, mediaContent)
                    .setFields("id")
                    .execute();

            return  file.getId();

            /*
        File metadata = new File()
                .setParents(Collections.singletonList("root"))
                .setMimeType("text/plain")
                .setName("Untitled file");



                File googleFile = mDriveService.files().create(metadata).execute();
                if (googleFile == null) {
                    throw new IOException("Null result when requesting file creation.");
                }

                return googleFile.getId();

             */
            });



    }
}

package com.example.teamprojekt;


import android.os.Environment;

import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.google.android.gms.tasks.Tasks;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.FileList;

public class GoogleDriveHelper {


    private final Executor mExecutor = Executors.newSingleThreadExecutor();
    private Drive mDriveService;

    public GoogleDriveHelper(Drive mDriveService){
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

    public Task<String> createFolder(){
        return Tasks.call(mExecutor, () ->{


            File fileMetadata = new File();;
            fileMetadata.setName("Teamprojekt");
            fileMetadata.setMimeType("application/vnd.google-apps.folder");

            // For mime type of specific file visit Drive Doucumentation
            File file = mDriveService.files().create(fileMetadata)
                    .setFields("id")
                    .execute();

            return  file.getId();

        });
    }


    public Task<String> createFileInFolder(String folderId){
        return Tasks.call(mExecutor, () ->{
            System.out.println("CreateFileInFolder");
            File fileMetadata = new File();
            fileMetadata.setName("photo.zip");
            fileMetadata.setParents(Collections.singletonList(folderId));
            String path = Environment.getExternalStorageDirectory() + java.io.File.separator + "A_Project.zip";
            java.io.File filePath = new java.io.File(path);
            FileContent mediaContent = new FileContent("application/zip.zip", filePath);
            File file = mDriveService.files().create(fileMetadata, mediaContent)
                    .setFields("id, parents")
                    .execute();
            System.out.println("File ID: " + file.getId());
            return file.getId();
        });
    }


    public Task<String> showFolder(){
        return Tasks.call(mExecutor, () ->{
            createFolder();
            System.out.println("ShowFolder");

            String pageToken = null;
            do {
                FileList result = mDriveService.files().list()
                        .setQ("mimeType='application/vnd.google-apps.folder'")
                        .setSpaces("drive")
                        .setFields("nextPageToken, files(id, name)")
                        .setPageToken(pageToken)
                        .execute();
                for (File file : result.getFiles()) {
                        System.out.printf("Found file: %s (%s)\n",
                                file.getName(), file.getId());

                        if(file.getName().equals("Teamprojekt")){
                            createFileInFolder(file.getId());
                        }


                        //createFileInFolder(file.getId());
                }
                pageToken = result.getNextPageToken();
            } while (pageToken != null);
            System.out.println("Token" + pageToken);
            return pageToken;
        });
    }

}

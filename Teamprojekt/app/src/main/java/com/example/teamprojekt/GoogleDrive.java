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
import com.google.api.services.drive.model.FileList;

public class GoogleDrive {

    private  String projectFolderId = null;
    private  String iosFolderId = null;
    private  String androidFolderId = null;
    private  String nnFileId = null;

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

    public Task<String> createFolder(){
        return Tasks.call(mExecutor, () ->{
            System.out.println("---------------CREATE FOLDER-------------------");
            System.out.println("CREATE FOLDER");
            if(projectFolderId !=null){
                System.out.println("PROJECTFOLDER != NULL");
                if(folderExists(projectFolderId).equals("true")){
                    System.out.println("PROJECTFOLDER EXISTS");
                    return projectFolderId;
                }
            }else{
                System.out.println("PROJECTFOLDER DOES NOT EXISTS");
                File fileMetadata = new File();;
                fileMetadata.setName("Project Ordner");
                fileMetadata.setMimeType("application/vnd.google-apps.folder");

                // For mime type of specific file visit Drive Doucumentation
                File file = mDriveService.files().create(fileMetadata)
                        .setFields("id")
                        .execute();
                projectFolderId = file.getId();
                System.out.println("FOLDER CREATED");
                System.out.println("---------------CREATE FOLDER-------------------");
                return  file.getId();
            }
            System.out.println("---------------CREATE FOLDER-------------------");
            return  null;
        });



    }

    public Task<String> folderExists(String fileId){
        return Tasks.call(mExecutor, () ->{
            System.out.println("----------------FOLDER EXISTS--------------------");
            String pageToken = null;
            do {
                FileList result = mDriveService.files().list()
                        .setQ("mimeType='application/vnd.google-apps.folder'")
                        .setSpaces("drive")
                        .setCorpora("user")
                        .setFields("nextPageToken, files(id, name)")
                        .setPageToken(pageToken)
                        .execute();
                for (File file : result.getFiles()) {
                    System.out.printf("Found file: %s (%s)\n",
                            file.getName(), file.getId());
                    if(file.getId().equals(fileId)){
                        System.out.println("FOLDER EXISTS");
                        System.out.println("----------------FOLDER EXISTS--------------------");
                        return "true";
                    }
                }
                pageToken = result.getNextPageToken();
            } while (pageToken != null);
            System.out.println("FOLDER DOES NOT EXIST");
            System.out.println("----------------FOLDER EXISTS--------------------");
            return  "false";
        });



    }



}

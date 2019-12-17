package com.example.teamprojekt;


import android.content.SharedPreferences;
import android.os.Environment;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.google.android.gms.tasks.Tasks;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.FileList;

public class GoogleDriveHelper {

    private  String projectFolderId = null;
    private  String iosFolderId = null;
    private  String androidFolderId = null;
    private  String trainingsdatenFolderId = null;
    private  String nnFileId = null;

    private final String pref_projectFolder = "projectFolder";
    private final String pref_iosFolder = "iosFolder";
    private final String pref_androidFolder = "androidFolder";
    private final String pref_trainingsdatenFolder = "trainingsdatenFolder";
    private final String pref_nnFile = "nnFile";

    private final Executor mExecutor = Executors.newSingleThreadExecutor();
    private Drive mDriveService;
    SharedPreferences preferences;

    Calendar kalender;
    SimpleDateFormat datumsformat = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss");

    String[] namen;
    String[] ids;
    public GoogleDriveHelper(Drive mDriveService, SharedPreferences pref){
        this.mDriveService = mDriveService;
        preferences = pref;
        loadIDs();
    }

    public String[] getNamen() {
        return namen;
    }

    public String[] getIds() {
        return ids;
    }

    public void loadIDs(){
        projectFolderId = preferences.getString(pref_projectFolder, null);
        iosFolderId = preferences.getString(pref_iosFolder, null);
        androidFolderId = preferences.getString(pref_androidFolder, null);
        nnFileId = preferences.getString(pref_nnFile, null);
        trainingsdatenFolderId = preferences.getString(pref_trainingsdatenFolder, null);
        System.out.println(projectFolderId);
        System.out.println(iosFolderId);
        System.out.println(androidFolderId);
        System.out.println(nnFileId);
        System.out.println(trainingsdatenFolderId);
    }

    public Task<String> createFile(String filePath){
        return Tasks.call(mExecutor, () ->{


            kalender = Calendar.getInstance();
            String zipName = datumsformat.format(kalender.getTime());
            File fileMetadata = new File();;
            fileMetadata.setName("Trainingsdaten-"+zipName);
            fileMetadata.setMimeType("application/zip");

            // For mime type of specific file visit Drive Doucumentation

            java.io.File  file2 = new java.io.File(filePath);

            FileContent mediaContent = new FileContent("application/zip.zip",file2);


            File file = mDriveService.files().create(fileMetadata, mediaContent)
                    .setFields("id")
                    .execute();

            return  file.getId();

        });



    }

    public Task<String> createFileInFolder(){
        return Tasks.call(mExecutor, () ->{
            System.out.println("---------------CREATE FILE IN FOLDER-------------------");
            if(folderExists(projectFolderId).equals("false")){
                System.out.println("CREATE PROJECTFOLDER");
                projectFolderId = createFolder().toString();
            }
            if(folderExists(trainingsdatenFolderId).equals("false")){
                System.out.println("CREATE TRAININGSDATENFOLDER");
                trainingsdatenFolderId = createTrainingsdatenSubFolder().toString();
            }
            System.out.println("CreateFileInFolder");
            kalender = Calendar.getInstance();
            String zipName = datumsformat.format(kalender.getTime());
            File fileMetadata = new File();;
            fileMetadata.setName("Trainingsdaten-"+zipName);

            fileMetadata.setMimeType("application/zip");
            fileMetadata.setParents(Collections.singletonList(trainingsdatenFolderId));
            String path = Environment.getExternalStorageDirectory() + java.io.File.separator + "A_Project.zip";

            java.io.File filePath = new java.io.File(path);
            FileContent mediaContent = new FileContent("application/zip.zip", filePath);
            File file = mDriveService.files().create(fileMetadata, mediaContent)
                    .setFields("id, parents")
                    .execute();
            System.out.println("File ID: " + file.getId());
            System.out.println("---------------CREATE FILE IN FOLDER-------------------");
            return file.getId();
        });
    }

    public Task<String> createFolder(){
        return Tasks.call(mExecutor, () ->{
            System.out.println("---------------CREATE PROJECTFOLDER-------------------");
            System.out.println("CREATE PROJECTFOLDER");
            if(projectFolderId != null){
                System.out.println("PROJECTFOLDER != NULL");
                if(folderExists(projectFolderId).equals("true")){
                    System.out.println("PROJECTFOLDER EXISTS");
                    return projectFolderId;
                }
            }else{
                System.out.println("PROJECTFOLDER DOES NOT EXISTS");
                File fileMetadata = new File();;
                fileMetadata.setName("Auto");
                fileMetadata.setMimeType("application/vnd.google-apps.folder");

                // For mime type of specific file visit Drive Doucumentation
                File file = mDriveService.files().create(fileMetadata)
                        .setFields("id")
                        .execute();
                projectFolderId = file.getId();
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(pref_projectFolder, projectFolderId);
                editor.commit();
                System.out.println("PROJECTFOLDER CREATED");
                System.out.println("---------------CREATE PROJECTFOLDER-------------------");
                return  file.getId();
            }
            System.out.println("---------------CREATE PROJECTFOLDER-------------------");
            return  null;
        });
    }

    public Task<String> createAnroidSubFolder(){
        return Tasks.call(mExecutor, () ->{
            System.out.println("---------------CREATE ANDROIDFOLDER-------------------");
            if(androidFolderId !=null){
                System.out.println("ANDROIDFOLDER != NULL");
                if(folderExists(androidFolderId).equals("true")){
                    System.out.println("ANDROIDFOLDER EXISTS");
                    return androidFolderId;
                }
            }else{
                System.out.println("ANDROIDFOLDER DOES NOT EXISTS");
                File fileMetadata = new File();;
                fileMetadata.setName("Android");
                fileMetadata.setParents(Collections.singletonList(projectFolderId));
                fileMetadata.setMimeType("application/vnd.google-apps.folder");

                // For mime type of specific file visit Drive Doucumentation
                File file = mDriveService.files().create(fileMetadata)
                        .setFields("id, parents")
                        .execute();
                androidFolderId = file.getId();
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(pref_androidFolder, androidFolderId);
                editor.commit();
                System.out.println("ANDROIDFOLDER CREATED");
                System.out.println("---------------CREATE ANDROIDFOLDER-------------------");
                return  file.getId();
            }
            System.out.println("---------------CREATE ANDROIDFOLDER-------------------");
            return  null;
        });
    }



    public Task<String> createIosSubFolder(){
        return Tasks.call(mExecutor, () ->{
            System.out.println("---------------CREATE IOSFOLDER-------------------");
            if(iosFolderId !=null){
                System.out.println("IOSFOLDER != NULL");
                if(folderExists(iosFolderId).equals("true")){
                    System.out.println("IOSFOLDER EXISTS");
                    return iosFolderId;
                }
            }else{
                System.out.println("IOSFOLDER DOES NOT EXISTS");
                File fileMetadata = new File();;
                fileMetadata.setName("IOS");
                fileMetadata.setParents(Collections.singletonList(projectFolderId));
                fileMetadata.setMimeType("application/vnd.google-apps.folder");

                // For mime type of specific file visit Drive Doucumentation
                File file = mDriveService.files().create(fileMetadata)
                        .setFields("id, parents")
                        .execute();
                iosFolderId = file.getId();
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(pref_iosFolder, iosFolderId);
                editor.commit();
                System.out.println("IOSFOLDER CREATED");
                System.out.println("---------------CREATE IOSFOLDER-------------------");
                return  file.getId();
            }
            System.out.println("---------------CREATE IOSFOLDER-------------------");
            return  null;
        });
    }

    public Task<String> createTrainingsdatenSubFolder(){
        return Tasks.call(mExecutor, () ->{
            System.out.println("---------------CREATE TRAININGSDATENFOLDER-------------------");
            if(trainingsdatenFolderId !=null){
                System.out.println("TRAININGSDATENFOLDER != NULL");
                if(folderExists(trainingsdatenFolderId).equals("true")){
                    System.out.println("TRAININGSDATENFOLDER EXISTS");
                    return trainingsdatenFolderId;
                }
            }else{
                System.out.println("TRAININGSDATENFOLDER DOES NOT EXISTS");
                File fileMetadata = new File();;
                fileMetadata.setName("Trainingsdaten");
                fileMetadata.setParents(Collections.singletonList(projectFolderId));
                fileMetadata.setMimeType("application/vnd.google-apps.folder");

                // For mime type of specific file visit Drive Doucumentation
                File file = mDriveService.files().create(fileMetadata)
                        .setFields("id, parents")
                        .execute();
                trainingsdatenFolderId = file.getId();
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(pref_trainingsdatenFolder, trainingsdatenFolderId);
                editor.commit();
                System.out.println("TRAININGSDATENFOLDER CREATED");
                System.out.println("---------------CREATE TRAININGSDATENFOLDER-------------------");
                return  file.getId();
            }
            System.out.println("---------------CREATE TRAININGSDATENFOLDER-------------------");
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

    public Task<String> tensorflowFiles(){
        return Tasks.call(mExecutor, () ->{
            System.out.println("----------------TensorFlow--------------------");
            String dateien = "";
            String pageToken = null;
            do {
                System.out.println("----------------DOSChleife--------------------");
                FileList result = mDriveService.files().list()
                        //.setQ("mimeType != 'application/vnd.google-apps.folder'")
                        .setSpaces("drive")
                        .setCorpora("user")
                        .setFields("nextPageToken, files(id, name)")
                        .setPageToken(pageToken)
                        .execute();
                namen = new String[result.getFiles().size()];
                ids = new String[result.getFiles().size()];
                for (File file : result.getFiles()) {
                    System.out.println("----------------1 For--------------------");
                    System.out.printf("Found file: %s (%s)\n",
                            file.getName(), file.getId());

                }
                for(int i=0;i<result.getFiles().size(); i++){
                    System.out.println("----------------2 FOr--------------------");
                    namen[i] = result.getFiles().get(i).getName();
                    ids[i] = result.getFiles().get(i).getId();
                    if(i==0){
                        dateien = dateien + result.getFiles().get(i).getId();
                    }else{
                        dateien = dateien + ", " + result.getFiles().get(i).getId();
                    }
                }
                pageToken = result.getNextPageToken();
            } while (pageToken != null);
            System.out.println("----------------TensorFlow--------------------");
            return  dateien;
        });
    }
}

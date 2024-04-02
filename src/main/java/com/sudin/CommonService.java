package com.sudin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sudin.entity.UploadTextEntity;
import com.sudin.storage.StorageException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CommonService {

    private static String DATE_TIME_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
    private static String DATE_FORMAT_NOW = "yyyy-MM-dd";


    public static String saveTextToFile(String data, String userName, String originPath) {
        String fileName = "";
        try {
            fileName = userName + getDateTimeNow() + ".txt";
            String pathNameUser = originPath + userName;
            File theDir = new File(pathNameUser);
            if (!theDir.exists()) {
                theDir.mkdirs();
            }
            String pathNameUserData = pathNameUser + "/text/";
            File theDirData = new File(pathNameUserData);
            if (!theDirData.exists()) {
                theDirData.mkdirs();
            }
            String pathSaveFileForDate = pathNameUserData + "/" + getDateNow().replace("-", "_");
            File theDirDate = new File(pathSaveFileForDate);
            if (!theDirDate.exists()) {
                theDirDate.mkdirs();
            }
            fileName = pathSaveFileForDate + "/" + fileName;
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            writer.write(data);

            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fileName;
    }

    public static String saveDataToFile(UploadTextEntity uploadTextEntity, String originPath) {
        String fileName = "data/data.txt";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String data = objectMapper.writeValueAsString(uploadTextEntity) + "\r\n";
            File file = new File(originPath + fileName);//cr
            File theDir = new File(originPath + "data");
            if (!theDir.exists()) {
                theDir.mkdirs();
            }
            if (file.exists() && !file.isDirectory()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(originPath + fileName, true);
            fos.write(data.getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fileName;
    }

    public static UploadTextEntity getFileFromData(String username, String originPath) {
        String fileName = "data/data.txt";
        List<UploadTextEntity> uploadTextEntities = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        UploadTextEntity uploadText = new UploadTextEntity();
        try {
            File file = new File(originPath + fileName);//creates a new file instance

            FileReader fr = new FileReader(file);   //reads the file
            BufferedReader br = new BufferedReader(fr);  //creates a buffering character input stream
            String line;
            while ((line = br.readLine()) != null) {
                Map o = (LinkedHashMap) objectMapper.readValue(line, Object.class);
                if (username.equals(String.valueOf(o.get("userId")))) {
                    UploadTextEntity temp = UploadTextEntity.builder()
                            .userId(String.valueOf(o.get("userId")))
                            .path(String.valueOf(o.get("path"))).build();
                    temp.setCreatedDate(String.valueOf(o.get("createdDate")));
                    uploadTextEntities.add(temp);
                }
            }
            fr.close();
            if (uploadTextEntities.size() == 1) {
                uploadText = uploadTextEntities.get(0);
            } else {
                uploadText = uploadTextEntities.stream().max((o1, o2) -> {
                    return o1.getCreatedDate().compareTo(o2.getCreatedDate());
                }).get();
            }

            FileReader fileReaderResult = new FileReader(uploadText.getPath());
            BufferedReader bufferedReader = new BufferedReader(fileReaderResult);

            uploadText.setContent(bufferedReader.readLine());

        } catch (Exception e) {
            e.printStackTrace();
        }


        return uploadText;
    }


    public static String getDateTimeNow() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("_yyyy-MM-dd_HH:mm:ss:");
        return sdf.format(cal.getTime()).replaceFirst(":", "h").replaceFirst(":", "m").replaceFirst(":", "s");
    }

    public static String getDateNow() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        return sdf.format(cal.getTime());
    }

    public static String genUUID() {
        return UUID.randomUUID().toString();
    }

    public static void newFolder(Path path) {
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }

    public static String convertToDateTime(String timeIn) {
        Instant instant = Instant.parse(timeIn);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        String formattedDateString = localDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss"));
        return formattedDateString;
    }

    public static String cleanFileName(String originalFileName) {
        return originalFileName.replaceAll("[^a-zA-Z0-9.-]", "");
    }
}

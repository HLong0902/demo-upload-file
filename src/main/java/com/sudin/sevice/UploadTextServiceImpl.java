package com.sudin.sevice;


import com.sudin.CommonService;
import com.sudin.entity.UploadTextEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.sudin.CommonService.getFileFromData;
import static com.sudin.CommonService.saveDataToFile;

@Service
public class UploadTextServiceImpl implements UploadTextService {


    @Value("${file.path}")
    private String pathSaveFile;


    @Override
    public void saveTextToFile(String data, String username) {
        UploadTextEntity uploadTextEntity = new UploadTextEntity();
        uploadTextEntity.setUserId(username);
        uploadTextEntity.setCreatedDate(LocalDateTime.now());
        uploadTextEntity.setPath(CommonService.saveTextToFile(data, username, pathSaveFile));
        saveDataToFile(uploadTextEntity, pathSaveFile);
    }

    @Override
    public UploadTextEntity getTextForUser(String username) {
        return getFileFromData(username, pathSaveFile);
    }
}

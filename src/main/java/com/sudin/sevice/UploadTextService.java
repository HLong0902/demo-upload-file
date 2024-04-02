package com.sudin.sevice;

import com.sudin.entity.UploadTextEntity;

public interface UploadTextService {

    public void saveTextToFile(String data, String username);

    public UploadTextEntity getTextForUser(String username);
}

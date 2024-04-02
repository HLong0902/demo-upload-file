package com.sudin.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class RenamedMultipartFile implements MultipartFile {

    private final MultipartFile originalFile;
    private final String newFilename;

    public RenamedMultipartFile(MultipartFile originalFile, String newFilename) {
        this.originalFile = originalFile;
        this.newFilename = newFilename;
    }

    @Override
    public String getName() {
        return originalFile.getName();
    }

    @Override
    public String getOriginalFilename() {
        return newFilename;
    }

    @Override
    public String getContentType() {
        return originalFile.getContentType();
    }

    @Override
    public boolean isEmpty() {
        return originalFile.isEmpty();
    }

    @Override
    public long getSize() {
        return originalFile.getSize();
    }

    @Override
    public byte[] getBytes() throws IOException {
        return originalFile.getBytes();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return originalFile.getInputStream();
    }

    @Override
    public void transferTo(File file) throws IOException, IllegalStateException {
        originalFile.transferTo(file);
    }
}


package com.sudin.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadTextEntity {
    private String userId;
    private String path;
    private LocalDateTime createdDate;
    private String content;

    public String getCreatedDate() {
        if (createdDate != null) return createdDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        return "";
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = LocalDateTime.parse(createdDate, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }
}

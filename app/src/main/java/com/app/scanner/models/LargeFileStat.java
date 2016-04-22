package com.app.scanner.models;


import java.io.File;
import java.io.Serializable;

public class LargeFileStat implements Serializable{
    private String name;
    private String fileSize;

    public LargeFileStat(File file) {
        this.name = file.getName();
        this.fileSize = file.length()+" bytes";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }
}

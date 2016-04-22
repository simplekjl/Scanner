package com.app.scanner.models;

import java.io.Serializable;

public class FreqFileStat implements Serializable{
    private String ext;
    private String count;

    public FreqFileStat(String ext, String count) {
        this.ext = ext;
        this.count = count;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}

package com.app.scanner.models;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.List;

public class Stats implements Serializable{
    private List<FreqFileStat> frequentExtensions;
    private List<LargeFileStat> largestFileNames;
    private String avgFileSize;

    public Stats(List<FreqFileStat> frequentExtensions, List<LargeFileStat> largestFileNames, String avgFileSize) {
        this.frequentExtensions = frequentExtensions;
        this.largestFileNames = largestFileNames;
        this.avgFileSize = avgFileSize;
    }

    public List<FreqFileStat> getFrequentExtensions() {
        return frequentExtensions;
    }

    public void setFrequentExtensions(List<FreqFileStat> frequentExtensions) {
        this.frequentExtensions = frequentExtensions;
    }

    public List<LargeFileStat> getLargestFileNames() {
        return largestFileNames;
    }

    public void setLargestFileNames(List<LargeFileStat> largestFileNames) {
        this.largestFileNames = largestFileNames;
    }

    public String getAvgFileSize() {
        return avgFileSize;
    }

    public void setAvgFileSize(String avgFileSize) {
        this.avgFileSize = avgFileSize;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}

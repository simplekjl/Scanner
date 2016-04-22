package com.app.scanner;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.app.scanner.commons.BundleConstants;
import com.app.scanner.commons.EventConstants;
import com.app.scanner.models.FreqFileStat;
import com.app.scanner.models.LargeFileStat;
import com.app.scanner.models.Stats;
import com.app.scanner.services.DirectoryService;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DirectoryScanner {

    private List<File> largeFiles = new ArrayList<>();
    private long fileCount = 0;
    private long scannedFiles = 0;
    private Double fileSize = 0.0;
    private Map<String, Long> extensions = new HashMap<>();
    private Context context;

    public DirectoryScanner(Context context) {
        this.context = context;
    }

    public Stats startScan(File root) {
        count(root);
        run(root);
        return parseStats();
    }

    public void count(File root) {
        if (!DirectoryService.shouldContinue) {
            return;
        }
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        File[] list = root.listFiles();
        for (File f : list) {
            if (f.isDirectory()) {
                count(f);
            } else {
                fileCount++;
                sendMessage("Counting Files...\n" + fileCount);
            }
        }
    }

    public void run(File root) {
        if (!DirectoryService.shouldContinue) {
            return;
        }
        File[] list = root.listFiles();
        for (File f : list) {
            if (f.isDirectory()) {
                run(f);
            } else {
                fileStats(f);
            }
        }
    }

    private void fileStats(File f) {
        if (!DirectoryService.shouldContinue) {
            return;
        }
        scannedFiles++;
        sendProgress((int) (scannedFiles * 100 / fileCount));
        long fileSizeInBytes = f.length();
        fileSize += (fileSizeInBytes / (1024.0 * 1024.0 * 1024.0)); // converting to gb
        Log.d("DirectoryScanner", "File: " + f.getAbsoluteFile() + " " + fileSize);
        addIfLarge(f);
        addExtension(f);
    }

    private void addExtension(File f) {
        if (!DirectoryService.shouldContinue) {
            return;
        }
        String ext = getExt(f.getName());
        if (ext == null) {
            return;
        }
        if (extensions.containsKey(ext)) {
            extensions.put(ext, extensions.get(ext) + 1);
        } else {
            extensions.put(ext, (long) 1);
        }
    }

    private String getExt(String fileName) {
        if (!DirectoryService.shouldContinue) {
            return null;
        }
        String extension = "No Extension";
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i + 1);
        }
        return extension;
    }

    private void addIfLarge(File f) {
        if (!DirectoryService.shouldContinue) {
            return;
        }
        if (largeFiles.size() < 10) {
            largeFiles.add(f);
            sort();
        } else if (largeFiles.get(9).length() < f.length()) {
            largeFiles.remove(9);
            largeFiles.add(f);
            sort();
        }
    }

    private void sort() {
        if (!DirectoryService.shouldContinue) {
            return;
        }
        Collections.sort(largeFiles, new Comparator<File>() {
            @Override
            public int compare(File file, File t1) {
                return (int) (t1.length() - file.length());
            }
        });
    }

    public Stats parseStats() {
        if (!DirectoryService.shouldContinue) {
            return null;
        }
        Map<Integer, String> map = sortByValues(extensions);
        if (map == null) {
            return null;
        }
        Set set = map.entrySet();
        Iterator iterator2 = set.iterator();
        List<FreqFileStat> list1 = new ArrayList<>();
        int maxFiles = 0;
        while (iterator2.hasNext()) {
            Map.Entry me2 = (Map.Entry) iterator2.next();
            list1.add(new FreqFileStat(me2.getKey() + "", me2.getValue() + ""));
            maxFiles++;
            if (maxFiles == 5) {
                break;
            }
        }

        List<LargeFileStat> list2 = new ArrayList<>();
        for (int i = 0, j = 0; i < largeFiles.size() && j < 10; i++, j++) {
            list2.add(new LargeFileStat(largeFiles.get(i)));
        }

        String avg = (fileSize / (double) fileCount) + " GB";
        Stats stats = new Stats(list1, list2, avg);

        System.out.println(stats.toString());
        return stats;
    }

    private HashMap sortByValues(Map map) {
        if (!DirectoryService.shouldContinue) {
            return null;
        }
        List list = new LinkedList(map.entrySet());
        // Defined Custom Comparator here
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o2)).getValue())
                        .compareTo(((Map.Entry) (o1)).getValue());
            }
        });
        // Here I am copying the sorted list in HashMap
        // using LinkedHashMap to preserve the insertion order
        HashMap sortedHashMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;
    }

    private void sendMessage(String msg) {
        if (!DirectoryService.shouldContinue) {
            return;
        }
        Intent intent = new Intent(EventConstants.BROADCAST_COUNT);
        Bundle bundle = new Bundle();
        bundle.putString(BundleConstants.FILE_COUNT, msg);
        intent.putExtras(bundle);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    private void sendProgress(int msg) {
        if (!DirectoryService.shouldContinue) {
            return;
        }
        Intent intent = new Intent(EventConstants.BROADCAST_UPDATE);
        Bundle bundle = new Bundle();
        bundle.putInt(BundleConstants.PROGRESS_PERCENT, msg);
        intent.putExtras(bundle);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}

package com.app.scanner.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;

import com.app.scanner.DirectoryScanner;
import com.app.scanner.R;
import com.app.scanner.activities.MainActivity;
import com.app.scanner.activities.StatsActivity;
import com.app.scanner.commons.BundleConstants;
import com.app.scanner.commons.EventConstants;
import com.app.scanner.models.Stats;

import java.io.File;

public class DirectoryService extends IntentService {

    private Context context;
    public static volatile boolean shouldContinue = true;

    public DirectoryService() {
        super("DirectoryService");
        context = this;
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        final String MEDIA_PATH = Environment.getExternalStorageDirectory()
                .getPath() + "/";

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(R.drawable.ic_launcher);
        mBuilder.setContentTitle(getString(R.string.notification_alert_msg));
        mBuilder.setContentText(getString(R.string.scanner_progress_msg));
        mBuilder.setOngoing(true);
        final Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mBuilder.setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // NOTIF_ID allows you to update the notification later on.
        mNotificationManager.notify(EventConstants.NOTIF_ID, mBuilder.build());
        Stats stats = new DirectoryScanner(context).startScan(new File(MEDIA_PATH));
        if(shouldContinue) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(BundleConstants.DATA, stats);
            Intent intent = new Intent(context, StatsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtras(bundle);
            startActivity(intent);

            Intent intent2 = new Intent(EventConstants.BROADCAST_FINISH);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent2);
        }

        mNotificationManager.cancelAll();
    }

}
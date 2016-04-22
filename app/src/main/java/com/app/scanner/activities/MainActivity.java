package com.app.scanner.activities;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.app.scanner.services.DirectoryService;
import com.app.scanner.R;
import com.app.scanner.commons.BundleConstants;
import com.app.scanner.commons.EventConstants;
import com.github.lzyzsd.circleprogress.ArcProgress;

public class MainActivity extends AppCompatActivity {

    private boolean isRunning;
    private TextView progress;
    private ArcProgress arcProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progress = (TextView) findViewById(R.id.text);
        arcProgress = (ArcProgress) findViewById(R.id.arc_progress);
        registerBroadcastReceiver();
        start();
    }

    private void start() {
        DirectoryService.shouldContinue = true;
        isRunning = true;
        Intent mServiceIntent = new Intent(this, DirectoryService.class);
        startService(mServiceIntent);
    }

    private void stop() {
        DirectoryService.shouldContinue = false;
        progress.setText("");
        arcProgress.setProgress(0);
        isRunning = false;
        Toast.makeText(this, R.string.scanner_stop_msg, Toast.LENGTH_SHORT).show();
    }

    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.app.scanner.services.DirectoryService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stop();
        this.finish();
    }

    private BroadcastReceiver progressReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            int progress = bundle.getInt(BundleConstants.PROGRESS_PERCENT);
            arcProgress.setProgress(progress);

        }
    };
    private BroadcastReceiver countReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            String progress = bundle.getString(BundleConstants.FILE_COUNT);
            updateCount(progress);

        }
    };

    private BroadcastReceiver finishReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    private void updateCount(String progress) {
        this.progress.setText(progress);
    }

    private void registerBroadcastReceiver() {
        LocalBroadcastManager.getInstance(this).registerReceiver(progressReceiver,
                new IntentFilter(EventConstants.BROADCAST_UPDATE));
        LocalBroadcastManager.getInstance(this).registerReceiver(countReceiver,
                new IntentFilter(EventConstants.BROADCAST_COUNT));
        LocalBroadcastManager.getInstance(this).registerReceiver(finishReceiver,
                new IntentFilter(EventConstants.BROADCAST_FINISH));

    }

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(progressReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(countReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(finishReceiver);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            if (isRunning) {
                stop();
                item.setTitle(R.string.start_msg);
            } else {
                start();
                item.setTitle(R.string.stop_msg);
            }
        }
        return super.onOptionsItemSelected(item);
    }
}

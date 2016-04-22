package com.app.scanner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.app.scanner.R;
import com.app.scanner.commons.BundleConstants;
import com.app.scanner.models.FreqFileStat;
import com.app.scanner.models.LargeFileStat;
import com.app.scanner.models.Stats;

public class StatsActivity extends AppCompatActivity {

    private FloatingActionButton shareButton;
    private TextView average;
    private TextView frequent;
    private TextView large;
    private Stats data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        shareButton = (FloatingActionButton) findViewById(R.id.fab_share);

        average = (TextView) findViewById(R.id.avg_value);
        frequent = (TextView) findViewById(R.id.freq_value);
        large = (TextView) findViewById(R.id.large_value);
        Bundle bundle = getIntent().getExtras();
        data = (Stats) bundle.getSerializable(BundleConstants.DATA);

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share(data.toString());
            }
        });

        setData();
    }

    private void setData() {
        average.setText(data.getAvgFileSize());
        StringBuilder sbFreq = new StringBuilder();
        for (FreqFileStat freqStat : data.getFrequentExtensions()) {
            sbFreq.append(freqStat.getExt()).append(" ").append(freqStat.getCount()).append("\n");
        }

        StringBuilder sbLarge = new StringBuilder();
        for (LargeFileStat largeStat : data.getLargestFileNames()) {
            sbLarge.append(largeStat.getName()).append("\n").append(largeStat.getFileSize()).append("\n\n");
        }

        frequent.setText(sbFreq.toString());
        large.setText(sbLarge.toString());
    }

    private void share(String shareString) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareString);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

}

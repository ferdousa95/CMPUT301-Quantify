package com.example.quantify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;


import java.util.ArrayList;

public class BinomialHistogramActivity extends AppCompatActivity {

    int successCount;
    int failCount;
    private BarChart barchart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.binomial_histogram);

        Intent intent = getIntent();
        successCount =  getIntent().getIntExtra("success",0);
        failCount =  getIntent().getIntExtra("fail",0);

        barchart = (BarChart) findViewById(R.id.binomialHistogram);

        XAxis xAxis = barchart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        // this is the y-axis info
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(failCount, 0));
        barEntries.add(new BarEntry(successCount,1));

        BarDataSet barDataSet = new BarDataSet(barEntries,"Counts");

        ArrayList<String> outcomes = new ArrayList<>();
        outcomes.add("Failure");
        outcomes.add("Success");

        //Log.d("histogram", String.valueOf(barchart.getXAxis().getLabelCount()));
        BarData theData = new BarData(outcomes,barDataSet);
        barchart.setData(theData);

        barchart.setTouchEnabled(true);
        barchart.setDragEnabled(true);
        barchart.setScaleEnabled(true);

    }
}
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

public class OtherHistogramActivity extends AppCompatActivity {

    ArrayList<String> Trial_list;
    ArrayList<Integer> Count_list;
    BarChart barchart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_histogram);

        Intent intent = getIntent();
        Count_list =  getIntent().getIntegerArrayListExtra("y-axis");
        Trial_list =  getIntent().getStringArrayListExtra("x-axis");
        barchart = (BarChart) findViewById(R.id.otherHistogram);

        XAxis xAxis = barchart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        Log.d("count",Count_list.toString());
        Log.d("count",String.valueOf(Count_list.size()));


        ArrayList<BarEntry> barEntries = new ArrayList<>();

        for (int counter = 0; counter < Count_list.size(); counter++) {
            barEntries.add(new BarEntry(Count_list.get(counter), counter));
        }

        BarDataSet barDataSet = new BarDataSet(barEntries,"Counts");

        ArrayList<String> outcomes = new ArrayList<>();

        for (int counter = 0; counter < Trial_list.size(); counter++) {
            outcomes.add(Trial_list.get(counter));
        }

        //Log.d("histogram", String.valueOf(barchart.getXAxis().getLabelCount()));
        BarData theData = new BarData(outcomes,barDataSet);
        barchart.setData(theData);

        barchart.setTouchEnabled(true);
        barchart.setDragEnabled(true);
        barchart.setScaleEnabled(true);
    }
}
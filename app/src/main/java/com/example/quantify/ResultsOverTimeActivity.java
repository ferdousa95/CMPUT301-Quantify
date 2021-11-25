package com.example.quantify;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;

public class ResultsOverTimeActivity extends AppCompatActivity {

    ArrayList<String> result_trial_list;
    ArrayList<Integer> Count_list;
    private BarChart barchart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results_over_time_histogram);

        Count_list =  getIntent().getIntegerArrayListExtra("y-axis");
        result_trial_list =  getIntent().getStringArrayListExtra("x-axis");
        barchart = (BarChart) findViewById(R.id.resultsOverTimeHistogram);

        XAxis xAxis = barchart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        Log.d("count",Count_list.toString());


        ArrayList<BarEntry> barEntries = new ArrayList<>();

        for (int counter = 0; counter < Count_list.size(); counter++) {
            barEntries.add(new BarEntry(Count_list.get(counter), counter));
        }

        BarDataSet barDataSet = new BarDataSet(barEntries,"Counts");

        ArrayList<String> outcomes = new ArrayList<>();

        for (int counter = 0; counter < result_trial_list.size(); counter++) {
            outcomes.add(result_trial_list.get(counter));
        }

        //Log.d("histogram", String.valueOf(barchart.getXAxis().getLabelCount()));
        BarData theData = new BarData(outcomes,barDataSet);
        barchart.setData(theData);

        barchart.setTouchEnabled(true);
        barchart.setDragEnabled(true);
        barchart.setScaleEnabled(true);
    }
}
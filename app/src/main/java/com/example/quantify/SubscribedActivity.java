package com.example.quantify;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;


public class SubscribedActivity extends AppCompatActivity {

    Intent intent;

    ListView subscribedList;

    ArrayAdapter<Experiment> subscribedExperimentAdapter;

    ArrayList<Experiment> subscribedExperimentDataList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subscribed);
        Log.d("BLABLA","Here");
        subscribedList = findViewById(R.id.sub_exp_list);

        intent = getIntent();
        if(intent!=null){
            subscribedExperimentDataList = (ArrayList<Experiment>) getIntent().getSerializableExtra("subscribed");
        }

        subscribedExperimentAdapter = new ExperimenterExperimentList(SubscribedActivity.this, subscribedExperimentDataList, new ArrayList<>());

        subscribedList.setAdapter(subscribedExperimentAdapter);
        //Log.d("BLABLA",subscribedExperimentDataList.toString());
    }
}
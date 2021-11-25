package com.example.quantify;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.UUID;

public class SearchActivity extends AppCompatActivity {

    ListView resultView;
    ArrayAdapter<Experiment> resultAdapter;
    ArrayList<Experiment> dataList;
    ArrayList<Experiment> resultList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Log.d("myTag", "This is test message");

        resultView = findViewById(R.id.test_search_view);

        dataList = new ArrayList<>();
        resultList = new ArrayList<>();

        ArrayList<Experiment> subscribeList = new ArrayList<>();

        resultAdapter = new ExperimenterExperimentList(SearchActivity.this, resultList, subscribeList);
        resultView.setAdapter(resultAdapter);


//        experimenterExperimentAdapter = new ExperimenterExperimentList(MainActivity.this, experimenterExperimentDataList, subscribedExperimentDataList);

        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("Experiments");

        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                dataList.clear();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    if(doc.getData().get("Experiment ID") != null
                            && doc.getData().get("Experiment User")!= null
                            && doc.getData().get("Experiment Status")!= null
                            && doc.getData().get("Experiment Type")!= null
                            && doc.getData().get("Experiment Location")!= null
                            && doc.getData().get("Min Trials")!= null) {
                        //Log.d("TAG", String.valueOf(doc.getData().get("Province Name")));
                        UUID experiment_id = UUID.fromString((String) doc.getData().get("Experiment ID"));
                        String experiment_description = doc.getId();
                        String experiment_username = (String) doc.getData().get("Experiment User");
                        String experiment_status = (String) doc.getData().get("Experiment Status");
                        String experiment_type = (String) doc.getData().get("Experiment Type");
                        String experiment_location = (String) doc.getData().get("Experiment Location");
                        Integer experiment_min_trials = 1;
                        try {
                            experiment_min_trials = Integer.valueOf((String) doc.getData().get("Min Trials"));
                        } catch (Exception e) {
                            experiment_min_trials = 0;
                        }
                        Log.d("TAG", experiment_username);
                        dataList.add(new Experiment(experiment_id, experiment_description, experiment_username, experiment_status, experiment_type, experiment_min_trials, experiment_location)); // Adding the cities and provinces from FireStore
                    }
                }

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.nav_search, menu);

        MenuItem search = menu.findItem(R.id.searchBar);
        SearchView searchView = (SearchView) search.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Toast.makeText(SearchActivity.this, "SEARCH " + query, Toast.LENGTH_LONG).show();
                searchExps(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Toast.makeText(SearchActivity.this, "SEARCH " + newText, Toast.LENGTH_LONG).show();
                searchExps(newText);
                return false;
            }
        });

        searchView.setQueryHint("search something");

        return true;

    }

    private void updateListExps(ArrayList<Experiment> listExps) {

        // Sort the list by date
//        Collections.sort(listExps, new Comparator<Experiment>() {
//            @Override
//            public int compare(Experiment o1, Experiment o2) {
//                int res = -1;
//                if (o1.getDate() > (o2.getDate())) {
//                    res = 1;
//                }
//                return res;
//            }
//        });

        resultList.clear();
        resultList.addAll(listExps);

        resultAdapter.notifyDataSetChanged();
    }


    private void searchExps(String queryText) {
//        if (queryText.length() > 0)
//            queryText = queryText.substring(0, 1).toUpperCase() + queryText.substring(1).toLowerCase();

        ArrayList<Experiment> results = new ArrayList<>();

        for(Experiment exp : dataList){
            if(exp.getDescription() != null && exp.getDescription().toLowerCase().contains(queryText) && !results.contains(exp)){
                results.add(exp);
            }
        }
        updateListExps(results);
    }
}
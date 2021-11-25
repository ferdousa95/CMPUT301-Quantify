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

public class UserSearch extends AppCompatActivity {

    ListView resultView;
    ArrayAdapter<User> resultAdapter;
    ArrayList<User> dataList;
    ArrayList<User> resultList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Log.d("myTag", "This is test message");

        resultView = findViewById(R.id.test_search_view);

        dataList = new ArrayList<>();
        resultList = new ArrayList<>();

        ArrayList<User> subscribeList = new ArrayList<>();

        resultAdapter = new UserList(UserSearch.this, resultList);
        resultView.setAdapter(resultAdapter);


//        experimenterExperimentAdapter = new ExperimenterExperimentList(MainActivity.this, experimenterExperimentDataList, subscribedExperimentDataList);

        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("Users");

        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                dataList.clear();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    //Log.d("TAG", String.valueOf(doc.getData().get("Province Name")));
                    String user_id = doc.getId();
                    String user_contact = (String) doc.getData().get("User contact");

                    dataList.add(new User(user_id, user_contact)); // Adding the cities and provinces from FireStore
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
                Toast.makeText(UserSearch.this, "SEARCH " + query, Toast.LENGTH_LONG).show();
                searchExps(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Toast.makeText(UserSearch.this, "SEARCH " + newText, Toast.LENGTH_LONG).show();
                searchExps(newText);
                return false;
            }
        });

        searchView.setQueryHint("search something");

        return true;

    }

    private void updateListExps(ArrayList<User> listExps) {


        resultList.clear();
        resultList.addAll(listExps);

        resultAdapter.notifyDataSetChanged();
    }


    private void searchExps(String queryText) {
//        if (queryText.length() > 0)
//            queryText = queryText.substring(0, 1).toUpperCase() + queryText.substring(1).toLowerCase();

        ArrayList<User> results = new ArrayList<>();

        for(User ur : dataList){
            if(ur.getUserID() != null && ur.getUserID().toLowerCase().contains(queryText) && !results.contains(ur)){
                results.add(ur);
            }
        }
        updateListExps(results);
    }
}
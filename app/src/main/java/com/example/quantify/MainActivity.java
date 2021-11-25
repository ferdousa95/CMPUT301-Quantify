package com.example.quantify;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.Result;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    ListView experimentList;

    ArrayAdapter<Experiment> ownerExperimentAdapter;
    ArrayAdapter<Experiment> experimenterExperimentAdapter;

    ArrayList<Experiment> ownerExperimentDataList;
    ArrayList<Experiment> experimenterExperimentDataList;
    ArrayList<Experiment> subscribedExperimentDataList;

    MaterialButton delete_button;
    EditText expDesc;
    EditText expMinTrials;

    FloatingActionButton floatingActionButton;
    String id;

    String latitude;
    String longitude;

    LocationManager locationManager;
    LocationListener locationListener;




    private int tabPos = 0;

    GoogleMap googleMap;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        floatingActionButton = findViewById(R.id.floatingActionButton);
        experimentList = findViewById(R.id.exp_list);

        //        delete_button = findViewById(R.id.delete_button);
        ownerExperimentDataList = new ArrayList<>();
        experimenterExperimentDataList = new ArrayList<>();
        subscribedExperimentDataList = new ArrayList<>();

        ownerExperimentAdapter = new OwnerExperimentList(MainActivity.this, ownerExperimentDataList);
        experimenterExperimentAdapter = new ExperimenterExperimentList(MainActivity.this, experimenterExperimentDataList, subscribedExperimentDataList);

        // initially, we see the owner view
        experimentList.setAdapter(ownerExperimentAdapter);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationListener = new UserLocationListenerMain();
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 5000, 10, locationListener);

        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("Experiments");
        final CollectionReference collectionReference_1 = db.collection("Users");
        final DocumentReference documentReference = collectionReference_1.document(id);
        final CollectionReference collectionReferenceSubscribed = documentReference.collection("Subscribed");


        ArrayList<String> Subscribed_description_list = new ArrayList<String>();
        collectionReferenceSubscribed.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                Subscribed_description_list.clear();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    String exp_description = doc.getId();
                    Subscribed_description_list.add(exp_description);
                }
            }
        });


        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                experimenterExperimentDataList.clear();
                ownerExperimentDataList.clear();
                subscribedExperimentDataList.clear();
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
                        experimenterExperimentDataList.add(new Experiment(experiment_id, experiment_description, experiment_username, experiment_status, experiment_type, experiment_min_trials, experiment_location)); // Adding the cities and provinces from FireStore

                        if (Subscribed_description_list.contains(experiment_description)){
                            subscribedExperimentDataList.add(new Experiment(experiment_id, experiment_description, experiment_username, experiment_status, experiment_type, experiment_min_trials, experiment_location));
                        }

                        if (experiment_username.equals(id)) {
                            ownerExperimentDataList.add(new Experiment(experiment_id, experiment_description, experiment_username, experiment_status, experiment_type, experiment_min_trials, experiment_location));
                        }
                    }
                }
                experimenterExperimentAdapter.notifyDataSetChanged();
                ownerExperimentAdapter.notifyDataSetChanged();

            }
        });


//        FloatingActionButton fab;
//        fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);


        Toolbar topAppBar;
        topAppBar = (Toolbar) findViewById(R.id.topAppBar);
//        setSupportActionBar(topAppBar);
//
        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the navigation icon press

            }
        });
//
        topAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.search:
                        // Handle search icon press
//                        Toast.makeText(MainActivity.this, "Your Device: " + id, Toast.LENGTH_SHORT).show();
                        Intent search_intent = new Intent(MainActivity.this, SearchActivity.class);
                        search_intent.putExtra("USER", id);
//
                        startActivity(search_intent);
                        break;


                    case R.id.user:
                        // Handle user icon press

                        Intent intent = new Intent(MainActivity.this, ShowUserProfile.class);
                        intent.putExtra("USER", id);
//
                        startActivity(intent);
                        break;


                    /*case R.id.more:
                        // Handle more icon press
                        break;*/

                }
                return false;
            }
        });

        TabLayout tabLayout = findViewById(R.id.tabLayout);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //Toast.makeText(MainActivity.this, "You Clicked : " + tab.getPosition(), Toast.LENGTH_SHORT).show();

                if (tab.getPosition() == 0) {
                    floatingActionButton.setVisibility(View.VISIBLE);
                    tabPos = 0;
                    experimentList.setAdapter(ownerExperimentAdapter);
                    ownerExperimentAdapter.notifyDataSetChanged();
                } else {
                    floatingActionButton.setVisibility(View.INVISIBLE);
                    tabPos = 1;
                    experimentList.setAdapter(experimenterExperimentAdapter);
                    experimenterExperimentAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Handle tab reselect
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Handle tab unselect
            }

        });
//
        FloatingActionButton fab;
        fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);

        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                View view_1 = LayoutInflater.from(MainActivity.this).inflate(R.layout.add_experiment_fragment_layout, null);
                expDesc = view_1.findViewById(R.id.exp_desc_fragment);
                expMinTrials = view_1.findViewById(R.id.exp_min_trials);
                Spinner expLocation= view_1.findViewById(R.id.exp_location);

                ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(MainActivity.this,
                        android.R.layout.simple_spinner_item,
                        getResources().getStringArray(R.array.locationChoice));
                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                expLocation.setAdapter(adapter2);

                Spinner expTypes = view_1.findViewById(R.id.exp_type_fragment);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,
                        android.R.layout.simple_spinner_item,
                        getResources().getStringArray(R.array.expTypes));
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                expTypes.setAdapter(adapter);

                AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
                adb.setTitle("Add?");
                adb.setMessage("Are you sure you want to Add Experiment");
                adb.setView(view_1);

                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        UUID exp_id = UUID.randomUUID();
                        String exp_description = expDesc.getText().toString();
                        String exp_username = id;
                        String exp_status = "Running";
                        Integer exp_min_trials;
                        try{
                            exp_min_trials = Integer.valueOf(expMinTrials.getText().toString());
                        }catch(Exception e){
                            exp_min_trials = 0;
                        }
                        String exp_type = expTypes.getSelectedItem().toString();
                        String exp_location = expLocation.getSelectedItem().toString();


//  FIREBASE STUFF BEGINS
                        HashMap<String, String> data = new HashMap<>();
                        if (exp_description.length() > 0) {
                            data.put("Experiment ID", exp_id.toString());
                            data.put("Experiment User", exp_username);
                            data.put("Experiment Status", exp_status);
                            data.put("Experiment Type", exp_type);
                            data.put("Experiment Location", exp_location);
                            data.put("Min Trials", exp_min_trials.toString());
                        }
                        else{
                            Toast.makeText(MainActivity.this, "Unable to create experiment.\nDescription empty!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        collectionReference
                                .document(exp_description)
                                .set(data)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // These are a method which gets executed when the task is succeeded
                                        Log.d("TAG", "Data has been added successfully!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // These are a method which gets executed if there’s any problem
                                        Log.d("TAG", "Data could not be added!" + e.toString());
                                    }
                                });



//  FIREBASE STUFF ENDS

                        ownerExperimentDataList.add(new Experiment(exp_id, exp_description, exp_username, exp_status, exp_type, exp_min_trials, exp_location));
                        experimenterExperimentDataList.add(new Experiment(exp_id, exp_description, exp_username, exp_status, exp_type, exp_min_trials, exp_location));

//                                     current_exp.setExp_desc(exp_description);
//                                     current_exp.setUser(exp_username);
//                                     current_exp.setStatus(exp_status);
//
//
//
//
//                        Experiment mycity = new Experiment(exp_name, exp_description, null,S_Total,F_Total);
//                        experimentAdapter.remove(experimentAdapter.getItem(positionToRemove));
//                        experimentAdapter.insert(mycity, positionToRemove);
                        ownerExperimentAdapter.notifyDataSetChanged();

                    }
                });
                adb.show();
            }
        });//closing the setOnClickListener method


        experimentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                //Toast.makeText(MainActivity.this, "You Clicked : " + experimentList.isClickable(), Toast.LENGTH_SHORT).show();
                Log.d("BLABLA", "you clicked"+ experimentList.isClickable());
                String experiment_type = (String) ownerExperimentAdapter.getItem(position).getType();

                if (experiment_type.equals("Binomial Trials")) {
                    Log.d("BLABLA", "Binomial Clicked");
                    Intent intent_1 = new Intent(MainActivity.this, ResultsToIgnoreActivity.class);
                    intent_1.putExtra("Experiment", ownerExperimentAdapter.getItem(position));
                    startActivity(intent_1);
                } else if (experiment_type.equals("Count-based Tests")) {
                    Log.d("BLABLA", "Count Clicked");
                    Intent intent_1 = new Intent(MainActivity.this, ResultsToIgnoreActivity.class);
                    intent_1.putExtra("Experiment", ownerExperimentAdapter.getItem(position));
                    startActivity(intent_1);
                } else if (experiment_type.equals("Measurement Trials")) {
                    Log.d("BLABLA", "Temperature clicked");
                    Intent intent_1 = new Intent(MainActivity.this, ResultsToIgnoreActivity.class);
                    intent_1.putExtra("Experiment", ownerExperimentAdapter.getItem(position));
                    startActivity(intent_1);
                } else if (experiment_type.equals("Non-negative Integer Counts")) {
                    Log.d("BLABLA", "Non-neg clicked");
                    Intent intent_1 = new Intent(MainActivity.this, ResultsToIgnoreActivity.class);
                    intent_1.putExtra("Experiment", ownerExperimentAdapter.getItem(position));
                    startActivity(intent_1);
                }
            }
        });

        Toolbar bottomAppBar;
        bottomAppBar = findViewById(R.id.bottomAppBar);
        bottomAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the navigation icon press

            }
        });

        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.location:
                        // Handle location icon press
                        Intent intent_2 = new Intent(MainActivity.this, MapsActivity.class);
                        startActivity(intent_2);
                        break;

                    case R.id.subscribe_ic:
                        Log.d("BLABLA", subscribedExperimentDataList.toString());
                        Intent intent = new Intent(MainActivity.this, SubscribedActivity.class);
                        intent.putExtra("subscribed", subscribedExperimentDataList);
                        startActivity(intent);
                        break;


                    /*case R.id.question_answer:*/
                        // Handle question_answer icon press

                    case R.id.qr_code:
                        // Handle qr_code icon press
                        IntentIntegrator intentIntegrator = new IntentIntegrator(
                                MainActivity.this
                        );
                        //set prompt text
                        intentIntegrator.setPrompt("For flash use volume up key");
                        //set beep
                        intentIntegrator.setBeepEnabled(true);
                        //locked orientation
                        intentIntegrator.setOrientationLocked(true);
                        //set capture activity
                        intentIntegrator.setCaptureActivity(Capture.class);
                        //initiate scan
                        intentIntegrator.initiateScan();

                        break;
                }
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //initialize intent result
        IntentResult intentResult = IntentIntegrator.parseActivityResult(
                requestCode,resultCode,data
        );
        //check condition
        if (intentResult.getContents() != null) {
            //when result content is not null
            //initialize alert dialog
            if (intentResult.getContents().contains(";")){
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        MainActivity.this
                );
                String trialResultString;
                String experimentIDString;
                String experimentDesc;
                UUID experimentID;
                //set title
                builder.setTitle("Notification");
                //set message
                builder.setMessage("Your trial has been added.");
                experimentIDString = intentResult.getContents().split(";")[0];
                trialResultString = intentResult.getContents().split(";")[2];
                experimentDesc = intentResult.getContents().split(";")[1];
                experimentID = UUID.fromString(experimentIDString);


                FirebaseFirestore db;
                db = FirebaseFirestore.getInstance();
                //whereEqualTo() is from
                //https://stackoverflow.com/questions/53332471/checking-if-a-document-exists-in-a-firestore-collection/53332591#53332591
                db.collection("Experiments").whereEqualTo("Experiment ID", experimentID)
                        .limit(1).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    //Toast.makeText(getApplicationContext(),experimentDesc, Toast.LENGTH_SHORT).show();
                                    //Log.d("docsize", experimentDesc);
                                    //task.getResult().getDocuments().get(0).getId();

                                    final CollectionReference collectionReference_1 = db.collection("Experiments");
                                    final DocumentReference documentReference = collectionReference_1.document(experimentDesc);
                                    final CollectionReference collectionReference = documentReference.collection("Trials");

                                    Date date;
                                    SimpleDateFormat currentDate;
                                    String formattedCurrentDate;

                                    date = Calendar.getInstance().getTime();
                                    currentDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                                    formattedCurrentDate = currentDate.format(date);
                                    String experimenterID = id;

                                    HashMap<String, String> data = new HashMap<>();
                                    data.put("Experimenter ID", experimenterID);
                                    data.put("Location Latitude", latitude);
                                    data.put("Location Longitude", longitude);
                                    data.put("Trial Date", formattedCurrentDate);
                                    data.put("Trial-Result", trialResultString);

                                    UUID trialID = UUID.randomUUID();
                                    collectionReference
                                            .document(trialID.toString())
                                            .set(data)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // These are a method which gets executed when the task is succeeded
                                                    Log.d("TAG", "Data has been added successfully!");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // These are a method which gets executed if there’s any problem
                                                    Log.d("TAG", "Data could not be added!" + e.toString());
                                                }
                                            });
                                }
                            }
                        });


                //set positive button
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        //dismiss dialog
                        dialogInterface.dismiss();
                    }
                });
                //show alert dialog
                builder.show();
                return;

            }

            else {
                String barcodeInfo = intentResult.getContents();

                FirebaseFirestore db;
                db = FirebaseFirestore.getInstance();
                //final CollectionReference barcodeCollection = db.collection("Barcodes");
                DocumentReference docRef = db.collection("Barcodes").document(barcodeInfo);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
//                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                addBarcodeTrialResult(document);
                            } else {
//                                Log.d(TAG, "No such document");
                                initBarcode(barcodeInfo);
                            }
                        } else {
//                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
            }
     
        }else {
            //when result content is null
            //display toast
            Toast.makeText(getApplicationContext()
            ,"OOPS... You did not scan anything", Toast.LENGTH_SHORT)
                    .show();
        }
    }              
              

    private void addBarcodeTrialResult(DocumentSnapshot doc){
//        Toast.makeText(MainActivity.this, "bar code is: " + barcode, Toast.LENGTH_SHORT).show();
        AlertDialog.Builder builder = new AlertDialog.Builder(
                MainActivity.this
        );
        //set title
        builder.setTitle("Notification");
        //set message
        builder.setMessage("Your trial has been added.");

        String trialResultString;
        String experimentIDString;
        //final String[] expestring = new String[1];
        String experimentDesc;
        UUID experimentID;




        trialResultString = doc.getString("Result");
        experimentDesc = doc.getString("Associate Exp");

        Log.d("OnComplete", "aaa");

        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();


        experimentIDString = doc.getString("Exp ID");
        Log.d("Fail test", experimentIDString);
        experimentID = UUID.fromString(experimentIDString);

        //whereEqualTo() is from
        //https://stackoverflow.com/questions/53332471/checking-if-a-document-exists-in-a-firestore-collection/53332591#53332591
        db.collection("Experiments").whereEqualTo("Experiment ID", experimentID)
                .limit(1).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //Toast.makeText(getApplicationContext(),experimentDesc, Toast.LENGTH_SHORT).show();
                            //Log.d("docsize", experimentDesc);
                            //task.getResult().getDocuments().get(0).getId();

                            final CollectionReference collectionReference_1 = db.collection("Experiments");
                            final DocumentReference documentReference = collectionReference_1.document(experimentDesc);
                            final CollectionReference collectionReference = documentReference.collection("Trials");

                            Date date;
                            SimpleDateFormat currentDate;
                            String formattedCurrentDate;

                            date = Calendar.getInstance().getTime();
                            currentDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                            formattedCurrentDate = currentDate.format(date);
                            String experimenterID = id;

                            HashMap<String, String> data = new HashMap<>();
                            data.put("Experimenter ID", experimenterID);
                            data.put("Location Latitude", latitude);
                            data.put("Location Longitude", longitude);
                            data.put("Trial Date", formattedCurrentDate);
                            data.put("Trial-Result", trialResultString);

                            UUID trialID = UUID.randomUUID();
                            collectionReference
                                    .document(trialID.toString())
                                    .set(data)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // These are a method which gets executed when the task is succeeded
                                            Log.d("TAG", "Data has been added successfully!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // These are a method which gets executed if there’s any problem
                                            Log.d("TAG", "Data could not be added!" + e.toString());
                                        }
                                    });
                        }
                    }
                });


        //set positive button
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                //dismiss dialog
                dialogInterface.dismiss();
            }
        });
        //show alert dialog
        builder.show();
    }

    private void initBarcode(String barcode){
        Toast.makeText(MainActivity.this, "bar code is: " + barcode, Toast.LENGTH_SHORT).show();
        View view_1 = LayoutInflater.from(MainActivity.this).inflate(R.layout.barcode_associate_exp_dialog, null);
        expDesc = view_1.findViewById(R.id.exp_desc_fragment);
        expMinTrials = view_1.findViewById(R.id.exp_min_trials);

        Spinner expTypes = view_1.findViewById(R.id.exp_type_fragment);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.booleanTypes));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        expTypes.setAdapter(adapter);

        AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
        adb.setTitle("New Barcode detecte, attach to experiment");
//        adb.setMessage("Are you sure ");
        adb.setView(view_1);

        adb.setNegativeButton("Cancel", null);
        adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                String exp_description = expDesc.getText().toString();
                String bool_result = expTypes.getSelectedItem().toString();
                String num_result = expMinTrials.getText().toString();
                final String[] result = new String[1];

                FirebaseFirestore dbdbdb;
                dbdbdb = FirebaseFirestore.getInstance();
                final CollectionReference coll = dbdbdb.collection("Experiments");
                final DocumentReference docdoc = coll.document(exp_description);

                docdoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d("EXTAG", "Document exists!");
                                String exp_type = document.getString("Experiment Type");
//                                Log.d("EXTAG", ()exp_type);
                                Log.d("EXTAG", "exp_type");

                                if (exp_type.equals("Binomial Trials")){
                                    Log.d("EXTAG", "I am here");
                                    if (bool_result.equals("Not Binomial")){
                                        Toast.makeText(MainActivity.this, "Invalid Result!", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    result[0] = bool_result;
                                }

                                if (exp_type.equals("Non-negative Integer Counts") ){
                                    if (num_result.contains(".") || Integer.parseInt(num_result) < 0 || num_result.isEmpty()) {
                                        Toast.makeText(MainActivity.this, "Invalid Result!", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    String result = num_result;
                                }

                                if (exp_type.equals("Count-based Tests")){

                                    if (num_result.contains(".") || Integer.parseInt(num_result) < 1 || num_result.isEmpty()) {
                                        Toast.makeText(MainActivity.this, "Invalid Result!", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    result[0] = num_result;
                                }

                                if (exp_type.equals("Measurement Trials") ){

                                    if (num_result.isEmpty()) {
                                        Toast.makeText(MainActivity.this, "Invalid Result!", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    result[0] =  num_result;
                                }

                                FirebaseFirestore dbc;
                                dbc = FirebaseFirestore.getInstance();

                                final CollectionReference coll_bar = dbc.collection("Barcodes");

                                HashMap<String, String> data = new HashMap<>();
                                if (exp_description.length() > 0) {
                                    data.put("Associate Exp", exp_description);
                                    data.put("Result", result[0]);
                                    data.put("Type", exp_type);
                                    data.put("Exp ID", document.getString("Experiment ID"));
                                }
                                else{
                                    Toast.makeText(MainActivity.this, "Unable to create experiment.\nDescription empty!", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                coll_bar
                                        .document(barcode)
                                        .set(data)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // These are a method which gets executed when the task is succeeded
                                                Log.d("EXTAG", "Data has been added successfully!");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // These are a method which gets executed if there’s any problem
                                                Log.d("TAG", "Data could not be added!" + e.toString());
                                            }
                                        });

                            } else {
                                Log.d("TAG", "Document does not exist!");
                                Toast.makeText(MainActivity.this, "No such experiment exist!", Toast.LENGTH_SHORT).show();
                            }
                            return;
                        } else {
                            Log.d("TAG", "Failed with: ", task.getException());
                        }
                    }
                });
            }
        });
        adb.show();
        return;
    }
  
    class UserLocationListenerMain implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {

            longitude = loc.getLongitude()+"";
            Log.v("longitude", longitude);
            latitude = loc.getLatitude() + "";
            Log.d("latitude", latitude);

            String final_location = longitude + ", " + latitude;


        }

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

    }  
  
  
  
  
  
  
}
              
              
              
              
              

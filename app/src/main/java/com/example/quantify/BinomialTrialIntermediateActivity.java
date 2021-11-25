package com.example.quantify;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.UUID;

public class BinomialTrialIntermediateActivity extends AppCompatActivity {

    Experiment exp;

    TextView expDesc;
    TextView userID;
    TextView minTrials;
    TextView locationText;
    TextView locationView;
    TextView SuccessCount;
    TextView FailureCount;
    Button locationButton;
    Button start;

    String longitude;
    String latitude;

    LocationManager locationManager;
    LocationListener locationListener;

    ArrayList<String> longitudeList;
    ArrayList<String> latitudeList;
    ArrayList<String> experimentIDList;

    int SUCCESS;
    int FAILURE;

    private ArrayList<String> result_date_list;
    private ArrayList<Integer> result_count_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.binomial_intermediate);

        Intent intent = getIntent();
        exp = (Experiment) getIntent().getSerializableExtra("Experiment");

        expDesc = findViewById(R.id.experimentDescriptionViewBino);
        userID = findViewById(R.id.userIDViewBino);
        minTrials = findViewById(R.id.minTrialViewBino);
        locationText = findViewById(R.id.locationTextBino);
        locationView = findViewById(R.id.locationViewBino);
        SuccessCount = findViewById(R.id.successViewBino);
        FailureCount = findViewById(R.id.failViewBino);
        locationButton = findViewById(R.id.LocationButtonBino);
        start = findViewById(R.id.startButtonBino);


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationListener = new UserLocationListenerBinomial();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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


        expDesc.setText(exp.getDescription());
        userID.setText(exp.getExperimentID().toString());
        minTrials.setText(exp.getMinTrials().toString());

        // trial array
        longitudeList = new ArrayList<>();
        latitudeList = new ArrayList<>();
        experimentIDList = new ArrayList<>();

        if(exp.getLocation().equals("No")){
            locationText.setVisibility(View.INVISIBLE);
            locationView.setVisibility(View.INVISIBLE);
            locationButton.setVisibility(View.INVISIBLE);
        }
        else{
            // later change this value to be the user's location
            locationView.setText("Fetching location: Latitude, longitude.\nPlease wait.");
        }

        if(exp.getStatus().equals("End")){
            start.setVisibility(View.INVISIBLE);
        }

        result_date_list = new ArrayList<>();
        result_count_list = new ArrayList<>();


        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        final CollectionReference collectionReference_1 = db.collection("Experiments");
        final DocumentReference documentReference = collectionReference_1.document(exp.getDescription());
        final CollectionReference collectionReference = documentReference.collection("Trials");



        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                SUCCESS = 0;
                FAILURE = 0;
                result_count_list.clear();
                result_date_list.clear();
                latitudeList.clear();
                longitudeList.clear();
                experimentIDList.clear();

                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    if (doc.getData().get("Trial-Result") != null) {
                        String Trial_id = doc.getId();
                        String Trial_result = (String) doc.getData().get("Trial-Result");
                        Log.d("TAG", Trial_result);

                        if (Trial_result.equals("Success")) {
                            SUCCESS++;
                            Log.d("TAG", "BOOM");
                        }

                        if (Trial_result.equals("Fail")) {
                            FAILURE++;
                            Log.d("TAG", "BOOM2");
                        }

                        if(doc.getData().get("Trial Date") != null) {
                            String Result_date = (String) doc.getData().get("Trial Date");

                            if (result_date_list.contains(Result_date)) {
                                int index = result_date_list.indexOf(Result_date);
                                result_count_list.set(index, result_count_list.get(index) + 1);
                            } else {
                                result_date_list.add(Result_date);
                                result_count_list.add(1);
                            }
                        }
                        if(doc.getData().get("Location Latitude") != null && doc.getData().get("Location Longitude") != null){
                            if(!longitudeList.contains(doc.getData().get("Location Longitude")) && !latitudeList.contains(doc.getData().get("Location Latitude"))) {
                                longitudeList.add(doc.getData().get("Location Longitude").toString());
                                latitudeList.add(doc.getData().get("Location Latitude").toString());
                                experimentIDList.add(doc.getData().get("Experimenter ID").toString());
                            }
                        }
                    }
                }
                SuccessCount.setText(String.valueOf(SUCCESS));
                FailureCount.setText(String.valueOf(FAILURE));
                Log.d("longitudeList", longitudeList.toString());
                Log.d("latitudeList", latitudeList.toString());
            }
        });



    }

    public void startBinomialTrial(View target){

        Log.d("BLABLA", "Binomial Clicked");
        Intent intent_1 = new Intent(this, BinomialTrialActivity.class);
        intent_1.putExtra("Experiment", exp);
        intent_1.putExtra("Longitude",longitude);
        intent_1.putExtra("Latitude",latitude);
        this.startActivity(intent_1);
    }

    public void createHistogramBino(View target){
        Log.d("BLABLA", "Binomial Clicked");
        Intent intent_1 = new Intent(this, BinomialHistogramActivity.class);
        intent_1.putExtra("success", SUCCESS);
        intent_1.putExtra("fail", FAILURE);
        this.startActivity(intent_1);
    }

    public void questionForumLaunch(View view)  {

        TextView expName = (TextView)findViewById(R.id.experimentDescriptionViewBino);
        String expNameString = expName.getText().toString();

        Intent intent = new Intent(this, QuestionForumList.class);
        intent.putExtra("EXPNAME", expNameString);
        intent.putExtra("SENT_FROM_TRIAL", 5);
        startActivity(intent);

        Log.d("STARTQA", "questionForumLaunch: The QuestionForum is launched!");
    }

    public void onLocationClickedBinomial(View target){
        Log.d("location", "Location Clicked");

        if(longitudeList.size() == 0 || latitudeList.size() == 0 || experimentIDList.size() == 0){
            Toast.makeText(BinomialTrialIntermediateActivity.this, "No location has been shared yet.", Toast.LENGTH_SHORT).show();
        }
        else {
            Intent intent_1 = new Intent(this, MapsActivity.class);
            intent_1.putExtra("longitude", longitudeList);
            intent_1.putExtra("latitude", latitudeList);
            intent_1.putExtra("Experimenter ID", experimentIDList);
            this.startActivity(intent_1);
        }
    }

    public void resultsOverTimeBinomialClicked(View target){
        Log.d("BLABLA", "Results Clicked");
        Intent intent_1 = new Intent(this, ResultsOverTimeActivity.class);
        intent_1.putExtra("y-axis", result_count_list);
        intent_1.putExtra("x-axis", result_date_list);
        this.startActivity(intent_1);
    }


    // Code taken from: https://stackoverflow.com/questions/1513485/how-do-i-get-the-current-gps-location-programmatically-in-android
    // Code owner: Swiftboy (https://stackoverflow.com/users/1371853/swiftboy)

    class UserLocationListenerBinomial implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {

            longitude = loc.getLongitude()+"";
            Log.v("longitude", longitude);
            latitude = loc.getLatitude() + "";
            Log.d("latitude", latitude);

            String final_location = longitude + ", " + latitude;

            locationView.setText(final_location);

        }

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

    }
}


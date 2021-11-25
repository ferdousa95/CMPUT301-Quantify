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

import java.util.ArrayList;
import java.util.Collections;

public class TrialIntermediateActivity extends AppCompatActivity {

    Experiment exp;

    TextView expDesc;
    TextView userID;
    TextView minTrials;
    TextView locationText;
    TextView locationView;

    String longitude;
    String latitude;

    LocationManager locationManager;
    LocationListener locationListener;

    ArrayList<String> longitudeList;
    ArrayList<String> latitudeList;
    ArrayList<String> experimentIDList;

    TextView mean;
    TextView median;
    TextView Q1;
    TextView Q3;
    TextView sd;

    Button start;

    ArrayList<String> Trial_list;
    ArrayList<Integer> Count_list;
    ArrayList<Double> Complete_Trial_list;

    ArrayList<String> result_date_list;
    ArrayList<Integer> result_count_list;
    int length;
    double sum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trial_intermediate);

        Intent intent = getIntent();
        exp = (Experiment) getIntent().getSerializableExtra("Experiment");

        expDesc = findViewById(R.id.experimentDescriptionView);
        userID = findViewById(R.id.userIDView);
        minTrials = findViewById(R.id.MinTrialView);
        locationText = findViewById(R.id.locationText);
        locationView = findViewById(R.id.locationView);

        mean = findViewById(R.id.mean_value);
        median = findViewById(R.id.medianValue);
        Q1 = findViewById(R.id.quartile1View);
        Q3 = findViewById(R.id.quartile3View);
        sd = findViewById(R.id.StdDevValue);

        start = findViewById(R.id.startButton);

        expDesc.setText(exp.getDescription());
        userID.setText(exp.getExperimentID().toString());
        minTrials.setText(exp.getMinTrials().toString());

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationListener = new UserLocationListenerOther();
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

        longitudeList = new ArrayList<>();
        latitudeList = new ArrayList<>();
        experimentIDList = new ArrayList<>();

        if(exp.getLocation().equals("No")){
            locationText.setVisibility(View.INVISIBLE);
            locationView.setVisibility(View.INVISIBLE);
        }
        else{
            // later change this value to be the user's location
            locationView.setText("Fetching location: Latitude, longitude.\nPlease wait.");
        }

        if(exp.getStatus().equals("End")){
            start.setVisibility(View.INVISIBLE);
        }

        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        final CollectionReference collectionReference_1 = db.collection("Experiments");
        final DocumentReference documentReference = collectionReference_1.document(exp.getDescription());
        final CollectionReference collectionReference = documentReference.collection("Trials");

        // create an array of numbers and its counters
        // if the number is unique, add it to array and set count to 1
        // if the number is not unique, increment count
        Trial_list = new ArrayList<String>();
        Count_list = new ArrayList<Integer>();
        Complete_Trial_list = new ArrayList<Double>();

        result_date_list = new ArrayList<>();
        result_count_list = new ArrayList<>();



        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                length = 0;
                sum = 0;
                Complete_Trial_list.clear();
                Count_list.clear();
                Trial_list.clear();
                result_count_list.clear();
                result_date_list.clear();
                latitudeList.clear();
                longitudeList.clear();
                experimentIDList.clear();

                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    if (doc.getData().get("Trial-Result") != null) {
                        String Trial_id = doc.getId();
                        String Trial_result = (String) doc.getData().get("Trial-Result");
                        Complete_Trial_list.add(Double.parseDouble(Trial_result));
//                        Log.d("TAG", Result_date);

                        if (Trial_list.contains(Trial_result)){
                            int index = Trial_list.indexOf(Trial_result);
                            Count_list.set(index, Count_list.get(index) + 1);
                            sum = sum + Double.parseDouble(Trial_result);
                            length = length + 1;
                        }
                        else {
                            Trial_list.add(Trial_result);
                            Count_list.add(1);
                            sum = sum + Double.parseDouble(Trial_result);
                            length = length + 1;
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
                        // pass the result and its count to next activity smhw

                        if(doc.getData().get("Location Latitude") != null && doc.getData().get("Location Longitude") != null){
                            if(!longitudeList.contains(doc.getData().get("Location Longitude")) && !latitudeList.contains(doc.getData().get("Location Latitude"))) {
                                longitudeList.add(doc.getData().get("Location Longitude").toString());
                                latitudeList.add(doc.getData().get("Location Latitude").toString());
                                experimentIDList.add(doc.getData().get("Experimenter ID").toString());
                            }
                        }
                    }
                }
                Log.d("TAG", (String) result_count_list.toString());
                Log.d("longitudeList", longitudeList.toString());
                Log.d("latitudeList", latitudeList.toString());
                Log.d("LENGTH", String.valueOf(length));

                if (Complete_Trial_list.size() > 0){
                    double mean_v = calculateMean(sum, length);
                    mean.setText(String.valueOf(mean_v));
                    Collections.sort(Complete_Trial_list);
                    double median_v = calculateMedian(Complete_Trial_list);
                    double Q1_v = calculateQ1(Complete_Trial_list);
                    double Q3_v = calculateQ3(Complete_Trial_list);
                    double sd_v = calculateSD(Complete_Trial_list, mean_v);

                    Q1.setText(String.valueOf(Q1_v));
                    median.setText(String.valueOf(median_v));
                    Q3.setText(String.valueOf(Q3_v));
                    sd.setText(String.valueOf(sd_v));
                    Log.d("Sorted", Complete_Trial_list.toString());
                }



            }

        });

    }

    public double calculateMedian(ArrayList<Double> CompleteList){
        int position = CompleteList.size()/2;
        Log.d("Q2 position", String.valueOf(position));
        return CompleteList.get(position);
    }

    public double calculateQ1(ArrayList<Double> CompleteList){
        int position = CompleteList.size()/4;
        Log.d("Q1 position", String.valueOf(position));
        return CompleteList.get(position);
    }

    public double calculateQ3(ArrayList<Double> CompleteList){
        Log.d("LENGTH OF LIST", String.valueOf(CompleteList.size()));
        int position = (CompleteList.size()*3)/4;
        Log.d("Q3 position", String.valueOf(position));
        return CompleteList.get(position);
    }

    public double calculateSD(ArrayList<Double> CompleteList, double mean){
        double sum_square = 0;
        double sd;
        for (int counter=0; counter<CompleteList.size(); counter++) {
            sum += (CompleteList.get(counter) - mean)*(CompleteList.get(counter) - mean);
        }
        sd = Math.sqrt(sum/(CompleteList.size()-1));
        return sd;
    }


    public double calculateMean(double sum, int length) {
        return sum/length;
    }

    public void startTrial(View target){
        String experiment_type = exp.getType();

        if (experiment_type.equals("Count-based Tests")) {
            Log.d("BLABLA", "Count Clicked");
            Intent intent_1 = new Intent(this, CountTrialActivity.class);
            intent_1.putExtra("Experiment", exp);
            intent_1.putExtra("Longitude",longitude);
            intent_1.putExtra("Latitude",latitude);
            this.startActivity(intent_1);
        } else if (experiment_type.equals("Measurement Trials")) {
            Log.d("BLABLA", "Measurement clicked");
            Intent intent_1 = new Intent(this, MeasurementTrialActivity.class);
            intent_1.putExtra("Experiment", exp);
            intent_1.putExtra("Longitude",longitude);
            intent_1.putExtra("Latitude",latitude);
            this.startActivity(intent_1);
        } else if (experiment_type.equals("Non-negative Integer Counts")) {
            Log.d("BLABLA", "Non-neg clicked");
            Intent intent_1 = new Intent(this, NonNegativeCountTrialActivity.class);
            intent_1.putExtra("Experiment", exp);
            intent_1.putExtra("Longitude",longitude);
            intent_1.putExtra("Latitude",latitude);
            this.startActivity(intent_1);
        }
    }

    public void questionForumLaunchTrial(View view)  {

        TextView expName = (TextView)findViewById(R.id.experimentDescriptionView);
        String expNameString = expName.getText().toString();

        Intent intent = new Intent(this, QuestionForumList.class);
        intent.putExtra("EXPNAME", expNameString);
        intent.putExtra("SENT_FROM_TRIAL", 5);
        startActivity(intent);

        Log.d("STARTQA", "questionForumLaunch: The QuestionForum is launched!");
    }

    public void createHistogram(View target){
        Log.d("count list", (String) Count_list.toString());

        Intent intent_1 = new Intent(this, OtherHistogramActivity.class);
        intent_1.putExtra("y-axis", Count_list);
        intent_1.putExtra("x-axis", Trial_list);
        this.startActivity(intent_1);
    }

    public void onLocationClickedOther(View target){
        Log.d("location", "Location Clicked");

        if(longitudeList.size() == 0 || latitudeList.size() == 0 || experimentIDList.size() == 0){
            Toast.makeText(TrialIntermediateActivity.this, "No location has been shared yet.", Toast.LENGTH_SHORT).show();
        }
        else {
            Intent intent_1 = new Intent(this, MapsActivity.class);
            intent_1.putExtra("longitude", longitudeList);
            intent_1.putExtra("latitude", latitudeList);
            intent_1.putExtra("Experimenter ID", experimentIDList);
            this.startActivity(intent_1);
        }
    }


    public void resultsOverTimeOtherClicked(View target){
        Log.d("BLABLA", "Results Clicked");
        Intent intent_1 = new Intent(this, ResultsOverTimeActivity.class);
        intent_1.putExtra("y-axis", result_count_list);
        intent_1.putExtra("x-axis", result_date_list);
        this.startActivity(intent_1);
    }
    // Code taken from: https://stackoverflow.com/questions/1513485/how-do-i-get-the-current-gps-location-programmatically-in-android
    // Code owner: Swiftboy (https://stackoverflow.com/users/1371853/swiftboy)

    class UserLocationListenerOther implements LocationListener {
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
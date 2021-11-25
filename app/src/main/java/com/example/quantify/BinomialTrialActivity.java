package com.example.quantify;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGSaver;

public class BinomialTrialActivity extends AppCompatActivity {

    public static final String QRPath = "null";
    Experiment exp;

    TextView expDesc;
    TextView userID;
    TextView minTrials;
    TextView result;

    String longitude;
    String latitude;

    Date date;
    SimpleDateFormat currentDate;
    String formattedCurrentDate;

    Button save;
    Button generateQR;
    ImageView binomialQRImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.binomial_trial);

        Intent intent = getIntent();
        exp = (Experiment) getIntent().getSerializableExtra("Experiment");
        longitude = getIntent().getStringExtra("Longitude");
        latitude = getIntent().getStringExtra("Latitude");

        expDesc = findViewById(R.id.bTrialDescriptionView);
        userID = findViewById(R.id.bTrialUserIDView);
        minTrials = findViewById(R.id.bMinTrialView);
        result = findViewById(R.id.bResultValue);
        generateQR = findViewById(R.id.bTrialGenerateQRCodeButton);


        date = Calendar.getInstance().getTime();
        currentDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        formattedCurrentDate = currentDate.format(date);

        expDesc.setText(exp.getDescription());
        userID.setText(exp.getExperimentID().toString());
        minTrials.setText(exp.getMinTrials().toString());

        //generate the QR code with save feature not working
        String savePath = Environment.getExternalStorageDirectory().getPath() + "/QRCode/";
        String TAG = "GenerateQRCode";
        UUID thisExperimentID = exp.getExperimentID();
        binomialQRImage = findViewById(R.id.binomialQRImage);
        generateQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (result.getText().toString().equals("_____")) {
                    Toast.makeText(getApplicationContext(), "The Trial result is empty, please enter result.", Toast.LENGTH_LONG).show();
                    return;
                }

                String inputValue = thisExperimentID.toString() + ";" + exp.getDescription() + ";" + result.getText().toString();
                try{
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.encodeBitmap(inputValue, BarcodeFormat.QR_CODE,400,400);
                    binomialQRImage.setImageBitmap(bitmap);

                    Log.d("expdesc", exp.getDescription());

                    FirebaseFirestore db;
                    db = FirebaseFirestore.getInstance();
                    final CollectionReference collectionReference = db.collection("Barcodes");

                    HashMap<String, String> data = new HashMap<>();
                    data.put("Associate Exp", thisExperimentID.toString());
                    data.put("Experiment desc", exp.getDescription());
                    data.put("Result", result.getText().toString());
                    data.put("Type", "Binomial Trials");

                    collectionReference
                            .document(inputValue)
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

                    boolean save;
                    String result;
                    try {
                        save = QRGSaver.save(savePath, "Test1", bitmap, QRGContents.ImageType.IMAGE_PNG);
                        String realPath = savePath.toString() + "Test1";
                        result = save ? "Image Saved" : "Image Not Saved";
                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        });
    }



    public void successClicked(View target){
        result.setText("Success");
    }

    public void failClicked(View target){
        result.setText("Fail");
    }


    public void binomialSaveClicked(View target){
        // we give the trial an ID using UUID and save the result in the database

        if(result.getText().toString().equals("Fail") || result.getText().toString().equals("Success")) {
            FirebaseFirestore db;
            db = FirebaseFirestore.getInstance();
            String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            final CollectionReference collectionReference_1 = db.collection("Experiments");
            final DocumentReference documentReference = collectionReference_1.document(exp.getDescription());
            final CollectionReference collectionReference = documentReference.collection("Trials");

            HashMap<String, String> data = new HashMap<>();
            data.put("Trial-Result", result.getText().toString());
            data.put("Experimenter ID", Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
            data.put("Trial Date", formattedCurrentDate);
            data.put("Location Latitude", latitude);
            data.put("Location Longitude", longitude);
            UUID Trial_id = UUID.randomUUID();

            collectionReference
                    .document(Trial_id.toString())
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


            Log.d("count", "Count: " + result.getText().toString());
        }

        finish();
    }
}
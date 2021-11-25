package com.example.quantify;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ExperimenterExperimentList extends ArrayAdapter<Experiment> {

    private ArrayList<Experiment> experiments;
    private ArrayList<Experiment> subscribed;
    private Context context;
    String id = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);

    public ExperimenterExperimentList(Context context, ArrayList<Experiment> experiments, ArrayList<Experiment> subscribed){
        super(context, 0, experiments);
        this.experiments = experiments;
        this.subscribed = subscribed;
        this.context = context;
    }
    @NonNull
    @Override

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){

        View view = convertView;

        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.experimenter_card, parent, false);
        }

        Experiment experiment = experiments.get(position);

        LinearLayout card = view.findViewById(R.id.exp_card);

        TextView expDesc = view.findViewById(R.id.experimenter_exp_desc);
        TextView expUser = view.findViewById(R.id.experimenter_exp_user);
        TextView expStatus = view.findViewById(R.id.experimenter_exp_status);

        expDesc.setText(experiment.getDescription());
        expUser.setText(experiment.getUser());
        expStatus.setText(experiment.getStatus());


        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference_1 = db.collection("Users");
        final DocumentReference documentReference = collectionReference_1.document(id);
        final CollectionReference collectionReference = documentReference.collection("Subscribed");
        final CollectionReference collectionReferenceExperiments = documentReference.collection("Experiments");



        card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if(!(context instanceof SubscribedActivity)){
                    Toast.makeText(context ,"Subscribed", Toast.LENGTH_SHORT).show();
                    HashMap<String, String> data = new HashMap<>();

                    data.put("Subscribed", "Subscribed");

                    collectionReference
                            .document(experiment.getDescription())
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

                if(!subscribed.contains(experiments.get(position))) {
                    subscribed.add(experiments.get(position));
                    Log.d("subscribed","Subscribed");
                }
                notifyDataSetChanged();
                return false;
            }
        });

        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String experiment_type = (String) getItem(position).getType();

//                Toast.makeText(context ,(String) getItem(position).getLocation(), Toast.LENGTH_SHORT).show();
//                Toast.makeText(context ,(String) getItem(position).getDescription(), Toast.LENGTH_SHORT).show();
                if ( ((String) getItem(position).getLocation()).equals("Yes") ) {
                    //Creating the instance of PopupMenu
                    View view_1 = LayoutInflater.from(context).inflate(R.layout.location_warning, null);


                    AlertDialog.Builder adb = new AlertDialog.Builder(context);
                    adb.setTitle("WARNING!!");
                    adb.setMessage("Proceed with caution!");
                    adb.setView(view_1);
                    adb.setNegativeButton("Cancel", null);
                    adb.setPositiveButton("Proceed", new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if(experiment_type.equals("Binomial Trials")){
                                Intent intent_1 = new Intent(context, BinomialTrialIntermediateActivity.class);
                                intent_1.putExtra("Experiment", getItem(position));
                                context.startActivity(intent_1);
                            }
                            else {
                                Intent intent_1 = new Intent(context, TrialIntermediateActivity.class);
                                intent_1.putExtra("Experiment", getItem(position));
                                context.startActivity(intent_1);
                            }
                        }
                    });
                    adb.show();
                }
                else{
                        if(experiment_type.equals("Binomial Trials")){
                            Intent intent_1 = new Intent(context, BinomialTrialIntermediateActivity.class);
                            intent_1.putExtra("Experiment", getItem(position));
                            context.startActivity(intent_1);
                        }
                        else {
                            Intent intent_1 = new Intent(context, TrialIntermediateActivity.class);
                            intent_1.putExtra("Experiment", getItem(position));
                            context.startActivity(intent_1);
                        }
//                    }
                }
            }

        });

        return view;
    }
}

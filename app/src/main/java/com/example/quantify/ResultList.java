package com.example.quantify;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class ResultList extends ArrayAdapter<Trial> {
    private ArrayList<Trial> trials;
    ArrayList<Trial> ignoredTrials;
    private Context context;

    ArrayList<String> Trial_list;
    ArrayList<Integer> Count_list;

    ArrayList<String> result_date_list;
    ArrayList<Integer> result_count_list;

    ArrayList<Integer> booleanResults; // zero index is fail. one index is success

    public ResultList(Context context, ArrayList<Trial> trials, ArrayList<Trial> ignoredTrials, ArrayList<String> Trial_list, ArrayList<Integer> Count_list, ArrayList<String> result_date_list, ArrayList<Integer> result_count_list, ArrayList<Integer> booleanResults){
        super(context, 0, trials);
        this.trials = trials;
        this.ignoredTrials = ignoredTrials;
        this.Trial_list = Trial_list;
        this.Count_list = Count_list;
        this.result_date_list = result_date_list;
        this.result_count_list = result_count_list;
        this.booleanResults = booleanResults;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){

        View view = convertView;

        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.result_card, parent, false);
        }

        LinearLayout card = view.findViewById(R.id.result_card);

        Trial trial = trials.get(position);

        TextView result = view.findViewById(R.id.result);
        TextView userID = view.findViewById(R.id.userID);

        result.setText("Result: " + trial.getResult());
        userID.setText("Experimenter ID: " + trial.getExperimenterID());

        card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if(!ignoredTrials.contains(trials.get(position))) {
                    Toast.makeText(context ,"Ignoring this result", Toast.LENGTH_SHORT).show();
//                    ignoredTrials.add(trials.get(position));
                    trials.remove(position);
                    Log.d("ignored","Ignored trial");

                    Trial_list.clear();
                    Count_list.clear();
                    result_date_list.clear();
                    result_count_list.clear();
                    booleanResults.set(0,0); // zero index is fail
                    booleanResults.set(1,0); // one index is success

                    for(int counter = 0; counter < trials.size(); counter++){
                        String Trial_result = trials.get(counter).getResult();
                        String Result_date = trials.get(counter).getDate();

                        if (Trial_list.contains(Trial_result)){
                            int index = Trial_list.indexOf(Trial_result);
                            Count_list.set(index, Count_list.get(index) + 1);
                        }
                        else {
                            Trial_list.add(Trial_result);
                            Count_list.add(1);
                        }

                        if (result_date_list.contains(Result_date)) {
                            int index = result_date_list.indexOf(Result_date);
                            result_count_list.set(index, result_count_list.get(index) + 1);
                        } else {
                            result_date_list.add(Result_date);
                            result_count_list.add(1);
                        }

                        if (Trial_result.equals("Success")) {
                            booleanResults.set(1, booleanResults.get(1)+1);
                            Log.d("TAG", "BOOM");
                        }

                        else if (Trial_result.equals("Fail")) {
                            booleanResults.set(0, booleanResults.get(0)+1);
                            Log.d("TAG", "BOOM2");
                        }
                    }

                Log.d("success",String.valueOf(booleanResults.get(1)));
                Log.d("failure",String.valueOf(booleanResults.get(0)));
                }
                notifyDataSetChanged();
                return false;
            }
        });


        return view;
    }
}

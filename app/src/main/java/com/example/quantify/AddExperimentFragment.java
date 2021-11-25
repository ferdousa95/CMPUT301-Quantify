package com.example.quantify;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class AddExperimentFragment extends DialogFragment {
    private EditText expDesc;
    private EditText expUser;
    private EditText expStatus;
    private EditText expType;


    private OnFragmentInteractionListener listener;

    public interface OnFragmentInteractionListener {
        void onOkPressed(Experiment newExperiment);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + "must implement onFragmentInteractionListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.add_experiment_fragment_layout, null);
        expDesc = view.findViewById(R.id.exp_desc_fragment);
        //expUser = view.findViewById(R.id.exp_user_fragment);
        //expStatus = view.findViewById(R.id.exp_status_fragment);
        expType = view.findViewById(R.id.exp_type_fragment);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Add Experiment")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        String experiment = expDesc.getText().toString();
//                        String user = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
//                        //String status = expStatus.getText().toString();
//                        String type = expType.getText().toString();
//
////                        https://stackoverflow.com/a/15314227
////                        username: Ahmed Aeon Axan
////                        license: CC BY-SA 3.0
//
//
////                        Reference ends. I just learned parseInt from this guy
//
//                        listener.onOkPressed(new Experiment(experiment, user, status, type));
                    }
                }).create();
    }
}
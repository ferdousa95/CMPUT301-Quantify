package com.example.quantify;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class ShowUserSearchResult extends AppCompatActivity {

    String nameText;
    int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user_search_result);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        nameText = intent.getStringExtra("USER");
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("Users");

        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
//                    Log.d("TAG", String.valueOf(doc.getData().get("Province Name")));
                    String user_id = doc.getId();

                    if (user_id.equals(nameText)) {
                        String user_contact = (String) doc.getData().get("User contact");
                        //String user_contact = (String) doc.getData().get("User contact");

                        TextView nameTextView = findViewById(R.id.tv_name);
                        nameTextView.setText(user_id);

                        TextView content_View = findViewById(R.id.contact);
                        content_View.setText(user_contact);
                        flag = 1;
                        break;
                    }
                }

            }
        });


        if (flag == 1) {
            HashMap<String, String> data = new HashMap<>();
            if (nameText.length() > 0) {
                data.put("User email", "");
                data.put("User contact", "");
            } else {
//                Toast.makeText(ShowUserProfile.this, "Unable to create USER.\nUSER empty!", Toast.LENGTH_SHORT).show();
                return;
            }

            collectionReference
                    .document(nameText)
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


        TextView nameTextView = findViewById(R.id.tv_name);
        nameTextView.setText(nameText);


    }


}
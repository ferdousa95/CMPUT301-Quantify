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

public class ShowUserProfile extends AppCompatActivity implements UserProfileEditFragment.OnFragmentInteractionListener {

    String nameText;
    int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user_profile);

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
                Toast.makeText(ShowUserProfile.this, "Unable to create USER.\nUSER empty!", Toast.LENGTH_SHORT).show();
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

//        ImageView iv;
//        iv = (ImageView)findViewById(R.id.user_profile_setting);
//        iv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PopupMenu popup = new PopupMenu(ShowUserProfile.this, iv);
//                popup.getMenuInflater().inflate(R.menu.user_profile_menu, popup.getMenu());
//                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        Toast.makeText(ShowUserProfile.this,"You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
//                        return false;
//                    }
//                });
//
//
//            }
//        });

    }


    public void editName(View v) {
//        Toast.makeText(this, "Email", Toast.LENGTH_SHORT).show();
//        new UserProfileEditFragment().show(getSupportFragmentManager(), "EditName");
    }

    public void editContact(View v) {
//        Toast.makeText(this, "Email", Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, "Email", Toast.LENGTH_SHORT).show();
        new UserProfileEditFragment().show(getSupportFragmentManager(), "EditContact");
    }


//    public void editPhone(View v) {
//        Toast.makeText(this, "Phone", Toast.LENGTH_SHORT).show();
//    }
//
//    public void editTwitter(View v) {
//        Toast.makeText(this, "Email", Toast.LENGTH_SHORT).show();
//    }
//
//
//    public void editFacebook(View v) {
//        Toast.makeText(this, "Phone", Toast.LENGTH_SHORT).show();
//    }

    @Override
    public void onOkPressed(String new_text) {
        setContentView(R.layout.activity_show_user_profile);
        TextView nameTextView_1 = findViewById(R.id.contact);
        nameTextView_1.setText(new_text);
//        nameText = new_text;

//        FIREBASE STUFF AGAIN
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("Users");

        HashMap<String, String> data = new HashMap<>();
        if (nameText.length() > 0) {
            data.put("User email", "");
            data.put("User contact", new_text);
        } else {
            Toast.makeText(ShowUserProfile.this, "Unable to create USER.\nUSER empty!", Toast.LENGTH_SHORT).show();
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
//        END OF FIREBASE STUFF


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user_profile_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.user_search:
                Intent intent = new Intent(ShowUserProfile.this, UserSearch.class);
                startActivity(intent);
        }
        return true;
    }

}
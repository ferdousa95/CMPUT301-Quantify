package com.example.quantify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddNewQuestion extends AppCompatActivity {


    private String path;
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_question);

        //String path = "/Experiments/Test Trial/Questions";
        Intent intent = getIntent();
        String receive = intent.getStringExtra("PATH_TILL_QUESTION");
        setPath(receive);


    }


    public void postNewQuestion(View view) {
        TextView questionText = (TextView)findViewById(R.id.questionBody);
        String questionBody = questionText.getText().toString();
        if (questionBody == "" || questionBody.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Question is empty", Toast.LENGTH_SHORT).show();
        }
        else {
            enginePostNewQuestion(getPath());
            finish();

        }



    }


    /**
     * enginePostNewQuestion(view: View)
     * This method runs when you press the post button in the XML page. Later it collects the
     * question body, connects to the database and store the question (with other information) as a
     * document to the Questions collection.
     * Also Creates a Reply collection attached to the newly created document.
     *
     * @param path: database absolute path
     */
    public void enginePostNewQuestion(String path) {
        // Take the reference of the textView

        TextView questionText = (TextView)findViewById(R.id.questionBody);
        String questionBody = questionText.getText().toString();
        String absolutePath = path + "/" + questionBody;

        // Connect to the database and position at correct place to upload data
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Write data
        Map<String, Object> docData = new HashMap<>();
        docData.put("askedToReplyVote", 0);

        db.document(absolutePath).set(docData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "onSuccess: Question nicely updated");;
                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("QUESTION", e.toString());
                        Toast.makeText(getApplicationContext(), "Error creating the Document", Toast.LENGTH_SHORT).show();
                    }
                });


    }
}
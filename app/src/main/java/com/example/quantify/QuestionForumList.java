package com.example.quantify;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class QuestionForumList extends AppCompatActivity {

    private String experimentDoc;
    private String fullPath;


    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }


    public String getExperimentDoc() {
        return experimentDoc;
    }

    public void setExperimentDoc(String experimentDoc) {
        this.experimentDoc = experimentDoc;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_forum_list);

        // Attributes
        final String EXP_TAB = "/Experiments/";
        final String QUES_TAB = "/Question";
        List<String> list = new ArrayList<>();

        // Getting the database path for which trial these questions belong to
        Intent intent = getIntent();
        String expTitle = intent.getStringExtra("EXPNAME");
        setExperimentDoc(expTitle);

        // Combining the whole path
        String absPath = EXP_TAB + getExperimentDoc() + QUES_TAB;             // I know there is a
        setFullPath(absPath);                                                 // better way but been
        //through a lot for this

        //Method Calls
        arrangeListWithDocument(getFullPath(), list);
        interactWithQuestionList(list);

    }

    /**
     * interactWithQuestionList(list: List<String>)
     * this method is simple, it contains OnItemClickListener for the QuestionList so that whenever
     * a question is clicked, this method intents to a detail page of that Question info with
     * required information
     */
    public void interactWithQuestionList(List<String> list) {
        AdapterView.OnItemClickListener itemClickListener =
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        Intent intent = new Intent(QuestionForumList.this, QuestionDetails.class);
                        intent.putExtra("PATH_TILL_QUES", getFullPath());
                        intent.putExtra("QUESTION", list.get(position));
                        startActivity(intent);
                    }
                };

        // Time to connect with the listView from XML
        ListView listView = (ListView)findViewById(R.id.questionList);
        listView.setOnItemClickListener(itemClickListener);


    }

    /**
     * arrangeListWithDocuments(path: String, list: List<String>)
     * This method -
     *      1. connects with the database and reaches the question documents of selected experiment
     *      2. Gets ids of all the documents and store in a array
     *      3. Creates ArrayAdapter to pass the values.
     *      4. Finally, the Questions are displayed in the listview
     *
     * @param path: the absolute path in the database
     * @param list: the array to store all the questions
     */
    public void arrangeListWithDocument(String path, List<String> list) {

        List<String> user = new ArrayList<>();

        TextView textSuggestion = (TextView)findViewById(R.id.addQuestionReferenceText);
        // Firebase connection
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(path).orderBy("askedToReplyVote", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {

                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        // if the connection have an problem, we don't want the program to crash
                        if (error != null) {

                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "Error connecting with Database. Please try again later.",
                                    Toast.LENGTH_SHORT);
                            toast.show();
                            return;
                        }


                        list.clear();
                        // storing the values
                        for (DocumentSnapshot snapshot : value) {
                            list.add(snapshot.getId());
                        }


                        // ArrayAdapter
                        ArrayAdapter<String> listAdapter = new ArrayAdapter<>(
                                getApplicationContext(),
                                android.R.layout.simple_list_item_1,
                                list
                        );

                        // Finally! Set the values
                        ListView listView = (ListView) findViewById(R.id.questionList);
                        listView.setAdapter(listAdapter);
                    }


                });
    }

    /**
     * addquestion(view: View)
     * This is a button method, the purpose of this method is to open a new page.
     */
    public void addQuestion(View view) {
        Intent intent = new Intent(this, AddNewQuestion.class);
        intent.putExtra("PATH_TILL_QUESTION", getFullPath());
        startActivity(intent);
    }
}

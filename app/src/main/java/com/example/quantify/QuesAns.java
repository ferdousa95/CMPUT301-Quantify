package com.example.quantify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class QuesAns extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ques_ans);

        Intent intent = getIntent();
        String storeQues = intent.getStringExtra("QUESTION");
        String storeAns = intent.getStringExtra("REPLY");

        TextView ques = (TextView)findViewById(R.id.quesBox);
        TextView ans = (TextView)findViewById(R.id.replyBox);

        ques.setText(storeQues);
        ans.setText(storeAns);
    }
}
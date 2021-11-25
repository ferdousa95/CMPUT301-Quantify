package com.example.quantify;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import java.util.ArrayList;


public class UserList extends ArrayAdapter<User> {

    private ArrayList<User> users;
    private Context context;

    public UserList(Context context, ArrayList<User> users){
        super(context, 0, users);
        this.users = users;
        this.context = context;
    }
    @NonNull
    @Override

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View view = convertView;
        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.user_card, parent, false);
        }
        User user = users.get(position);

        LinearLayout card = view.findViewById(R.id.user_cardView);

        TextView userID = view.findViewById(R.id.user_id);
        TextView userNum = view.findViewById(R.id.user_contact);

        userID.setText(user.getUserID());
        userNum.setText(user.getPhoneNum());

        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uid = (String) getItem(position).getUserID();
                Intent intent = new Intent(context, ShowUserSearchResult.class);
                intent.putExtra("USER", uid);
//
                context.startActivity(intent);
            }
        });

        return view;
    }
}

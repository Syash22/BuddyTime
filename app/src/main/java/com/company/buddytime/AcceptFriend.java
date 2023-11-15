package com.company.buddytime;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class AcceptFriend extends AppCompatActivity {

    ListView exampleList;
    ArrayList<String> dataSample;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accept_friend);

        dataSample = new ArrayList<String>();

        dataSample.add("Test1");
        dataSample.add("Test2");
        dataSample.add("Test3");

        exampleList = findViewById(R.id.acpt_listview);
        ButtonListAdapter buttonListAdapter = new ButtonListAdapter(this, dataSample);

        exampleList.setAdapter(buttonListAdapter);
    }
}
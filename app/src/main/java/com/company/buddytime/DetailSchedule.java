package com.company.buddytime;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class DetailSchedule extends AppCompatActivity {
    TextView Date, Time, Title, Contents;
    Button editBtn, deleteBtn;
    Switch sharedSw;


    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_schedule_new);

        db = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        String documentId = intent.getStringExtra("documentId");

        DateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm");
        Date = findViewById(R.id.todayDate);
        Time = findViewById(R.id.targetTime);
        Title = findViewById(R.id.schTitle);
        Contents = findViewById(R.id.schcontents);
        editBtn = findViewById(R.id.editButton);
        deleteBtn = findViewById(R.id.deleteButton);
        sharedSw = findViewById(R.id.sharedSwitch);

        DocumentReference docRef = db.collection("schedule").document(documentId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Title.setText(document.getString("title"));
                        Contents.setText(document.getString("contents"));
                        String time1 = timeFormat.format(document.getTimestamp("time1").toDate());
                        String time2 = timeFormat.format(document.getTimestamp("time2").toDate());
                        String time3 = time1 + " ~ " + time2;
                        Time.setText(time3);
                        Date.setText(dateFormat.format(document.getTimestamp("time1").toDate()));

                        boolean isShared = document.getBoolean("shared");
                        sharedSw.setChecked(isShared);

                        Log.d(TAG, "DocumentSnapshot data: " + dateFormat.format(document.getTimestamp("time1").toDate()));

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        sharedSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // 스위치 상태 변경에 따라 Firestore 문서 업데이트
                updateSharedField(documentId, isChecked);
            }
        });
    }
    private void updateSharedField(String documentId, boolean isChecked) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("shared", isChecked);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("schedule").document(documentId);
        docRef.update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }
}
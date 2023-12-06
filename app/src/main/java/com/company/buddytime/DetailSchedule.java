package com.company.buddytime;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
    String documentId, time1, time2, time3;
    DateFormat dateFormat, timeFormat;
    private static final int EDIT_SCHEDULE_REQUEST = 1;


    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_schedule_new);

        db = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        documentId = intent.getStringExtra("documentId");

        dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
        timeFormat = new SimpleDateFormat("HH:mm");
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
                        time1 = timeFormat.format(document.getTimestamp("time1").toDate());
                        time2 = timeFormat.format(document.getTimestamp("time2").toDate());
                        time3 = time1 + " ~ " + time2;
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

        editBtn.setOnClickListener(view -> {
            // EditSchedule 액티비티를 시작하기 위한 Intent 생성
            Intent editIntent = new Intent(DetailSchedule.this, editSchedule.class);
            editIntent.putExtra("documentId", documentId);
            startActivityForResult(editIntent, EDIT_SCHEDULE_REQUEST);
        });
        deleteBtn.setOnClickListener(view -> {
            // 모달 다이얼로그 표시
            AlertDialog.Builder builder = new AlertDialog.Builder(DetailSchedule.this);
            builder.setTitle("삭제 확인");
            builder.setMessage("일정을 삭제하시겠습니까?");

            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // 사용자가 확인을 누른 경우, 문서 삭제
                    deleteSchedule(documentId);
                }
            });

            builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // 사용자가 취소를 누른 경우, 아무 작업 없음
                }
            });

            // 다이얼로그 표시
            builder.show();
        });
    }
    private void deleteSchedule(String documentId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("schedule").document(documentId);
        docRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        // 삭제 후 액티비티 종료 또는 다른 작업 수행
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                        // 삭제 실패 시 사용자에게 알림
                        Toast.makeText(
                                DetailSchedule.this, "일정 삭제 실패.",
                                Toast.LENGTH_SHORT
                        ).show();
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_SCHEDULE_REQUEST) {

            if (resultCode == Activity.RESULT_OK) {
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
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // 사용자가 수정을 취소하거나 다른 이유로 돌아온 경우의 처리
            }
        }
    }
}
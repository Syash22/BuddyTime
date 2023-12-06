package com.company.buddytime;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class editSchedule extends AppCompatActivity {
    EditText editTitle;
    TextView Date1, Time1, Time2, Title, Contents, Category;
    private DatePickerDialog.OnDateSetListener callbackDate;

    private int selectedHour;
    private int selectedMinute;
    Button editBtn;
    Switch sharedSw;
    String startTime, endTime;


    FirebaseFirestore db;
    String documentId;
    // 다른 필드도 필요한 경우 추가...

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_schedule);
        Intent intent = getIntent();
        documentId = intent.getStringExtra("documentId");
        db = FirebaseFirestore.getInstance();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm");
        Date1 = findViewById(R.id.setDate);
        Time1 = findViewById(R.id.input_time);
        Time2 = findViewById(R.id.input_time2);
        Title = findViewById(R.id.input_title);
        Category = findViewById(R.id.input_category);
        Contents = findViewById(R.id.input_contents);
        editBtn = findViewById(R.id.editbtn);
        sharedSw = findViewById(R.id.sharedSw);

        DocumentReference docRef = db.collection("schedule").document(documentId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Title.setText(document.getString("title"));
                        Contents.setText(document.getString("contents"));
                        Category.setText(document.getString("category"));
                        String time1 = timeFormat.format(document.getTimestamp("time1").toDate());
                        String time2 = timeFormat.format(document.getTimestamp("time2").toDate());
                        Time1.setText(time1);
                        Time2.setText(time2);
                        Date1.setText(dateFormat.format(document.getTimestamp("time1").toDate()));

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
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Title = findViewById(R.id.input_title);
                    Date1 = findViewById(R.id.setDate);
                    startTime = Date1.getText().toString() + " " + Time1.getText().toString() + ":00";
                    endTime = Date1.getText().toString() + " " + Time2.getText().toString() + ":00";
                    Category = findViewById(R.id.input_category);
                    Contents = findViewById(R.id.input_contents);

                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date userDateTime1 = dateFormat.parse(startTime);
                    Date userDateTime2 = dateFormat.parse(endTime);

                    // 수정할 데이터를 Map에 담기
                    Map<String, Object> updatedSchedule = new HashMap<>();
                    updatedSchedule.put("title", Title.getText().toString());
                    updatedSchedule.put("time1", userDateTime1);
                    updatedSchedule.put("time2", userDateTime2);
                    updatedSchedule.put("category", Category.getText().toString());
                    updatedSchedule.put("contents", Contents.getText().toString());
                    // ownerid는 그대로 유지되어야 하면 추가

                    // 해당 문서를 찾아서 데이터 업데이트
                    db.collection("schedule")
                            .document(documentId)
                            .update(updatedSchedule)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "DocumentSnapshot successfully updated!");
                                    Toast.makeText(editSchedule.this, "일정이 수정 되었습니다.",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                    Intent resultIntent = new Intent();
                                    setResult(Activity.RESULT_OK, resultIntent);

                                    // 현재 액티비티 종료
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error updating document", e);
                                    Toast.makeText(
                                            editSchedule.this, "일정 수정 실패.",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }
                            });
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
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
    public void OnClickHandler(View view)
    {
        DatePickerDialog dialog = new DatePickerDialog(this, callbackDate, 2023, 12, 7);

        dialog.show();
    }

    public void startTimePick(View view) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        selectedHour = hourOfDay;
                        selectedMinute = minute;
                        Time1.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
                    }
                }, hour, minute, true);
        timePickerDialog.show();
    }

    public void endTimePick(View view) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        selectedHour = hourOfDay;
                        selectedMinute = minute;
                        Time2.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
                    }
                }, hour, minute, true);
        timePickerDialog.show();
    }
}


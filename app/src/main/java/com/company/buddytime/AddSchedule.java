package com.company.buddytime;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddSchedule extends AppCompatActivity {
    private TextView textView_Date;
    private TextView textView_Time1, textView_Time2;
    private DatePickerDialog.OnDateSetListener callbackDate;
    private int selectedHour;
    private int selectedMinute;
    Switch sharedSwitch;
    TextView title, category, contents;
    String startTime, endTime;
    Boolean shared = false;
    FirebaseUser user;
    String userEnterDate;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    String currentDate = sdf.format(new Date());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_schedule);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Button addBtn = (Button) findViewById(R.id.addbtn);
        sharedSwitch = findViewById(R.id.sharedSw);
        user = FirebaseAuth.getInstance().getCurrentUser();
        textView_Date = findViewById(R.id.setDate);
        textView_Date.setText(currentDate);

        this.InitializeView();
        this.InitializeListener();

        sharedSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonview, boolean ischecked) {
                if(ischecked)
                    shared = true;
                else
                    shared = false;
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    title = findViewById(R.id.input_title);
                    textView_Date = findViewById(R.id.setDate);
                    startTime = textView_Date.getText().toString() + " " + textView_Time1.getText().toString() + ":00";
                    endTime = textView_Date.getText().toString() + " " + textView_Time2.getText().toString() + ":00";
                    category = findViewById(R.id.input_category);
                    contents = findViewById(R.id.input_contents);
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date userDateTime1 = dateFormat.parse(startTime);
                    Date userDateTime2 = dateFormat.parse(endTime);
                    Map<String, Object> schedule = new HashMap<>();
                    schedule.put("title", title.getText().toString());
                    schedule.put("time1", userDateTime1);
                    schedule.put("time2", userDateTime2);
                    schedule.put("category", category.getText().toString());
                    schedule.put("contents", contents.getText().toString());
                    schedule.put("shared", shared);
                    schedule.put("ownerid", user.getEmail());


                    db.collection("schedule")
                            .add(schedule)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                                    Toast.makeText(
                                            AddSchedule.this, "일정이 생성 되었습니다.",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error adding document", e);
                                    Toast.makeText(
                                            AddSchedule.this, "일정 생성 실패.",
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

    private void InitializeListener() {
        callbackDate = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
                textView_Date.setText(String.format("%04d-%02d-%02d", year, monthOfYear+1, dayOfMonth));
            }
        };
    }

    public void OnClickHandler(View view)
    {
        DatePickerDialog dialog = new DatePickerDialog(this, callbackDate, 2023, 10, 17);

        dialog.show();
    }
    private void InitializeView() {

        textView_Date = (TextView)findViewById(R.id.setDate);
        textView_Time1 = (TextView) findViewById(R.id.input_time);
        textView_Time2 = (TextView) findViewById(R.id.input_time2);
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
                        textView_Time1.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
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
                        textView_Time2.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
                    }
                }, hour, minute, true);
        timePickerDialog.show();
    }
}

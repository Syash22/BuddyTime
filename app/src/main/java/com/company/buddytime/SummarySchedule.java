package com.company.buddytime;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SummarySchedule extends Activity {
    TextView title;
    String fdEmail;
    private ListView listView;
    private DatePickerDialog.OnDateSetListener callbackDate;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    String currentDate = sdf.format(new Date());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.summary_schedule);
        this.InitializeListener1();
        title = findViewById(R.id.sumTitle);
        listView = findViewById(R.id.fdList);
        title.setText(currentDate);

        // 받아온 이메일 정보
        Intent intent = getIntent();
        fdEmail = intent.getStringExtra("friendEmail");

        // 검색할 날짜
        String selectedDate = title.getText().toString(); // "yyyy-MM-dd" 형식의 문자열

        // Firestore에서 데이터 가져오기
        fetchDataFromFirestore(fdEmail, selectedDate);
    }

    public void OnClickHandler1(View view) {
        DatePickerDialog dialog = new DatePickerDialog(this, callbackDate, 2023, 12, 7);
        dialog.show();
    }

    private void InitializeListener1() {
        callbackDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                title.setText(String.format("%04d-%02d-%02d", year, monthOfYear + 1, dayOfMonth));

                // 선택된 날짜로 Firestore에서 데이터 다시 가져오기
                fetchDataFromFirestore(fdEmail, title.getText().toString());
            }
        };
    }

    private void fetchDataFromFirestore(String fdEmail, String selectedDate) {
        // Firestore 쿼리 작성
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date;
        try {
            date = sdf.parse(selectedDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }
        Timestamp selectedTimestamp = new Timestamp(date);

        // 선택된 날짜의 다음 날을 계산
        Calendar nextDay = Calendar.getInstance();
        nextDay.setTime(date);
        nextDay.add(Calendar.DAY_OF_MONTH, 1);

        // 다음 날의 Timestamp 형식으로 변환
        Timestamp nextDayTimestamp = new Timestamp(nextDay.getTime());

        // Firestore 쿼리 작성
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference scheduleCollection = db.collection("schedule");
        Query query = scheduleCollection
                .whereEqualTo("ownerid", fdEmail)
                .whereGreaterThanOrEqualTo("time1", selectedTimestamp)
                .whereLessThan("time1", nextDayTimestamp)
                .whereEqualTo("shared", true);

        // 쿼리 실행
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<ScheduleItem> scheduleItems = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> documentData = document.getData();

                        String title = (String) documentData.get("title");
                        Timestamp time1Timestamp = (Timestamp) documentData.get("time1");
                        Date time1Date = time1Timestamp.toDate();

                        ScheduleItem scheduleItem = new ScheduleItem(title, time1Date, document.getId());
                        // 여기에서 document.getId()를 ScheduleItem에 저장합니다.
                        scheduleItems.add(scheduleItem);
                    }
                    updateListView(scheduleItems);
                } else {
                    // 오류 처리
                    Log.e("Firestore", "문서 가져오기 오류: ", task.getException());
                }
            }
        });
    }
    private void updateListView(ArrayList<ScheduleItem> scheduleItems) {
        ScheduleListAdapter adapter = new ScheduleListAdapter(this, scheduleItems);
        listView.setAdapter(adapter);
    }
}

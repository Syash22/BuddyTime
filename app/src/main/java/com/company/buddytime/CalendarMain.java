package com.company.buddytime;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class CalendarMain extends AppCompatActivity implements CalendarAdapter.OnDateClickListener {
    private FirebaseAuth auth;
    private TextView monthYearText;
    private LocalDate selectedDate;
    private RecyclerView recyclerView;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_main);

        auth = FirebaseAuth.getInstance();
        monthYearText = findViewById(R.id.monthYearText);
        ImageButton prevBtn = findViewById(R.id.pre_btn);
        ImageButton nextBtn = findViewById(R.id.next_btn);
        recyclerView = findViewById(R.id.recyclerView);
        listView = findViewById(R.id.summary_list);

        selectedDate = LocalDate.now();
        setMonthView();

        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedDate = selectedDate.minusMonths(1);
                setMonthView();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedDate = selectedDate.plusMonths(1);
                setMonthView();
            }
        });
    }

    private void setMonthView() {
        monthYearText.setText(monthYearFromDate(selectedDate));
        ArrayList<LocalDate> dayList = daysInMonthArray(selectedDate);
        CalendarAdapter adapter = new CalendarAdapter(dayList);
        adapter.setOnDateClickListener(this); // 이벤트 리스너 설정

        RecyclerView.LayoutManager manager = new GridLayoutManager(getApplicationContext(), 7);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
    }

    private String monthYearFromDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM월 yyyy");
        return date.format(formatter);
    }

    private ArrayList<LocalDate> daysInMonthArray(LocalDate date) {
        ArrayList<LocalDate> dayList = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);
        int lastDay = yearMonth.lengthOfMonth();
        LocalDate firstDay = selectedDate.withDayOfMonth(1);
        int dayOfWeek = firstDay.getDayOfWeek().getValue();

        for (int i = 1; i < 42; i++) {
            if (i <= dayOfWeek || i > lastDay + dayOfWeek) {
                dayList.add(null);
            } else {
                dayList.add(LocalDate.of(selectedDate.getYear(), selectedDate.getMonth(), i - dayOfWeek));
            }
        }
        return dayList;
    }

    // OnDateClickListener의 메서드 구현
    @Override
    public void onDateClick(LocalDate selectedDate) {
        // 사용자가 선택한 날짜에 대한 데이터를 Firestore에서 가져오기
        fetchDataFromFirestore(selectedDate);
    }

    private void fetchDataFromFirestore(LocalDate selectedDate) {
        String currentUserEmail = auth.getCurrentUser().getEmail();
        Date startDate = Date.from(selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(selectedDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        Timestamp startTimestamp = new Timestamp(startDate);
        Timestamp endTimestamp = new Timestamp(endDate);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference scheduleCollection = db.collection("schedule");
        Query query = scheduleCollection
                .whereEqualTo("ownerid", currentUserEmail)
                .whereGreaterThanOrEqualTo("time1", startTimestamp)
                .whereLessThan("time1", endTimestamp);

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
                    Log.e("Firestore", "문서 가져오기 오류: ", task.getException());
                }
            }
        });
    }

    private void updateListView(ArrayList<ScheduleItem> scheduleItems) {
        ScheduleListAdapter adapter = new ScheduleListAdapter(this, scheduleItems);
        listView.setAdapter(adapter);

        // 리스트뷰 아이템 클릭 이벤트 처리
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 클릭된 아이템의 문서 ID를 가져옴
                String documentId = scheduleItems.get(position).getDocumentId();

                // DetailSchedule 액티비티 실행
                startDetailScheduleActivity(documentId);
            }
        });
    }
    private void startDetailScheduleActivity(String documentId) {
        Intent intent = new Intent(this, DetailSchedule.class);
        intent.putExtra("documentId", documentId);
        startActivity(intent);
    }
}

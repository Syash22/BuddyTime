package com.company.buddytime;


import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class CalendarMain extends AppCompatActivity {
    private FirebaseAuth auth;
    TextView monthYearText; //년월 텍스트뷰

    LocalDate selectedDate; //년월 변수
    RecyclerView recyclerView; //Recycler View 객체 추가

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_main);
        ListView listView = (ListView)findViewById(R.id.summary_list);
        SingerAdapter adapter = new SingerAdapter();
        //adapter에 data값을 줘봅시다.
        adapter.addItem(new SingerItem("일정이름","집가기"));
        adapter.addItem(new SingerItem("일정이름2","집에서도 집가기"));


        listView.setAdapter(adapter);

        auth = FirebaseAuth.getInstance();

        //초기화
        monthYearText = findViewById(R.id.monthYearText);
        ImageButton prevBtn = findViewById(R.id.pre_btn);
        ImageButton nextBtn = findViewById(R.id.next_btn);
        recyclerView = findViewById(R.id.recyclerView);

        //현재 날짜
        selectedDate = LocalDate.now();

        //화면 설정
        setMonthView();

        //이전달 버튼 이벤트
        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //현재 월-1 변수에 담기
                selectedDate = selectedDate.minusMonths(1);
                setMonthView();
            }
        });

        //다음달 버튼 이벤트
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //현재 월+1 변수에 담기
                selectedDate = selectedDate.plusMonths(1);
                setMonthView();
            }
        });
    }//onCreate


    /**
     * 날짜 타입 설정
     * @param date 날짜
     * @return
     */
    private String monthYearFromDate(LocalDate date){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM월 yyyy");
        return date.format(formatter);
    }
    /**
     * 화면 설정
     */
    private void setMonthView() {
        //년월 텍스트뷰 셋팅
        monthYearText.setText(monthYearFromDate(selectedDate));
        ArrayList<LocalDate> dayList = daysInMonthArray(selectedDate);
        CalendarAdapter adapter = new CalendarAdapter(dayList);
        //레이아웃 설정( 열 7개)
        RecyclerView.LayoutManager manager = new GridLayoutManager(getApplicationContext(), 7);
        //레이아웃 적용
        recyclerView.setLayoutManager(manager);
        //어뎁터 적용
        recyclerView.setAdapter(adapter);
    }
    private ArrayList<LocalDate> daysInMonthArray(LocalDate date){
        ArrayList<LocalDate> dayList = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);
        //해당 월 마지막 날짜 가져오기(예: 28,30,31)
        int lastDay = yearMonth.lengthOfMonth();
        //해당 월의 첫번째 날 가져오기(예: 4월1일)
        LocalDate firstDay = selectedDate.withDayOfMonth(1);
        //첫번째 날 요일 가져오기 (월: 1 , 일: 7)
        int dayOfWeek = firstDay.getDayOfWeek().getValue();
        for(int i = 1; i < 42; i++){
            if(i <= dayOfWeek || i > lastDay + dayOfWeek){
                dayList.add(null);
            }else{
                dayList.add(LocalDate.of(selectedDate.getYear(), selectedDate.getMonth(), i - dayOfWeek));
            }
        }
        return dayList;
    }
    class SingerAdapter extends BaseAdapter {
        //어뎁터가 데이터를 관리하며 데이터를 넣었다가 뺄 수도 있으므로 ArrayList를 활용하여 구현해보자.

        ArrayList<SingerItem> items = new ArrayList<SingerItem>();
        //걸그룹의 이름, 전화번호 등등이 필요할텐데 이거 하나로는 부족하니까
        //데이터형을 다양하게 담고있는 java파일을 하나 더 만들어줄거에요

        //!! 그런데 ArrayList에 데이터를 넣는 기능이 지금 없으므로 함수를 하나 더 만들어줄게요
        public void addItem(SingerItem item){
            items.add(item);
        }

        //너네 어뎁터 안에 몇 개의 아이템이 있니? 아이템갯수 반환함수
        @Override
        public int getCount() {
            return items.size(); //위의 ArrayList내부의 아이템이 몇 개나 들었는지 알려주게됨
        }

        @Override
        public Object getItem(int position) {
            return items.get(position); //position번째의 아이템을 얻을거야.
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        //이게 중요! 어뎁터가 데이터를 관리하기 때문에 화면에 보여질 각각의 화면에 보일 뷰도 만들어달라는 것
        //각각의 아이템 데이터 뷰(레이아웃)을 만들어주어 객체를 만든다음에 데이터를 넣고 리턴해줄 것임
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SingerItemView view = new SingerItemView(getApplicationContext());
            //어떤 뷰든 안드로이드에서는 Context객체를 받게 되어있으므로 getApplicationCotext로 넣어줍니다.

            //이제 이 뷰를 반환해주면 되는데 이 뷰가 몇 번째 뷰를 달라는 것인지 position값이 넘어오므로
            SingerItem item  = items.get(position); //SigerItem은 참고로 Dataset임. 따로 기본적인것만 구현해놓음
            //이 position값을 갖는 아이템의 SigerItem객체를 새로 만들어준 뒤
            view.setName(item.getName());
            view.setMobile(item.getMoblie());
            //이렇게 해당 position에 맞는 값으로 설정해줍니다.

            //그렇게 설정을 잘 해놓은 다음에 view를 반환해야 데이터값이 들어간 레이아웃이 반환될거에요~
            return view;
        }
    }
}
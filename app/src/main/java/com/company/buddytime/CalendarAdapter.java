package com.company.buddytime;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>{

    ArrayList<LocalDate> dayList;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentUserEmail;
    int selectedPosition = RecyclerView.NO_POSITION; //클릭한 아이템의 위치를 저장
    private OnDateClickListener onDateClickListener;
    public void setOnDateClickListener(OnDateClickListener listener) {
        this.onDateClickListener = listener;
    }


    public CalendarAdapter(ArrayList<LocalDate> dayList) {

        this.dayList = dayList;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.calendar_main_cell, parent, false);

        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {

        //날짜 변수에 담기
        LocalDate day = dayList.get(position);
        if(day == null) {
            holder.dayText.setText("");
        }else{
            //해당 일자를 넣는다.
            holder.dayText.setText(String.valueOf(day.getDayOfMonth()));
        }

        //텍스트 색상 지정
        if( (position + 1) % 7 == 0){ //토요일 파랑
            holder.dayText.setTextColor(Color.BLUE);
        }
        else if( position == 0 || position % 7 == 0){ //일요일 빨강
            holder.dayText.setTextColor(Color.RED);
        }
        //날짜 클릭 이벤트
        if (position == selectedPosition){
            holder.itemView.setBackgroundResource(R.drawable.calendar_square);
        } else{
            holder.itemView.setBackground(null);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int previousSelectedPosition = selectedPosition;
                selectedPosition = holder.getAdapterPosition();

                if(previousSelectedPosition != RecyclerView.NO_POSITION){
                    notifyItemChanged(previousSelectedPosition);
                }
                notifyDataSetChanged();
                // 클릭된 날짜의 뷰를 강조
                view.setBackgroundResource(R.drawable.calendar_square);
                LocalDate selectedDate = dayList.get(selectedPosition);
                Toast.makeText(view.getContext(), "Selected date: " + selectedDate, Toast.LENGTH_SHORT).show();
                if (onDateClickListener != null) {
                    onDateClickListener.onDateClick(selectedDate);
                }
                currentUserEmail = user.getEmail();
                Date startDate = Date.from(selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                Date endDate = Date.from(selectedDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

                Timestamp startTimestamp = new Timestamp(startDate);
                Timestamp endTimestamp = new Timestamp(endDate);

                // Firestore 쿼리 작성
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference scheduleCollection = db.collection("schedule");
                Query query = scheduleCollection
                        .whereEqualTo("ownerid", currentUserEmail)
                        .whereGreaterThanOrEqualTo("time1", startTimestamp)
                        .whereLessThan("time1", endTimestamp);

                // 쿼리 실행
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> documentData = document.getData();

                                // 가져온 문서의 모든 필드 정보를 로그에 출력
                                for (Map.Entry<String, Object> entry : documentData.entrySet()) {
                                    String fieldName = entry.getKey();
                                    Object value = entry.getValue();
                                    Log.d("Firestore", "Field: " + fieldName + ", Value: " + value);
                                }
                            }
                        } else {
                            // 오류 처리
                            Log.e("Firestore", "문서 가져오기 오류: ", task.getException());
                        }
                    }
                });
            }
        });
    }
    /* 여기에서 선택된 날짜에 대한 작업을 수행할 수 있습니다.
    LocalDate selectedDate = dayList.get(selectedPosition);
    선택된 날짜에 대한 처리 작업 수행
            Toast.makeText(view.getContext(), "Selected date: " + selectedDate, Toast.LENGTH_SHORT).show(); */
    @Override
    public int getItemCount() {
        return dayList.size();
    }

    class CalendarViewHolder extends RecyclerView.ViewHolder{

        //초기화
        TextView dayText;

        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);

            dayText = itemView.findViewById(R.id.dayText);
        }
    }
    public interface OnDateClickListener {
        void onDateClick(LocalDate selectedDate);
    }
}
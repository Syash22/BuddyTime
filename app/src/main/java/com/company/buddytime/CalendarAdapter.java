package com.company.buddytime;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.ArrayList;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>{

    ArrayList<LocalDate> dayList;
    int selectedPosition = RecyclerView.NO_POSITION; //클릭한 아이템의 위치를 저장

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
}
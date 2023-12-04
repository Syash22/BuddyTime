package com.company.buddytime;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ScheduleListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ScheduleItem> scheduleItems;

    public ScheduleListAdapter(Context context, ArrayList<ScheduleItem> scheduleItems) {
        this.context = context;
        this.scheduleItems = scheduleItems;
    }

    @Override
    public int getCount() {
        return scheduleItems.size();
    }

    @Override
    public Object getItem(int position) {
        return scheduleItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.singer_item, parent, false);
        }

        // 뷰를 업데이트하기 위해 현재 포지션의 ScheduleItem 객체 가져오기
        ScheduleItem scheduleItem = scheduleItems.get(position);

        // 뷰의 텍스트뷰에 데이터 설정
        TextView titleTextView = convertView.findViewById(R.id.schetitle);
        TextView time1TextView = convertView.findViewById(R.id.test2);

        titleTextView.setText(scheduleItem.getTitle());

        // SimpleDateFormat을 사용하여 시간을 형식에 맞게 표시
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        time1TextView.setText(dateFormat.format(scheduleItem.getTime1()));

        return convertView;
    }
}

package com.company.buddytime;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class ButtonListAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<String> data;
    private AcceptFriend activity; // AcceptFriend 액티비티 참조 추가
    public ButtonListAdapter(Context context, ArrayList<String> data, AcceptFriend activity) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.data = data;
        this.activity = activity; // 액티비티 참조 초기화
    }
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = layoutInflater.inflate(R.layout.accept_friend_listview, null);

        TextView textView = view.findViewById(R.id.textView1);
        textView.setText(data.get(position));

        Button accBtn = view.findViewById(R.id.acceptBtn);
        Button dclBtn = view.findViewById(R.id.declineBtn);

        accBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 액티비티의 메서드 호출 대신, 어댑터의 메서드 호출
                activity.onAcceptButtonClick(data.get(position));
            }
        });

        dclBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 액티비티의 메서드 호출 대신, 어댑터의 메서드 호출
                activity.onDeclineButtonClick(data.get(position));
            }
        });

        return view;
    }
}
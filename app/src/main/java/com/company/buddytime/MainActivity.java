package com.company.buddytime;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();

        Button sumbtn = findViewById(R.id.summarybtn);
        Button logoutButton = findViewById(R.id.logoutbtn);
        Button addschebtn = findViewById(R.id.addSchedule);
        Button detail = findViewById(R.id.detailbtn);
        Button req = findViewById(R.id.reqBtn);
        Button act = findViewById(R.id.actBtn);
        Button calman = findViewById(R.id.maincalBtn);
        Button fdBtn = findViewById(R.id.fdBtn);
        // 로그아웃
        sumbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SummarySchedule.class);
                startActivity(intent);
            }
        });
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 로그인 화면으로 이동
                Intent intent = new Intent(MainActivity.this, LoginSignIn.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                auth.signOut();
            }
        });
        addschebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 일정 추가 화면으로 이동
                Intent intent = new Intent(getApplicationContext(), AddSchedule.class);
                startActivity(intent);
            }
        });
        detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 일정 추가 화면으로 이동
                Intent intent = new Intent(getApplicationContext(), DetailSchedule.class);
                startActivity(intent);
            }
        });
        req.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 일정 추가 화면으로 이동
                Intent intent = new Intent(getApplicationContext(), RequestFriend.class);
                startActivity(intent);
            }
        });
        act.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 일정 추가 화면으로 이동
                Intent intent = new Intent(getApplicationContext(), AcceptFriend.class);
                startActivity(intent);
            }
        });
        fdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 일정 추가 화면으로 이동
                Intent intent = new Intent(getApplicationContext(), FriendMain.class);
                startActivity(intent);
            }
        });
        calman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 캘린더 메인 화면으로 이동
                Intent intent = new Intent(getApplicationContext(), CalendarMain.class);
                startActivity(intent);
            }
        });

    }



}

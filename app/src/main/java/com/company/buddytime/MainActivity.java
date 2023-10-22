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
        // 로그아웃
        sumbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, summary_schedule.class);
                startActivity(intent);
            }
        });
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 로그인 화면으로 이동
                Intent intent = new Intent(MainActivity.this, login_signin.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                auth.signOut();
            }
        });
        addschebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 일정 추가 화면으로 이동
                Intent intent = new Intent(getApplicationContext(), add_schedule.class);
                startActivity(intent);
            }
        });
        detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 일정 추가 화면으로 이동
                Intent intent = new Intent(getApplicationContext(), detail_schedule.class);
                startActivity(intent);
            }
        });

    }



}

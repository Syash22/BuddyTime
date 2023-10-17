package com.company.buddytime;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.ktx.*;
import com.google.firebase.ktx.Firebase;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();

        Button logoutButton = findViewById(R.id.logoutbtn);
        Button addschebtn = findViewById(R.id.addSchedule);
        // 로그아웃
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
    }

}

package com.company.buddytime;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.Nullable;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class login_signup extends AppCompatActivity {
    Button signupbtn;
    private LoginActivity auth;
    EditText inEmail, inPasswd;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_signup);
        auth = new LoginActivity();

        inEmail = findViewById(R.id.InputEmailAddress);
        inPasswd = findViewById(R.id.InputPasswd);
        signupbtn = (Button) findViewById(R.id.signupbtn);
        signupbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String email = inEmail.getText().toString();
                String passwd = inPasswd.getText().toString();
                auth.createAccount(email, passwd, new LoginActivity.AccountCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(
                                login_signup.this, "계정 생성 완료.",
                                Toast.LENGTH_SHORT
                        ).show();
                        finish();
                    }

                    @Override
                    public void onFailure() {
                        Toast.makeText(
                                login_signup.this, "계정 생성 실패",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
            }
        });
    }

}

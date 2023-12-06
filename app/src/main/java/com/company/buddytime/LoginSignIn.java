package com.company.buddytime;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginSignIn extends AppCompatActivity {
    TextView text;
    EditText inEmail, inPasswd;
    private LoginActivity auth;
    private FirebaseAuth Auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_signin);
        Auth = FirebaseAuth.getInstance();


        inEmail = findViewById(R.id.editTextTextEmailAddress);
        inPasswd = findViewById(R.id.editTextTextPassword);
        text = (TextView) findViewById(R.id.textView);
        auth = new LoginActivity();
        text.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), LoginSignUp.class);
                startActivity(intent);

            }
        }
        );

        findViewById(R.id.signinbtn).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String email = inEmail.getText().toString();
                String passwd = inPasswd.getText().toString();

                auth.signIn(email, passwd, new LoginActivity.AccountCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(
                                getBaseContext(), "로그인에 성공 하였습니다.",
                                Toast.LENGTH_SHORT
                        ).show();
                        moveMainPage(Auth.getCurrentUser());
                    }

                    @Override
                    public void onFailure() {
                        Toast.makeText(
                                getBaseContext(), "로그인에 실패 하였습니다.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
            }
        });
    }

    private void moveMainPage(FirebaseUser user) {
        if (user != null) {
            startActivity(new Intent(this, CalendarMain.class));
            finish();
        }
    }
}
package com.company.buddytime;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.Nullable;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginSignUp extends AppCompatActivity {
    Button signupbtn;
    private LoginActivity auth;
    EditText inEmail, inPasswd;
    FirebaseFirestore db;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_signup);
        auth = new LoginActivity();
        db = FirebaseFirestore.getInstance();

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
                        Map<String, Object> users = new HashMap<>();
                        users.put("email", email);
                        db.collection("users")
                                .add(users)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {

                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                });

                        Toast.makeText(
                                LoginSignUp.this, "계정 생성 완료.",
                                Toast.LENGTH_SHORT
                        ).show();
                        finish();
                    }

                    @Override
                    public void onFailure() {
                        Toast.makeText(
                                LoginSignUp.this, "계정 생성 실패",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
            }
        });
    }

}

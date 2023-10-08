package com.company.buddytime;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
public class LoginActivity {
    private FirebaseAuth mAuth;

    public LoginActivity() {
        mAuth = FirebaseAuth.getInstance();
    }

    public void createAccount(String email, String password, final CreateAccountCallback callback) {
        if (!email.isEmpty() && !password.isEmpty()) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                callback.onSuccess();
                            } else {
                                callback.onFailure();
                            }
                        }
                    });
        }
    }

    public interface CreateAccountCallback {
        void onSuccess();

        void onFailure();
    }
}

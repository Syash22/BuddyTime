package com.company.buddytime;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FriendMain extends AppCompatActivity {
    private FirebaseAuth auth;
    private ListView friendsListView;
    private ArrayList<String> friendEmailList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_main);

        auth = FirebaseAuth.getInstance();
        friendsListView = findViewById(R.id.friendsListView);
        friendEmailList = new ArrayList<>();
        TextView addBtn = findViewById(R.id.addBtn);
        TextView accBtn = findViewById(R.id.accBtn);

        // 현재 로그인한 유저의 이메일 가져오기
        String currentUserEmail = auth.getCurrentUser().getEmail();

        // Friends 컬렉션에서 currentUserEmail과 일치하는 문서들의 searchEmail 필드 값을 가져오기
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference friendsCollection = db.collection("friends");

        friendsCollection.whereEqualTo("currentUserEmail", currentUserEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // 검색된 문서의 searchEmail 값을 가져와서 다시 Friends 컬렉션에서 검색
                                String searchEmail = document.getString("searchEmail");
                                if (searchEmail != null) {
                                    searchFriendsByEmail(searchEmail);
                                }
                            }
                        } else {
                            Log.e("Firestore", "Friends 컬렉션 검색 오류: ", task.getException());
                        }
                    }
                });
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When addBtn is clicked, start the RequestFriend activity
                Intent intent = new Intent(FriendMain.this, RequestFriend.class);
                startActivity(intent);
            }
        });
        accBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When addBtn is clicked, start the RequestFriend activity
                Intent intent = new Intent(FriendMain.this, AcceptFriend.class);
                startActivity(intent);
            }
        });
        friendsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 선택된 친구의 이메일 가져오기
                String selectedFriendEmail = friendEmailList.get(position);

                // SummarySchedule 액티비티 실행
                startSummaryScheduleActivity(selectedFriendEmail);
            }
        });
    }
    private void startSummaryScheduleActivity(String friendEmail) {
        Intent intent = new Intent(this, SummarySchedule.class);
        intent.putExtra("friendEmail", friendEmail);
        startActivity(intent);
    }


    private void searchFriendsByEmail(String searchEmail) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference friendsCollection = db.collection("friends");

        friendsCollection.whereEqualTo("currentUserEmail", searchEmail)
                .whereEqualTo("searchEmail", auth.getCurrentUser().getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // 검색된 문서의 searchEmail 값을 리스트뷰에 추가
                                friendEmailList.add(searchEmail);
                            }

                            // 검색 결과를 리스트뷰에 표시
                            displayFriendsList();
                        } else {
                            Log.e("Firestore", "Friends 컬렉션 검색 오류: ", task.getException());
                        }
                    }
                });

    }

    private void displayFriendsList() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, friendEmailList);
        friendsListView.setAdapter(adapter);
    }
}

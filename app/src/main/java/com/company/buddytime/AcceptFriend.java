package com.company.buddytime;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AcceptFriend extends AppCompatActivity {

    ListView exampleList;
    ArrayList<String> dataSample;
    ButtonListAdapter buttonListAdapter;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private CollectionReference friendsCollection;
    private String currentUserEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accept_friend);

        // Firebase 초기화
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        friendsCollection = db.collection("friends");
        currentUserEmail = auth.getCurrentUser().getEmail();

        exampleList = findViewById(R.id.acpt_listview);
        dataSample = new ArrayList<>();
        buttonListAdapter = new ButtonListAdapter(this, dataSample, this); // 액티비티 참조 전달

        // friends 컬렉션에서 조건에 맞는 문서 가져오기
        friendsCollection.whereEqualTo("searchEmail", currentUserEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // friends 컬렉션에서 가져온 문서의 currentUserEmail 값을 ArrayList에 추가
                            String friendEmail = document.getString("currentUserEmail");
                            // 추가하기 전에 이미 친구 관계가 형성되었는지 확인
                            isFriendRelationship(currentUserEmail, friendEmail, new FriendCheckCallback() {
                                @Override
                                public void onResult(boolean isFriend) {
                                    if (!isFriend) {
                                        // 친구 요청이 왔다고 판단하고 리스트뷰에 추가
                                        dataSample.add(friendEmail);
                                        Log.d("FriendRelationship", "투르투르");

                                        // 리스트뷰 갱신
                                        runOnUiThread(() -> buttonListAdapter.notifyDataSetChanged());
                                    } else {
                                        Log.d("FriendRelationship", "일로가");
                                    }
                                }
                            });
                        }
                    } else {
                        // 가져오기 실패
                        // 실패에 대한 처리를 여기에 추가
                    }
                });

        exampleList.setAdapter(buttonListAdapter);
    }
    private void isFriendRelationship(String currentUserEmail, String friendEmail, FriendCheckCallback callback) {
        friendsCollection.whereEqualTo("currentUserEmail", currentUserEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // friends 컬렉션에서 가져온 문서의 currentUserEmail 값을 비교
                            String fe = document.getString("searchEmail");
                            if (fe.equals(friendEmail)) {
                                // 친구 관계가 존재하는 경우
                                callback.onResult(true);
                                return;
                            }
                        }
                        // 모든 문서를 확인했지만 친구 관계를 찾지 못한 경우
                        callback.onResult(false);
                    } else {
                        // 쿼리 실행 중 에러가 발생한 경우
                        callback.onResult(false);
                    }
                });
    }
    public interface FriendCheckCallback {
        void onResult(boolean isFriend);
    }





    // accBtn 클릭 시 호출되는 메서드
    public void onAcceptButtonClick(String friendEmail) {
        // friends 컬렉션에 추가
        addFriendToCollection(currentUserEmail, friendEmail, true);
    }

    // dclBtn 클릭 시 호출되는 메서드
    public void onDeclineButtonClick(String friendEmail) {
        // friends 컬렉션에서 삭제
        deleteFriendFromCollection(currentUserEmail, friendEmail);
    }

    // addFriendToCollection 메서드 정의
    private void addFriendToCollection(String currentUserEmail, String searchEmail, boolean isFriend) {
        SearchUtil.Friend friend = new SearchUtil.Friend(currentUserEmail, searchEmail, isFriend);
        friendsCollection.add(friend)
                .addOnSuccessListener(documentReference -> {
                    // 추가 성공
                    showToast("친구를 추가했습니다.");
                    // 리스트뷰에서 아이템 제거
                    dataSample.remove(searchEmail);
                    buttonListAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // 추가 실패
                    showToast("친구 추가에 실패했습니다.");
                });
    }

    // deleteFriendFromCollection 메서드 정의
    private void deleteFriendFromCollection(String currentUserEmail, String searchEmail) {
        friendsCollection.whereEqualTo("currentUserEmail", searchEmail)
                .whereEqualTo("searchEmail", currentUserEmail)
                .whereEqualTo("friend", true)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // friends 컬렉션에서 가져온 문서 삭제
                            document.getReference().delete();
                            // 리스트뷰에서도 제거
                            dataSample.remove(searchEmail);
                            buttonListAdapter.notifyDataSetChanged();
                            showToast("친구를 거절했습니다.");
                        }
                    } else {
                        // 삭제 실패
                        showToast("친구 거절에 실패했습니다.");
                    }
                });
    }

    // showToast 메서드는 기존 코드에서 가져왔습니다.
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // addFriendToCollection, deleteFriendFromCollection 메서드는 기존 코드에서 가져왔습니다.
}
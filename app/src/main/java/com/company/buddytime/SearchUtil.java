package com.company.buddytime;
import android.content.Context;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class SearchUtil {
    public SearchUtil(Context context) {
        this.context = context;
    }
    private Context context;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private String currentUserEmail = auth.getCurrentUser().getEmail();
    private CollectionReference usersCollection = db.collection("users");
    private CollectionReference friendsCollection = db.collection("friends");

    public void searchAndAddFriend(String searchEmail) {
        usersCollection.whereEqualTo("email", searchEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            // 이메일이 일치하는 사용자가 존재합니다.
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                checkAndAddFriend(currentUserEmail, searchEmail);
                            }
                        } else {
                            showToast("입력하신 사용자를 찾을 수 없습니다.");
                        }
                    } else {
                        // 쿼리 실행 중 오류가 발생했습니다.
                        // task.getException()을 통해 자세한 정보를 얻을 수 있습니다.
                    }
                });
    }

    private void checkAndAddFriend(String currentUserEmail, String searchEmail) {
        // 중복 여부 체크
        friendsCollection.whereEqualTo("currentUserEmail", currentUserEmail)
                .whereEqualTo("searchEmail", searchEmail)
                .whereEqualTo("friend", true)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            // 이미 친구 요청을 보낸 상대입니다.
                            showToast("이미 친구 요청을 보낸 상대입니다.");
                        } else {
                            // 친구 요청이 아직 없는 경우에만 추가
                            addFriendToCollection(currentUserEmail, searchEmail, true);
                            // 검색 및 친구 추가 성공
                            showToast("친구 요청을 보냈습니다.");
                        }
                    } else {
                        // 중복 여부 체크 중 오류가 발생했습니다.
                        // task.getException()을 통해 자세한 정보를 얻을 수 있습니다.
                    }
                });
    }
    private void addFriendToCollection(String currentUserEmail, String searchEmail, boolean helloFriend) {
        // friends 컬렉션에 새로운 문서 추가
        friendsCollection.add(new Friend(currentUserEmail, searchEmail, helloFriend))
                .addOnSuccessListener(documentReference -> {
                    // 추가 성공
                    // 추가된 문서에 대한 작업을 수행할 수 있습니다.
                })
                .addOnFailureListener(e -> {
                    // 추가 실패
                    // 실패한 이유에 대한 작업을 수행할 수 있습니다.
                });
    }

    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        // 토스트 메시지를 띄우는 코드
        // 이 코드는 액티비티의 컨텍스트를 사용하므로, SearchUtil이 액티비티 내부 클래스가 아닌 독립적인 클래스로 선언되어야 합니다.
    }
    // 사용자 정의 클래스
    public static class Friend {
        private String currentUserEmail;
        private String searchEmail;
        private boolean isFriend;

        // 기본 생성자 추가
        public Friend() {}

        public Friend(String currentUserEmail, String searchEmail, boolean isFriend) {
            this.currentUserEmail = currentUserEmail;
            this.searchEmail = searchEmail;
            this.isFriend = isFriend;
        }

        // getter 메서드 추가
        public String getCurrentUserEmail() {
            return currentUserEmail;
        }

        public String getSearchEmail() {
            return searchEmail;
        }

        public boolean isFriend() {
            return isFriend;
        }
    }
}

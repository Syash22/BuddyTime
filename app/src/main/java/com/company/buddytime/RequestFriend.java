package com.company.buddytime;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.company.buddytime.SearchUtil;

public class RequestFriend extends AppCompatActivity {

    private EditText emailEditText;
    private Button addBtn;
    private SearchUtil searchUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_friend); // 액티비티 레이아웃 파일 이름에 맞게 수정

        // XML 레이아웃에서 해당 뷰들을 가져옵니다.
        emailEditText = findViewById(R.id.rq_inEmail);
        addBtn = findViewById(R.id.rq_addBtn);

        // SearchUtil 인스턴스를 생성합니다.
        searchUtil = new SearchUtil(this);

        // "추가" 버튼 클릭 이벤트 처리
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // EditText에서 이메일 값을 가져옵니다.
                String searchEmail = emailEditText.getText().toString().trim();

                // 이메일이 비어있는지 확인
                if (!searchEmail.isEmpty()) {
                    // SearchUtil 클래스의 메서드를 호출하여 검색 및 친구 추가 로직을 실행
                    searchUtil.searchAndAddFriend(searchEmail);

                } else {
                    // 이메일이 비어있을 경우에 대한 처리
                    // 예: Toast 메시지를 통한 안내
                }
            }
        });
    }
}
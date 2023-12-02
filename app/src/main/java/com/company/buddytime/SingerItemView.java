package com.company.buddytime;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

public class SingerItemView extends LinearLayout {

    TextView textView;
    TextView textView2;
    ImageView imageView;
    //뷰를 생성할 때는 생성자가 두 개 이상이어야한다.
    public SingerItemView(Context context) {
        super(context);

        init(context);
    }

    public SingerItemView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    private  void init(Context context){
        //만들어놓은 singer_item xml파일을 인플레이터시켜서 객체화한다음 SingerItemView에 붙여줄 수 있겠죠
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //레이아웃 인플레이터ㅓ로 시스템서비스를 참조할 수 있음
        //시스템서비스는 화면에 보이지 않아도 돌아가고있음- 레아웃인플레이터를 이용하겠다.
        inflater.inflate(R.layout.singer_item,this,true);

        //이렇게 객체화시켜준 다음부터는 findViewById로 가져와 참조할 수 있음
        textView = (TextView)findViewById(R.id.test1);
        textView2 = (TextView)findViewById(R.id.test2);
    }

    //데이터를 설정할 수 있게 만든 함수
    public  void setName(String name){
        textView.setText(name);
        //걸그룹의 이름을 설정
    }
    public void setMobile(String mobile){
        textView2.setText(mobile);
    }

}
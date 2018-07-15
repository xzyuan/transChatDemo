package com.example.xie.myapplication.tool;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyTitleBar extends LinearLayout {

    private TextView tv_title;
    public MyTitleBar(Context context) {
        super(context,null);
    }

    public MyTitleBar(final Context context, AttributeSet attrs) {
        super(context, attrs);

//        //引入布局
//        LayoutInflater.from(context).inflate(R.layout.title_bar,this);
//        Button btnBack=(Button)findViewById(R.id.btnBack);
//        Button btnEdit=(Button)findViewById(R.id.btnEdit);
//        btnBack.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ((Activity)getContext()).finish();
//            }
//        });
//
//        btnEdit.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(context,"编辑",Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        tv_title=(TextView)findViewById(R.id.tv_title);
//
//    }
//
//    //显示活的的标题
//    public void setTitle(String title)
//    {
//        if(!TextUtils.isEmpty(title))
//        {
//            tv_title.setText(title);
//        }
    }
}

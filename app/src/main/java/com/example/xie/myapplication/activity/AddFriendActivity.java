package com.example.xie.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.xie.myapplication.R;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

public class AddFriendActivity extends AppCompatActivity {
    private EditText addFriendName;
    private Button addFriendBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfriend);
        addFriendBtn = (Button) findViewById(R.id.addFriendBtn);
        addFriendName = (EditText) findViewById(R.id.addFriendName);
        addFriendBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                try {
                    EMClient.getInstance().contactManager().addContact(addFriendName.getText().toString(), "");
                    Looper.prepare();
                    Toast.makeText(AddFriendActivity.this,"已发送好友请求",Toast.LENGTH_SHORT).show();
                    Looper.loop();
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    startActivity(new Intent(AddFriendActivity.this,FriendActivity.class));
                    Looper.prepare();
                    Toast.makeText(AddFriendActivity.this,"发送好友请求失败！",Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        });


    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        startActivity(new Intent(AddFriendActivity.this,FriendActivity.class));
    }


}

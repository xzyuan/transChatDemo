package com.example.xie.myapplication.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.xie.myapplication.R;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import android.content.Intent;

public class LoginActivity extends AppCompatActivity {

    private EditText userName;
    private EditText passWord;
    private Button loginBtn;
    private Button toSignUpBtn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.acticity_login);
        userName = (EditText) findViewById(R.id.userName);
        passWord = (EditText) findViewById(R.id.passWord);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        toSignUpBtn = (Button) findViewById(R.id.toSignUpBtn);

        loginBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(!userName.getText().toString().equalsIgnoreCase("")){
                login();
                }
            }
        });

        toSignUpBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });

        if(EMClient.getInstance().isLoggedInBefore()){
            EMClient.getInstance().groupManager().loadAllGroups();
            EMClient.getInstance().chatManager().loadAllConversations();
            startActivity(new Intent(LoginActivity.this,FriendActivity.class));
        };
    }

    private  void login(){
        EMClient.getInstance().login(userName.getText().toString().trim(),passWord.getText().toString().trim(),new EMCallBack() {//回调

            @Override
            public void onSuccess() {
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
                Log.d("main", "登录聊天服务器成功！");
                Intent intent =new Intent(LoginActivity.this,FriendActivity.class);
                startActivity(intent);

                Looper.prepare();
                Toast.makeText(getApplicationContext(), "登陆成功", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onProgress(int progress, String status) {
                Looper.prepare();
                Toast.makeText(getApplicationContext(), "正在登陆...", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onError(int code, String message) {
                Log.d("main", "登录聊天服务器失败！");
//                mTextView.setText("登录聊天服务器失败！");
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                Looper.prepare();
                Toast.makeText(getApplicationContext(), "登陆失败", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        });
    }
}

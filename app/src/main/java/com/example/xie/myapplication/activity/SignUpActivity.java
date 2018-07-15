package com.example.xie.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.xie.myapplication.R;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

public class SignUpActivity extends AppCompatActivity {

    private EditText signUpUserName;
    private EditText signUpPassWord;
    private Button signUpBtn;
    private Button getBackBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        signUpUserName = (EditText) findViewById(R.id.signUpUserName);
        signUpPassWord = (EditText) findViewById(R.id.signUpPassWord);
        signUpBtn = (Button) findViewById(R.id.signUpBtn);
        getBackBtn = (Button) findViewById(R.id.getBack);

        signUpBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            EMClient.getInstance().createAccount(signUpUserName.getText().toString().trim(), signUpPassWord.getText().toString().trim());
                            Toast.makeText(SignUpActivity.this,"注册成功",Toast.LENGTH_SHORT);
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        getBackBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                signUpUserName.setText("");
                signUpPassWord.setText("");
                Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
                startActivity(intent);

            }
        });
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
    }
}

package com.example.xie.myapplication.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.example.xie.myapplication.R;
import com.example.xie.myapplication.adapter.FriendAdapter;
import com.example.xie.myapplication.adapter.RecyclerItemClickListener;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMContactListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.List;

public class FriendActivity extends AppCompatActivity {

    private List<String> friendsList = new ArrayList<String>();
    private RecyclerView myRecyclerView;
    private RecyclerView.LayoutManager myLayoutManager;
    private FriendAdapter myAdapter;
    private Button addFriendBtn;
    private Button deleteFriendBtn;
    private Button logOutBtn;
    private Handler friendHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            myAdapter.refresh(friendsList);
            myAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null)
        {
            actionBar.hide();
        }
        addFriendBtn = (Button) findViewById(R.id.toAddContacterBtn);
        deleteFriendBtn = (Button) findViewById(R.id.toDeleteContacterBtn);
        logOutBtn = (Button) findViewById(R.id.logOutBtn);
        myRecyclerView = (RecyclerView) findViewById(R.id.friendsRecyclerView);
//        myRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        LinearLayoutManager manager = new LinearLayoutManager(this);
//        manager.setStackFromEnd(true);
        myRecyclerView.setLayoutManager(manager);
        myRecyclerView.setItemAnimator(new DefaultItemAnimator());
        myAdapter = new FriendAdapter(friendsList);
        myRecyclerView.setAdapter(myAdapter);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    friendsList = EMClient.getInstance().contactManager().getAllContactsFromServer();
                    friendHandler.sendMessage(friendHandler.obtainMessage(0, ""));
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        myRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // do whatever
                        Intent intent = new Intent(FriendActivity.this, ChatActivity.class);
                        intent.putExtra("username", friendsList.get(position));
                        startActivity(intent);
                    }
                })
        );
//        Intent intent = getIntent();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    friendsList = EMClient.getInstance().contactManager().getAllContactsFromServer();
                    friendHandler.sendMessage(friendHandler.obtainMessage(0, ""));
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }).start();


        addFriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FriendActivity.this, AddFriendActivity.class));
                finish();
            }
        });

        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EMClient.getInstance().logout(true, new EMCallBack() {

                    @Override
                    public void onSuccess() {
                        // TODO Auto-generated method stub
                        startActivity(new Intent(FriendActivity.this, LoginActivity.class));
                    }

                    @Override
                    public void onProgress(int progress, String status) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onError(int code, String message) {
                        // TODO Auto-generated method stub
                        Looper.prepare();
                        Toast.makeText(getApplicationContext(), "登出失败", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                });
            }
        });

        deleteFriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        EMClient.getInstance().contactManager().setContactListener(new EMContactListener() {

            @Override
            public void onFriendRequestAccepted(String username) {
                //好友请求被同意
            }

            @Override
            public void onFriendRequestDeclined(String username) {
                //好友请求被拒绝
            }

            @Override
            public void onContactInvited(String username, String reason) {
                showNormalDialog(username);
            }

            @Override
            public void onContactDeleted(String username) {
                //被删除时回调此方法
            }


            @Override
            public void onContactAdded(String username) {
                //增加了联系人时回调此方法
            }
        });

    }



    private List<String> listFriends(){
        List<String> a;
        a = getContacter();
        return a;
    }

    private List<String> getContacter() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().contactManager().getAllContactsFromServer();
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return null;
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }


    private void showNormalDialog(String username){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(FriendActivity.this);
        normalDialog.setIcon(R.drawable.addfriend);
        normalDialog.setTitle("好友请求");
        normalDialog.setMessage("来自   "+username);
        normalDialog.setPositiveButton("接受",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        normalDialog.setNegativeButton("拒绝",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        // 显示
        normalDialog.show();
    }
}

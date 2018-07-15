package com.example.xie.myapplication.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.xie.myapplication.R;
import com.example.xie.myapplication.adapter.MyAdapter;
import com.example.xie.myapplication.tool.Msg;
import com.example.xie.myapplication.tool.Youdao;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private GestureDetector mGestureDetector;
    private String partner = null;
    private RecyclerView myRecyclerView;
    private MyAdapter myAdapter;
    private EditText myEditText;
    private Button myButton;
    //    private RecyclerView.LayoutManager myLayoutManager;
    private List<EMMessage> myDataset;

    private  Handler allHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            myAdapter.notifyDataSetChanged();
            myRecyclerView.scrollToPosition(myDataset.size() - 1);
        }
    };

    private Handler mSendHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            myAdapter.notifyDataSetChanged();
            myRecyclerView.scrollToPosition(myDataset.size() - 1);
        }
    };

    public interface OnItemLongClickListener {
        public void onItemLongClick(int position, View childView);
    }

    //单击事件接口
    public interface OnItemClickListener {
        public void onItemClick(int position, View childView);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDataset = new ArrayList<>();
        final SQLiteDatabase db = openOrCreateDatabase("test.db", Context.MODE_PRIVATE, null);
        setContentView(R.layout.activity_chat);
        final Intent intent=getIntent();
        final String id=intent.getStringExtra("username");
        setTitle(id);
        partner = id;
        final String tablename = EMClient.getInstance().getCurrentUser()+"_"+id;
        db.execSQL("CREATE TABLE IF NOT EXISTS "+ EMClient.getInstance().getCurrentUser()+"_"+id+"(id integer PRIMARY key AUTOINCREMENT,name varchar(20),message varchar(255),type int);");
        myButton = (Button) findViewById(R.id.sendMsgBtn);
        myEditText = (EditText) findViewById(R.id.inputMsg);
        myRecyclerView = (RecyclerView) findViewById(R.id.chatRecyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(this);
//        manager.setStackFromEnd(true);
        myRecyclerView.setLayoutManager(manager);
        myRecyclerView.setItemAnimator(new DefaultItemAnimator());
        myAdapter = new MyAdapter(myDataset);
        myRecyclerView.setAdapter(myAdapter);

         final OnItemLongClickListener mOnItemLongClickListener = new OnItemLongClickListener() {
            @Override
            public void onItemLongClick(final int position, View childView) {
                final int p = position;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
//                    new Youdao().translateFrom("come with me");
                            Looper.prepare();
                            showNormalDialog(new Youdao().translateTo(getMessage(myDataset.get(p))));
                            Looper.loop();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        };

         final OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
            @Override
            public void onItemClick(int position, View childView) {
                Toast.makeText(getApplication(), "单击:" + position, Toast.LENGTH_SHORT).show();
            }
         };

        myRecyclerView.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                if (mGestureDetector.onTouchEvent(e)) {
                    return true;
                }
                return false;
            }
        });

        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {

            //长按事件
            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                if (mOnItemLongClickListener != null) {
                    View childView = myRecyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (childView != null) {
                        int position = myRecyclerView.getChildLayoutPosition(childView);
                        mOnItemLongClickListener.onItemLongClick(position, childView);
                    }
                }
            }
            public boolean onSingleTapUp(MotionEvent e) {
                if (mOnItemClickListener != null) {
                    View childView = myRecyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (childView != null) {
                        int position = myRecyclerView.getChildLayoutPosition(childView);
                        mOnItemClickListener.onItemClick(position, childView);
                        return true;
                    }
                }

                return super.onSingleTapUp(e);
            }

        });

        EMClient.getInstance().chatManager().addMessageListener(new EMMessageListener() {

            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                //收到消息
                myDataset.addAll(messages);
                allHandler.sendMessage(allHandler.obtainMessage(0));
                EMClient.getInstance().chatManager().importMessages(messages);
//                String tablename;
//                for (EMMessage message : messages){
//                    tablename = EMClient.getInstance().getCurrentUser()+"_"+message.getFrom();
//                    db.execSQL("CREATE TABLE IF NOT EXISTS "+ tablename+"(id integer PRIMARY key AUTOINCREMENT,name varchar(20),message varchar(255),type int);");
//                    db.execSQL("INSERT INTO "+ tablename + " (name,message,type) VALUES(\'" +
//                            EMClient.getInstance().getCurrentUser()+"\',\'"+getMessage(message)+"\',1);");
//                }
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
                //收到透传消息
            }

            @Override
            public void onMessageRead(List<EMMessage> messages) {
                //收到已读回执
            }

            @Override
            public void onMessageDelivered(List<EMMessage> message) {
                //收到已送达回执
            }

            @Override
            public void onMessageRecalled(List<EMMessage> messages) {
                //消息被撤回
            }

            @Override
            public void onMessageChanged(EMMessage message, Object change) {
                //消息状态变动
            }
        });

//        记得在不需要的时候移除listener，如在activity的onDestroy()时
//        EMClient.getInstance().chatManager().removeMessageListener(msgListener);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//                    EMConversation conversation = EMClient.getInstance().chatManager().getConversation(partner);
////获取此会话的所有消息
//                    List<EMMessage> messages = conversation.getAllMessages();
//                    myDataset.addAll(messages);
//
//                    allHandler.sendMessage(allHandler.obtainMessage(0));
                    Cursor cursor = db.rawQuery("select * from " +tablename+";", null);
                    while (cursor.moveToNext()) {
                        EMMessage message = EMMessage.createTxtSendMessage(cursor.getString(2),partner);
                        message.setFrom(cursor.getInt(3)==0?EMClient.getInstance().getCurrentUser():partner);
                        myDataset.add(message);
                    }
                    cursor.close();
                    mSendHander.sendMessage(mSendHander.obtainMessage(0));
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(), "获取消息列表成功", Toast.LENGTH_SHORT).show();
                    Looper.loop();
//SDK初始化加载的聊天记录为20条，到顶时需要去DB里获取更多
//获取startMsgId之前的pagesize条消息，此方法获取的messages SDK会自动存入到此会话中，APP中无需再次把获取到的messages添加到会话中
//        List<EMMessage> messages = conversation.loadMoreMsgFromDB(startMsgId, pagesize);
                } catch (Exception e) {
                    e.printStackTrace();
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(), "获取消息列表失败", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }).start();

        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//如果是群聊，设置chattype，默认是单聊
//                if (EMMessage.chatType == CHATTYPE_GROUP)
//                    message.setChatType(EMMessage.ChatType.GroupChat);
                final String a = myEditText.getText().toString();
                myEditText.setText("");
                //关闭键盘
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<EMMessage> messages = new ArrayList<EMMessage>();
                        Youdao youdao = new Youdao();
                        EMMessage message = null;
                        try {
                            message = EMMessage.createTxtSendMessage(youdao.translateFrom(a), partner);
                            EMClient.getInstance().chatManager().sendMessage(message);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Looper.prepare();
                            Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                        messages.add(message);
                        myDataset.addAll(messages);
                        mSendHander.sendMessage(mSendHander.obtainMessage(0, myEditText.getText().toString()));
                        EMClient.getInstance().chatManager().importMessages(messages);
                        db.execSQL("INSERT INTO "+ EMClient.getInstance().getCurrentUser()+"_"+id + " (name,message,type) VALUES(\'" +
                                EMClient.getInstance().getCurrentUser()+"\',\'"+getMessage(message)+"\',0);");
                    }
                }).start();
            }
        });
    }

    private String getMessage(EMMessage message) {
        return message.getBody().toString().split("\"")[1];
    }



    private void showNormalDialog(String s){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(ChatActivity.this);
        normalDialog.setIcon(R.drawable.translate);
        normalDialog.setTitle("翻译");
        normalDialog.setMessage(s);
        normalDialog.setPositiveButton("确定",
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






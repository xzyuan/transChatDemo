package com.example.xie.myapplication.activity;

import android.app.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.view.WindowManager;
import android.widget.Toast;
import com.example.xie.myapplication.R;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.*;
import com.hyphenate.util.NetUtils;

import java.util.Iterator;
import java.util.List;

public class DemoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        EMOptions options = new EMOptions();
// 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
// 是否自动将消息附件上传到环信服务器，默认为True是使用环信服务器上传下载，如果设为 false，需要开发者自己处理附件消息的上传和下载
        options.setAutoTransferMessageAttachments(true);
// 是否自动下载附件类消息的缩略图等，默认为 true 这里和上边这个参数相关联
        options.setAutoDownloadThumbnail(true);
//自动登陆
        options.setAutoLogin(true);

        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
// 如果APP启用了远程的service，此application:onCreate会被调用2次
// 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
// 默认的APP会在以包名为默认的process name下运行，如果查到的process name不是APP的process name就立即返回

        if (processAppName == null || !processAppName.equalsIgnoreCase(this.getPackageName())) {
//            Log.e("xzyuan", "enter the service process!");
            Toast.makeText(this, "enter the service process", Toast.LENGTH_SHORT).show();
            // 则此application::onCreate 是被service 调用的，直接返回
            return;
        }

//初始化
        EMClient.getInstance().init(this, options);

//在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        EMClient.getInstance().setDebugMode(true);
        //防止掉线
        EMClient.getInstance().addConnectionListener(new MyConnectionListener());

        //监听消息
        EMClient.getInstance().chatManager().addMessageListener(new EMMessageListener() {

            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                NotificationCompat.Builder mBuilder =
                        (NotificationCompat.Builder) new NotificationCompat.Builder(DemoApplication.this)
                                .setDefaults(Notification.DEFAULT_ALL)
                                .setSmallIcon(R.drawable.notifycation)
                                .setContentTitle("收到消息")
                                .setAutoCancel(true)
                                .setContentText("来自  " + messages.get(0).getFrom() );
                Intent resultIntent = new Intent(DemoApplication.this, FriendActivity.class);
                PendingIntent resultPendingIntent = PendingIntent.getActivity(
                        DemoApplication.this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(resultPendingIntent);
                NotificationManager mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
                mNotificationManager.notify(1, mBuilder.build());
                SQLiteDatabase db = openOrCreateDatabase("test.db", Context.MODE_PRIVATE, null);
                String tablename;
                for (EMMessage message : messages) {
                    tablename = EMClient.getInstance().getCurrentUser() + "_" + message.getFrom();
                    db.execSQL("CREATE TABLE IF NOT EXISTS " + tablename + "(id integer PRIMARY key AUTOINCREMENT,name varchar(20),message varchar(255),type int);");
                    db.execSQL("INSERT INTO " + tablename + " (name,message,type) VALUES(\'" +
                            EMClient.getInstance().getCurrentUser() + "\',\'" + getMessage(message) + "\',1);");
                }
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
                //消息状态变动c
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

//        EMClient.getInstance().contactManager().setContactListener(new EMContactListener() {
//
//            @Override
//            public void onContactAgreed(String username) {
//                //好友请求被同意
//            }
//
//            @Override
//            public void onContactRefused(String username) {
//                //好友请求被拒绝
//            }
//
//            @Override
//            public void onContactInvited(String username, String reason) {
////                showNormalDialog();
//            }
//
//            @Override
//            public void onContactDeleted(String username) {
//                //被删除时回调此方法
//            }
//
//
//            @Override
//            public void onContactAdded(String username) {
//                //增加了联系人时回调此方法
//            }
//        });
//    }

    private String getMessage(EMMessage message) {
        return message.getBody().toString().split("\"")[1];
    }

    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = this.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }

    private class MyConnectionListener implements EMConnectionListener {
        @Override
        public void onConnected() {
        }
        @Override
        public void onDisconnected(final int error) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    if(error == EMError.USER_REMOVED){
                        // 显示帐号已经被移除
                    }else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                        // 显示帐号在其他设备登录
                    } else {
                        if (NetUtils.hasNetwork(DemoApplication.this)){}
                        //连接不到聊天服务器
                        else{}
                        //当前网络不可用，请检查网络设置
                    }
                }
            }).start();
        }
    }

    private void showNormalDialog(String username){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(DemoApplication.this);
//        normalDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        normalDialog.setIcon(R.drawable.addfriend);
        normalDialog.setTitle("好友请求");
        normalDialog.setMessage("来自  "+username);
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

package cn.edu.dlut.servicedemo;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by asus on 2015/8/2.
 */
public class MyService extends Service {
    private ServiceHandler mHandler;
    //内部类
    private final  class ServiceHandler extends Handler{

        public ServiceHandler(Looper looper){
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0x1234){
                Log.v("MyService","handleMessage()被调用");

                int charNum = ((String)(msg.obj)).length();
                Bundle bundle = msg.getData();
                PendingIntent client = bundle.getParcelable("receiver");

                Log.v("MyService","字符数为" + charNum);
                Intent intent = new Intent();
                intent.putExtra("CHARNUM",charNum);
                try {
                    client.send(getApplicationContext(),0,intent);
                    Log.v("MyService","广播发送完成");
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new ServiceHandler(this.getMainLooper());
        Log.v("MyService","onCreate()被调用");
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, final int startId) {
        Log.v("MyService","onStartCommand()被调用");
        final String urlString = intent.getStringExtra("URL");
        new Thread(){
            @Override
            public void run() {
                try {
                    String result = null;
                    BufferedReader in = null ;

                    URL url = new URL(urlString);
                    URLConnection conn = url.openConnection();

                    conn.connect();//建立连接

                    in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line = null;
                    while ((line = in.readLine()) != null ){
                        result += "\n" + line;
                    }

                    Log.v("URL请求结果",result);
                    //发送消息
                    Message msg = new Message();
                    msg.what = 0x1234;
                    msg.arg1 = startId;
                    msg.obj = result;
                    msg.setData(intent.getExtras());
                    mHandler.sendMessage(msg);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.v("MyService","onBind()被调用");
        return null;
    }

}


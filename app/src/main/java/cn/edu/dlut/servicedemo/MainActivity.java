package cn.edu.dlut.servicedemo;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,MyBroadcastReceiver.onServiceResultReturnListener{

    Button btnStartService,btnBindService,btnStartServiceForResult;

    ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    MyBroadcastReceiver receiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStartService = (Button)findViewById(R.id.id_btn_startService);
        btnBindService = (Button)findViewById(R.id.id_btn_bindService);
        btnStartServiceForResult = (Button)findViewById(R.id.id_btn_startServiceForResult);

        btnStartService.setOnClickListener(this);
        btnBindService.setOnClickListener(this);
        btnStartServiceForResult.setOnClickListener(this);
        //初始化广播接受者
        initBroadcastReceiver();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.id_btn_startService:
                Intent sIntent = new Intent(MainActivity.this,MyService.class);
                Log.v("MainActivity","startService()将被调用");
                startService(sIntent);
                break;
            case R.id.id_btn_bindService:
                Intent bIntent = new Intent(MainActivity.this,MyService.class);
                Log.v("MainActivity","bindService()将被调用");
                Log.v("MainActivity",bindService(bIntent,mServiceConnection,0) == true ? "绑定成功" : "绑定失败");
                break;
            case R.id.id_btn_startServiceForResult:
                Intent sfIntent = new Intent(this,MyService.class);

                Intent broadIntent = new Intent();
                broadIntent.setAction("cn.edu.dlut.receiver");

                PendingIntent pIntent = PendingIntent.getBroadcast(this,0,broadIntent,0);
                Bundle bundle = new Bundle();
                bundle.putParcelable("receiver",pIntent);

                sfIntent.putExtras(bundle);
                sfIntent.putExtra("URL","http://www.baidu.com");
                startService(sfIntent);
                Log.v("MainActivity","startService()方法被调用");
                break;
        }
    }
    //初始化广播接收者
    private void initBroadcastReceiver(){
        receiver = new MyBroadcastReceiver(this);
        this.registerReceiver(receiver,new IntentFilter("cn.edu.dlut.receiver"));
        Log.v("MainActivity","广播接受者动态注册初始化完成");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(receiver);
    }

    @Override
    public void onServiceResultReturn(Intent intent) {
        int charNum = intent.getIntExtra("CHARNUM",0);
        Toast.makeText(this,"字符数为" + charNum,Toast.LENGTH_SHORT).show();
    }
}

//自定义广播接受者
class MyBroadcastReceiver extends BroadcastReceiver{
    //回调接口
    public interface onServiceResultReturnListener{
         void onServiceResultReturn(Intent intent);
    }

    private onServiceResultReturnListener client;

    public MyBroadcastReceiver(onServiceResultReturnListener client){
        this.client = client;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("MyBroadcaseReceiver","onReceive()方法被调用");
        this.client.onServiceResultReturn(intent);
    }
}

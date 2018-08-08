package cn.ncgds.spa;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private static long deltaTime = 0L;
    private static long totalTime = 0L;
    private boolean openScreenOnCal = true;
    private long startTime;
    private Integer minuter = 30;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x001:
                    if(openScreenOnCal){
                        totalTime += minuter;
                        Toast.makeText(MainActivity.this, "您已经使用手机"+totalTime+"分钟了，请注意休息", Toast.LENGTH_LONG).show();
                        startTime = System.currentTimeMillis();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //注册亮屏广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(receiver, filter);
        startTime = System.currentTimeMillis();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        Thread.sleep(1000);
                        long endTime = System.currentTimeMillis();
                        if((endTime - startTime) >= 1000 * 60 * minuter){
                            handler.sendEmptyMessage(0x001);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    //亮屏监听
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
                Log.e(TAG, "亮屏啦，，");
            }
            if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                Log.e(TAG, "灭屏啦，，");
            }
            if (Intent.ACTION_USER_PRESENT.equals(intent.getAction())) {
                Log.e(TAG, "解锁了，，");
                startTime = System.currentTimeMillis();
                totalTime = 0;
            }
        }
    };

    public void btnClick(View view) {
        switch (view.getId()){
            case R.id.open_screen_on_cal:
                openScreenOnCal = true;
                break;
            case R.id.close_screen_on_cal:
                openScreenOnCal = false;
                break;
            case R.id.set_cal_time:
                EditText et = (EditText)findViewById(R.id.editText3);
                String strTime = et.getText().toString();
                 minuter = Integer.valueOf(strTime);
                Toast.makeText(this, "设置提醒间隔时间成功", Toast.LENGTH_SHORT).show();
                startTime = System.currentTimeMillis();
                openScreenOnCal = true;
                break;
        }
    }



    }

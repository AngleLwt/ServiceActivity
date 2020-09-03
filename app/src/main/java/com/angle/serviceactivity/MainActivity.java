package com.angle.serviceactivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Example of binding and unbinding to the local service.
 * bind to, receiving an object through which it can communicate with the service.
 * <p>
 * Note that this is implemented as an inner class only keep the sample
 * all together; typically this code would appear in some separate class.
 */
public class MainActivity extends AppCompatActivity {
    public static final String MAIN_ACTIVITY_SERVICE_INTENT = "com.angle.serviceactivity.AppCompatActivity";

    // Don't attempt to unbind from the service unless the client has received some
    // information about the service's state.
    //除非客户端收到绑定，否则不要尝试解除绑定
    //服务状态的信息。
    private boolean mShouldUnbind;
    // To invoke the bound service, first make sure that this value
    // is not null.
    //要调用绑定服务，首先要确保此值
    //不为空。
    private LocationService mBoundService;
    private Button view;

    private Button views;
    private Intent intentService;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.

            //当与服务的连接完成时调用
            //建立，为我们提供可以使用的服务对象
            //与服务交互。因为我们绑定到一个显式
            //我们知道在自己的进程中运行的服务，我们可以
            //将其IBinder转换为具体类并直接访问它。

//            绑定服务得到LocalBinder对象
            LocationService.LocalBinder service1 = (LocationService.LocalBinder) service;
//            通过接口回掉传值
            service1.receive("message for activity");
//            调用LocalBinder得到LocationService对象
            mBoundService = service1.getLocationService();

            // Tell the user about this for our demo.
            Toast.makeText(MainActivity.this, R.string.local_service_connected,
                    Toast.LENGTH_SHORT).show();
        }

        // 服务意外失去连接
        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            //当与服务的连接完成时调用
            //意外断开连接——即进程崩溃。
            mBoundService = null;
            Toast.makeText(MainActivity.this, R.string.local_service_disconnected,
                    Toast.LENGTH_SHORT).show();
        }
    };

    void doBindService() {
        // Attempts to establish a connection with the service.  We use an
        // explicit class name because we want a specific service
        // implementation that we know will be running in our own process
        // (and thus won't be supporting component replacement by other
        // applications).

        //试图与服务建立连接。我们使用一个
        //显式类名，因为我们需要特定的服务
        //我们知道将在自己的进程中运行的实现
        //(因此不支持其他组件的替换
        //应用程序

//        创建Service连接
        intentService = new Intent(MainActivity.this, LocationService.class);
//         发送值将会在LocationService中onStartCommand得到
        intentService.putExtra(MAIN_ACTIVITY_SERVICE_INTENT, "message for activity");
        startService(intentService);
//        绑定Service
        if (bindService(intentService,
                mConnection, Context.BIND_AUTO_CREATE)) {
//            如果绑定确认解绑
            mShouldUnbind = true;
        } else {
            Log.e("MY_APP_TAG", "Error: The requested service doesn't " +
                    "exist, or this client isn't allowed access to it.");
        }
    }

    void doUnbindService() {
//        如果服务已经绑定
        if (mShouldUnbind) {
            // Release information about the service's state.
            //解绑服务
            unbindService(mConnection);
            mShouldUnbind = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        当解Activity退出绑服务
        doUnbindService();
    }

    private void initView() {
        views = (Button) findViewById(R.id.views);
        views.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                创建服务绑定
                doBindService();
            }
        });
    }
}

package com.angle.serviceactivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class LocationService extends Service {

    private NotificationManager mNM;

    // Unique Identification Number for the Notification.
    // We use it on Notification start, and to cancel it.
    private int NOTIFICATION = R.string.local_service_started;

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    // 这是接收来自客户端的交互的对象
    // 以获得更完整的示例。
    private final IBinder mBinder = new LocalBinder();

    //返回onBinder对象
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
   //创建需要完成的服务
    @Override
    public void onCreate() {
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Display a notification about us starting.  We put an icon in the status bar.
        showNotification();
    }


    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */

    /*
    以便客户机访问。因为我们一直都知道这项服务
    *运行在与它的客户端相同的进程中，我们不需要处理
    * IPC。
    */
    public class LocalBinder extends Binder {
        LocationService getLocationService() {
            return LocationService.this;
        }
        void receive(String msg){
            Log.d("TAG", "receive: "+msg);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        onStartCommand绑定页面进行通信
        Log.i("LocalService", "Received start id " + startId + ": " + intent.getStringExtra(MainActivity.MAIN_ACTIVITY_SERVICE_INTENT));
        return START_NOT_STICKY;
    }

    /**
     * Show a notification while this service is running.
     */
    public void showNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = getText(R.string.local_service_started);

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_android_black_24dp)  // the status icon
                .setTicker(text)  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle(getText(R.string.local_service_label))  // the label of the entry
                .setContentText(text)  // the contents of the entry
                .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .build();
        // Send the notification.
        mNM.notify(NOTIFICATION, notification);
    }
//  解除通知
    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
        mNM.cancel(NOTIFICATION);
        // Tell the user we stopped.
        Toast.makeText(this, R.string.local_service_stopped, Toast.LENGTH_SHORT).show();
    }

}

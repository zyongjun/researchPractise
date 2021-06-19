package com.example.myapplication.download;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.text.HtmlCompat;
import android.text.Spanned;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplication.R;

public class MyNotificationUtils {
    private static final String TAG = "MyNotificationUtils";
    private static NotificationManager manager;

    private static NotificationManager getManager(Context context) {
        if (manager == null) {
            manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    private static NotificationCompat.Builder getNotificationBuilder(Context mContext, String title
            , String content, String channelId) {
        //大于8.0
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        //id随便指定
        NotificationChannel channel = new NotificationChannel(channelId
                , mContext.getPackageName(), NotificationManager.IMPORTANCE_DEFAULT);
        channel.canBypassDnd();//可否绕过，请勿打扰模式
//            channel.enableLights(true);//闪光
//            channel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);//锁屏显示通知
//            channel.setLightColor(Color.RED);//指定闪光是的灯光颜色
//            channel.canShowBadge();//桌面laucher消息角标
//            channel.enableVibration(true);//是否允许震动
//            channel.setSound(null, null);
        //channel.getAudioAttributes();//获取系统通知响铃声音配置
//            channel.getGroup();//获取通知渠道组
//            channel.setBypassDnd(true);//设置可以绕过，请勿打扰模式
//            channel.setVibrationPattern(new long[]{100, 100, 200});//震动的模式，震3次，第一次100，第二次100，第三次200毫秒
//            channel.shouldShowLights();//是否会闪光
        //通知管理者创建的渠道
        getManager(mContext).createNotificationChannel(channel);
        return new NotificationCompat.Builder(mContext, channelId).setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(content).setSmallIcon(R.mipmap.ic_launcher);
    }

    /**
     * @param title
     * @param content
     * @param manageId
     * @param channelId
     * @param progress
     * @param maxProgress
     */
    public static void showNotificationProgress(Context mContext, String title
            , String content, int manageId, String channelId
            , int progress, int maxProgress) {
        final NotificationCompat.Builder builder = getNotificationBuilder(mContext, title, content, channelId);
       /* Intent intent = new Intent(this, SecondeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        builder.setContentIntent(pendingIntent);*/
        builder.setOnlyAlertOnce(true);
        builder.setDefaults(Notification.FLAG_ONLY_ALERT_ONCE);
        builder.setProgress(maxProgress, progress, false);
        builder.setWhen(System.currentTimeMillis());
        getManager(mContext).notify(manageId, builder.build());
    }

    public static void showProgressNotification(Context context, int progress) {
        String channelId = DownloadProcessor.PACKAGE_NAME_UMETRIP;
        NotificationChannel channel = new NotificationChannel(channelId
                , context.getPackageName(), NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);
        NotificationCompat.Builder builder = getNotificationBuilder(context, "正在下载", "test", channelId);
        builder.setOnlyAlertOnce(true);
        builder.setDefaults(Notification.FLAG_ONLY_ALERT_ONCE);
        builder.setProgress(100, progress, false);
        builder.setWhen(System.currentTimeMillis());
        manager.notify(DownloadProcessor.PACKAGE_NAME_UMETRIP.hashCode(), builder.build());
    }

    public static void showNotificationProgressApkDown(Context mContext
            , int progress) {
        String channelId = DownloadProcessor.PACKAGE_NAME_UMETRIP;
        NotificationChannel channel = new NotificationChannel(channelId
                , mContext.getPackageName(), NotificationManager.IMPORTANCE_DEFAULT);
        channel.canBypassDnd();//可否绕过，请勿打扰模式
        getManager(mContext).createNotificationChannel(channel);
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, channelId).setAutoCancel(true)
                .setContentTitle("正在下载")
                .setContentText("test").setSmallIcon(R.mipmap.ic_launcher);
        builder.setOnlyAlertOnce(true);
        builder.setDefaults(Notification.FLAG_ONLY_ALERT_ONCE);
        builder.setProgress(100, progress, false);
        builder.setWhen(System.currentTimeMillis());
        getManager(mContext).notify(DownloadProcessor.PACKAGE_NAME_UMETRIP.hashCode(), builder.build());
    }

    public static void cancelNotification(Context mContext, int manageId) {
        getManager(mContext).cancel(manageId);
    }

    public static void showProgressWithCancel(Context context) {
        Toast.makeText(context, "可取消进度条通知", Toast.LENGTH_SHORT).show();
        String channelId = DownloadProcessor.PACKAGE_NAME_UMETRIP;
        NotificationChannel channel = new NotificationChannel(channelId
                , context.getPackageName(), NotificationManager.IMPORTANCE_DEFAULT);
        channel.canBypassDnd();//可否绕过，请勿打扰模式
        getManager(context).createNotificationChannel(channel);
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId).setAutoCancel(true)
                .setContentTitle("正在下载")
                .setContentText("test").setSmallIcon(R.mipmap.ic_launcher);
        builder.setOnlyAlertOnce(true);
        builder.setDefaults(Notification.FLAG_ONLY_ALERT_ONCE);
        builder.setProgress(100, 0, false);
        builder.setWhen(System.currentTimeMillis());
        getManager(context).notify(DownloadProcessor.PACKAGE_NAME_UMETRIP.hashCode(), builder.build());
    }

    private static final String ACTION_RECEIVER = "com.example.myapplication.contentc.lick";
    private static BroadcastReceiver mContentBroadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive: action:" + intent.getAction());
            context.unregisterReceiver(this);
        }
    };

    public static void showRetry(Context context) {
        Toast.makeText(context, "重试通知", Toast.LENGTH_SHORT).show();
        String channelId = DownloadProcessor.PACKAGE_NAME_UMETRIP;
        NotificationChannel channel = new NotificationChannel(channelId
                , context.getPackageName(), NotificationManager.IMPORTANCE_DEFAULT);
        channel.canBypassDnd();//可否绕过，请勿打扰模式
        int color = ContextCompat.getColor(context, R.color.colorPrimaryDark);
        Spanned content = HtmlCompat.fromHtml("<font color=\"" + color + "\">" + "点击重试" + "</font>", HtmlCompat.FROM_HTML_MODE_LEGACY);

        getManager(context).createNotificationChannel(channel);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_RECEIVER);
        context.registerReceiver(mContentBroadReceiver,intentFilter);
        Intent intent = new Intent(ACTION_RECEIVER);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                1, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId).setAutoCancel(true)
                .setContentTitle("\"应用市场\"应用下载失败")
                .setContentText(content).setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent);
        builder.setOnlyAlertOnce(true);
        builder.setDefaults(Notification.FLAG_ONLY_ALERT_ONCE);
//        builder.setProgress(100, 0, false);
        builder.setWhen(System.currentTimeMillis());
        getManager(context).notify(DownloadProcessor.PACKAGE_NAME_UMETRIP.hashCode(), builder.build());
    }
}

package com.example.myapplication.download;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.myapplication.MyApplication;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;

public class DownloadProcessor implements DownloadStatusListener {
    private static final String TAG = "DownloadProcessor";
    private static final String URL_UMETRIP = "http://121.14.51.207/imtt.dd.qq.com/16891/apk/196E1F40EBC0EFCCBF689F547EC2E799.apk?mkey=60c225acda1132c3&f=255c&fsname=com.umetrip.android.msky.app_7.0.5_372.apk&hsr=4d5s&cip=218.17.20.54&proto=http";
    public static final String PACKAGE_NAME_UMETRIP = "om.umetrip.android.msky.app";
    private static final Uri BASE_URI_DOWNLOAD = Uri.parse("content://downloads/my_downloads/");
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final DownloadObserver mDownloadObserver = new DownloadObserver(mHandler);
    private List<Long> mDownloadIds = new CopyOnWriteArrayList<>();

    private DownloadProcessor() {
    }

    public static DownloadProcessor getInstance() {
        return Holder.INSTANCE;
    }

    private static final class Holder {
        private static final DownloadProcessor INSTANCE = new DownloadProcessor();
    }

    public void download(String url) {
        Log.i(TAG, "download start");
        // todo network not enable should return
        // todo has download task should remove all
        Context context = MyApplication.getContext();
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        if(!mDownloadIds.isEmpty()){
            long[] downloadIds = mDownloadIds.stream().mapToLong(value -> value).toArray();
            downloadManager.remove(downloadIds);
            mDownloadIds.clear();
        }
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(URL_UMETRIP));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        request.setVisibleInDownloadsUi(false);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, PACKAGE_NAME_UMETRIP + ".apk");
        long downloadId = downloadManager.enqueue(request);
        mDownloadIds.add(downloadId);
        Log.i(TAG, "download: id:" + downloadId);
        Uri downloadUri = Uri.withAppendedPath(BASE_URI_DOWNLOAD, String.valueOf(downloadId));
        Log.i(TAG, "download: uri:" + downloadUri.toString());
        mDownloadObserver.initDownloadInfo(context, downloadId);
        MyApplication.getContext().getContentResolver().registerContentObserver(downloadUri, false, mDownloadObserver);
    }

    @Override
    public void onDownloadSuccess(String uri) {
        Context context = MyApplication.getContext();
        removeCurrentDownload(context);
//        File apkFile = new File();
//        InstallProcessor installProcessor = new InstallProcessor();
//        installProcessor.install(apkFile);
    }

    @Override
    public void onDownloadFail(String message) {
        Context context = MyApplication.getContext();
        removeCurrentDownload(context);
    }

    @Override
    public void onDownloadRunning(int progress) {
        showProgressNotification(MyApplication.getContext(), progress);
    }

    private void removeCurrentDownload(Context context) {
        context.getContentResolver().unregisterContentObserver(mDownloadObserver);
        cancelNotification(context, PACKAGE_NAME_UMETRIP.hashCode());
        mDownloadIds.clear();
    }

    private void showProgressNotification(Context context, int progress) {
        String channelId = DownloadProcessor.PACKAGE_NAME_UMETRIP;
        NotificationChannel channel = new NotificationChannel(channelId
                , context.getPackageName(), NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId).setAutoCancel(true)
                .setContentTitle("正在下载")
                .setContentText("test").setSmallIcon(R.mipmap.ic_launcher);
        ;
        builder.setOnlyAlertOnce(true);
        builder.setDefaults(Notification.FLAG_ONLY_ALERT_ONCE);
        builder.setProgress(100, progress, false);
        builder.setWhen(System.currentTimeMillis());
        manager.notify(DownloadProcessor.PACKAGE_NAME_UMETRIP.hashCode(), builder.build());
    }

    private static void cancelNotification(Context context, int manageId) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(manageId);
    }
}

package com.example.myapplication.download;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.myapplication.MyApplication;

public class DownloadObserver extends ContentObserver {
    private static final String TAG = "DownloadObserver";
    private DownloadManager mDownloadManager;
    private DownloadManager.Query mQuery;
    private final ApkDownloadReceiver mReceiver = new ApkDownloadReceiver();

    private static final class ApkDownloadReceiver extends BroadcastReceiver {
        private static final String TAG = "ApkDownloadReceiver";
        private long mDownLoadId;

        public void setDownLoadId(long downLoadId) {
            this.mDownLoadId = downLoadId;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive: action:" + intent.getAction());
            checkStatus();
        }

        //检查下载状态
        private void checkStatus() {
            DownloadManager downloadManager = (DownloadManager) MyApplication.getContext().getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(mDownLoadId);
            Cursor cursor = downloadManager.query(query);
            if (cursor.getCount() == 0) {
                DownloadProcessor.getInstance().onDownloadFail("download error");
                return;
            }
            if (cursor.moveToFirst()) {
                int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                int bytesDownload = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                String description = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_DESCRIPTION));
                String id = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_ID));
                String localUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                String mimeType = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE));
                String title = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE));
                int totalSize = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                cursor.close();
                int progress = (int) ((bytesDownload * 100f) / totalSize);
                Log.i(TAG, "checkStatus: progress:" + progress);
                Log.i(TAG, "checkStatus: status:" + status);
                Log.i(TAG, "bytesDownload:" + bytesDownload);
                Log.i(TAG, "description:" + description);
                Log.i(TAG, "id:" + id);
                Log.i(TAG, "localUri:" + localUri);
                Log.i(TAG, "mimeType:" + mimeType);
                Log.i(TAG, "title:" + title);
                Log.i(TAG, "status:" + status);
                Log.i(TAG, "totalSize:" + totalSize);
                switch (status) {
                    //下载暂停
                    case DownloadManager.STATUS_PAUSED:
                        break;
                    //下载延迟
                    case DownloadManager.STATUS_PENDING:
                        break;
                    //正在下载
                    case DownloadManager.STATUS_RUNNING:
                        break;
                    //下载完成
                    case DownloadManager.STATUS_SUCCESSFUL:
                        DownloadProcessor.getInstance().onDownloadSuccess(localUri);
                        break;
                    //下载失败
                    case DownloadManager.STATUS_FAILED:
                        DownloadProcessor.getInstance().onDownloadFail("download fail");
                        break;
                }
            }
        }
    }

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public DownloadObserver(Handler handler) {
        super(handler);
    }

    public void initDownloadInfo(Context context, long downloadId) {
        mReceiver.setDownLoadId(downloadId);
        context.registerReceiver(mReceiver,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        mDownloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        mQuery = new DownloadManager.Query().setFilterById(downloadId);
        mQuery.setFilterById(downloadId);
    }

    public void removeDownloadInfo(Context context) {
        mReceiver.setDownLoadId(0);
        context.unregisterReceiver(mReceiver);
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
    }

    private void showProgressNotification() {
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = mDownloadManager.query(mQuery);
                cursor.moveToFirst();
                int count = cursor.getCount();
                if (count == 0) {
                    Log.i(TAG, "onChange: cursor count 0");
                    DownloadProcessor.getInstance().onDownloadFail("download fail");
                    return;
                }
                int bytes_downloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                String localUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                int progress = (int) ((bytes_downloaded * 100f) / bytes_total);
                cursor.close();
                Log.i(TAG, "onChange: bytes_downloaded:" + bytes_downloaded);
                Log.i(TAG, "onChange: bytes_total:" + bytes_total);
                Log.i(TAG, "onChange: progress:" + progress);
                Log.i(TAG, "onChange: status:" + status);
                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    DownloadProcessor.getInstance().onDownloadSuccess(localUri);
                    return;
                }
                if (status == DownloadManager.STATUS_FAILED) {
                    DownloadProcessor.getInstance().onDownloadFail("download fail");
                    return;
                }
                DownloadProcessor.getInstance().onDownloadRunning(progress);
            }
        }, 500);
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);
        Log.i(TAG, "onChange: uri:" + uri.toString());
        showProgressNotification();
    }
}

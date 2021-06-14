package com.example.myapplication.download;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.example.myapplication.MyApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static android.content.pm.PackageInstaller.SessionParams.RESTRICTED_PERMISSIONS_ALL;

public class InstallProcessor {
    private static final String TAG = "InstallProcessor";

    private void checkPermission(){
        Context context = MyApplication.getContext();
        boolean hasInstallPermission = context.getPackageManager().canRequestPackageInstalls();
        if (!hasInstallPermission) {
//            Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
//            ((Activity)context).startActivityForResult(intent,REQUEST_CODE_APP_INSTALL);
            return;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void install(File apkFile){
        String path = apkFile.getAbsolutePath();
        Log.d(TAG, "installApp()------->" + path);
        if (!apkFile.exists()) {
            Log.d(TAG, "文件不存在");
        }

        Context context = MyApplication.getContext();
        PackageInfo packageInfo = context.getPackageManager().getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES);
        if (packageInfo != null) {
            String packageName = packageInfo.packageName;
            int versionCode = packageInfo.versionCode;
            String versionName = packageInfo.versionName;
            Log.d("ApkActivity", "packageName=" + packageName + ", versionCode=" + versionCode + ", versionName=" + versionName);
        }

        PackageInstaller packageInstaller = context.getPackageManager().getPackageInstaller();
        PackageInstaller.SessionParams sessionParams
                = new PackageInstaller.SessionParams(PackageInstaller
                .SessionParams.MODE_FULL_INSTALL);
        sessionParams.setWhitelistedRestrictedPermissions(RESTRICTED_PERMISSIONS_ALL);
        Log.d(TAG, "apkFile length" + apkFile.length());
        sessionParams.setSize(apkFile.length());
        int mSessionId = -1;
        try {
            mSessionId = packageInstaller.createSession(sessionParams);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "sessionId---->" + mSessionId);
        if (mSessionId != -1) {
            boolean copySuccess = onTransfesApkFile(path,mSessionId);
            Log.d(TAG, "copySuccess---->" + copySuccess);
            if (copySuccess) {
                execInstallAPP(mSessionId);
            }
        }
    }

    /**
     * 通过文件流传输apk
     *
     * @param apkFilePath
     * @return
     */
    private boolean onTransfesApkFile(String apkFilePath,int sessionId) {
        Log.d(TAG, "---------->onTransfesApkFile()<---------------------");
        InputStream in = null;
        OutputStream out = null;
        PackageInstaller.Session session = null;
        boolean success = false;
        try {
            File apkFile = new File(apkFilePath);
            Context context = MyApplication.getContext();
            session = context.getPackageManager().getPackageInstaller().openSession(sessionId);
            out = session.openWrite("base.apk", 0, apkFile.length());
            in = new FileInputStream(apkFile);
            int total = 0, c;
            byte[] buffer = new byte[1024 * 1024];
            while ((c = in.read(buffer)) != -1) {
                total += c;
                out.write(buffer, 0, c);
            }
            session.fsync(out);
            Log.d(TAG, "streamed " + total + " bytes");
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != session) {
                session.close();
            }
            try {
                if (null != out) {
                    out.close();
                }
                if (null != in) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return success;
    }
    /**
     * 执行安装并通知安装结果
     *
     */
    private void execInstallAPP(int sessionId) {
        Log.d(TAG, "--------------------->execInstallAPP()<------------------");
        PackageInstaller.Session session = null;
        try {
            Context context = MyApplication.getContext();
            session = context.getPackageManager().getPackageInstaller().openSession(sessionId);
            Intent intent = new Intent(context, InstallReceiver.class);
            intent.putExtra("package",DownloadProcessor.PACKAGE_NAME_UMETRIP);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    1, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            session.commit(pendingIntent.getIntentSender());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != session) {
                session.close();
            }
        }
    }
}

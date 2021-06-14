package com.example.myapplication.download;

public interface DownloadStatusListener {
    void onDownloadSuccess(String uri);

    void onDownloadFail(String message);

    void onDownloadRunning(int progress);
}

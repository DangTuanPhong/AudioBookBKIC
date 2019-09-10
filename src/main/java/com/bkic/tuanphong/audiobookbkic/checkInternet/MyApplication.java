package com.bkic.tuanphong.audiobookbkic.checkInternet;

import android.app.Application;

import com.bkic.tuanphong.audiobookbkic.download.DownloadReceiver;

public class MyApplication extends Application {

    private static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
    public void setDownloadListener(DownloadReceiver.DownloadReceiverListener listener){
        DownloadReceiver.downloadReceiverListener = listener;
    }
}

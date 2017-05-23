package com.neuroandroid.pyfilebrowser.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by NeuroAndroid on 2017/5/23.
 */

public class FileBrowserService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

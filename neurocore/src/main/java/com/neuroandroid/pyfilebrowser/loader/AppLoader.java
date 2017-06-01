package com.neuroandroid.pyfilebrowser.loader;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.neuroandroid.pyfilebrowser.bean.PYFileBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by NeuroAndroid on 2017/6/1.
 */

public class AppLoader {
    public static ArrayList<PYFileBean> getAllApps(Context context) {
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        ArrayList<PYFileBean> dataList = new ArrayList<>();
        PYFileBean pyFileBean;
        for (PackageInfo info : packageInfos) {
            ApplicationInfo appInfo = info.applicationInfo;
            int flags = appInfo.flags;
            if ((flags & ApplicationInfo.FLAG_SYSTEM) > 0) {
                // 系统程序
            } else {
                // 用户程序 == 0
                pyFileBean = new PYFileBean();
                pyFileBean.setDate(info.lastUpdateTime);
                pyFileBean.setPackName(info.packageName);
                pyFileBean.setVersion(info.versionName);
                pyFileBean.setPath(appInfo.publicSourceDir);
                pyFileBean.setSize(new File(pyFileBean.getPath()).length());
                pyFileBean.setTitle(appInfo.loadLabel(packageManager).toString());
                pyFileBean.setAppIcon(appInfo.loadIcon(packageManager));
                pyFileBean.setAppIntent(packageManager.getLaunchIntentForPackage(info.packageName));
                dataList.add(pyFileBean);
            }
        }
        return dataList;
    }
}

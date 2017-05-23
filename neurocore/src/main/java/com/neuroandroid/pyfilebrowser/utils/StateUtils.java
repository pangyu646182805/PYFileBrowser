package com.neuroandroid.pyfilebrowser.utils;

import com.google.gson.Gson;
import com.neuroandroid.pyfilebrowser.config.Constant;
import com.neuroandroid.pyfilebrowser.model.response.User;

/**
 * Created by NeuroAndroid on 2017/2/8.
 */

public class StateUtils {
    public static User getUser() {
        String str = SPUtils.getString(UIUtils.getContext(), Constant.USER, null);
        if (UIUtils.isEmpty(str)) {
            return null;
        } else {
            return new Gson().fromJson(str, User.class);
        }
    }

    public static void saveUserInfo(String userInfo) {
        SPUtils.putString(UIUtils.getContext(), Constant.USER, userInfo);
    }

    public static void deleteUserInfo() {
        SPUtils.putString(UIUtils.getContext(), Constant.USER, "");
    }
}

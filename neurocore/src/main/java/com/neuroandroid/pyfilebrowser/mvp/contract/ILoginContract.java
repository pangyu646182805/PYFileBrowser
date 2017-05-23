package com.neuroandroid.pyfilebrowser.mvp.contract;

import com.neuroandroid.pyfilebrowser.base.BaseResponse;
import com.neuroandroid.pyfilebrowser.base.IBasePresenter;
import com.neuroandroid.pyfilebrowser.base.IBaseView;
import com.neuroandroid.pyfilebrowser.model.response.User;

/**
 * Created by NeuroAndroid on 2017/3/15.
 */

public interface ILoginContract {
    interface Presenter extends IBasePresenter {
        /**
         * 登录
         */
        void login(String param, String password, int userType, String ip);
    }

    interface View extends IBaseView<Presenter> {
        /**
         * 获取登录信息
         * @param user
         */
        void showLoginMsg(BaseResponse<User> user);
    }
}

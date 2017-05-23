package com.neuroandroid.pyfilebrowser.mvp.model;


import com.neuroandroid.pyfilebrowser.base.BaseResponse;
import com.neuroandroid.pyfilebrowser.model.response.User;

import retrofit2.Call;

/**
 * Created by NeuroAndroid on 2017/3/15.
 */

public interface ILoginModel {
    Call<BaseResponse<User>> login(String param, String password, int userType, String Ip);
}

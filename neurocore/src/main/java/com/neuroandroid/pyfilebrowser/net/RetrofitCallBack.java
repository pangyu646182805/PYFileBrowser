package com.neuroandroid.pyfilebrowser.net;

import com.neuroandroid.pyfilebrowser.base.BaseResponse;
import com.neuroandroid.pyfilebrowser.utils.ShowUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by NeuroAndroid on 2017/3/14.
 */

public abstract class RetrofitCallBack<T extends BaseResponse> implements Callback<T> {
    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (!call.isCanceled()) {  // 如果retrofit请求没有被取消
            if (response.raw().code() == 200) {
                T body = response.body();
                if (body != null) {
                    onSuccess(response);
                } else {
                    onFail("response body is null");
                }
            } else {
                onFailure(call, new Exception("response error, detail = " + response.raw().toString()));
            }
        } else {
            onFail("retrofit request is be canceled");
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        t.printStackTrace();
        if (!call.isCanceled()) {
            onFail(t.getMessage());
        } else {
            ShowUtils.showToast("网络请求被取消掉了");
        }
    }

    public abstract void onSuccess(Response<T> response);

    public abstract void onFail(String msg);
}

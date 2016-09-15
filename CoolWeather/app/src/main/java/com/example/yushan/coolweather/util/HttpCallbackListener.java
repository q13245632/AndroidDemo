package com.example.yushan.coolweather.util;

/**
 * Created by yushan on 2016/9/15.
 */
public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}

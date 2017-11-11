package com.hwpush;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.huawei.hms.api.ConnectionResult;
import com.huawei.hms.api.HuaweiApiClient;
import com.huawei.hms.support.api.client.PendingResult;
import com.huawei.hms.support.api.push.TokenResult;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * Created by Kun on 2017/5/24.
 */

public class HuaweiPush extends ReactContextBaseJavaModule implements HuaweiApiClient.ConnectionCallbacks,HuaweiApiClient.OnConnectionFailedListener {
    public static ReactApplicationContext mReactApplicationContext;
    public static HuaweiPush mModule;
    HuaweiApiClient client;

    public HuaweiPush(ReactApplicationContext reactContext) {
        super(reactContext);
        mReactApplicationContext = reactContext;
        mModule = this;
    }

    @Override
    public String getName() {
        return "HuaweiPush";
    }

    private static final String FILE_NAME = "HWPush";

    @ReactMethod
    public void init(Callback callback) {
        boolean ishuawei;
        Activity currentActivity = getCurrentActivity();
        if (currentActivity == null) {
            callback.invoke(false);
            return;
        }
        SharedPreferences sp = currentActivity.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);

        if (!sp.contains("ishuawei")) {
            ishuawei =  getEmuiLeval();
            SharedPreferences.Editor edit = sp.edit();
            edit.putBoolean("ishuawei", ishuawei);
            edit.commit();
        }
        ishuawei = sp.getBoolean("ishuawei", false);
        if (!ishuawei) {
            callback.invoke(false);
            return;
        }
        client = new HuaweiApiClient.Builder(currentActivity)
                .addApi(com.huawei.hms.support.api.push.HuaweiPush.PUSH_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        client.connect();
        callback.invoke(true);
    }

    @ReactMethod
    public void getTocken(final Callback callback) {
        if (!client.isConnected()) {
            callback.invoke(false);
            return;
        }
        // 同步调用方式
        new Thread() {
            @Override
            public void run() {
                PendingResult<TokenResult> tokenResult = com.huawei.hms.support.api.push.HuaweiPush.HuaweiPushApi.getToken(client);
                // 结果通过广播返回，不通过pendingResult返回，预留接口
                tokenResult.await();
                callback.invoke(true);
            }
        }.start();
    }

    @Override
    public void onConnected() {}

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}


    public boolean getEmuiLeval() {
        boolean ishuawei = false;
        Properties properties = new Properties();
        File propFile = new File(Environment.getRootDirectory(), "build.prop");
        FileInputStream fis = null;
        if (propFile != null && propFile.exists()) {
            try {
                fis = new FileInputStream(propFile);
                properties.load(fis);
                fis.close();
                fis = null;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
            }
        }

        if (properties.containsKey("ro.build.user")) {
            String user = properties.getProperty("ro.build.user");
            if (user.equals("huawei"))
                ishuawei = true;
        }
        if (properties.containsKey("ro.build.hw_emui_api_level")) {
            ishuawei = true;
        }
        return ishuawei;
    }
}

package com.hwpush;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.huawei.hms.support.api.push.PushReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by Kun on 2017/5/23.
 */

public class MyPushReceiver extends PushReceiver {
    @Override
    public void onEvent(Context context, Event event, Bundle extras) {
        if (Event.NOTIFICATION_OPENED.equals(event) || Event.NOTIFICATION_CLICK_BTN.equals(event)) {
            int notifyId = extras.getInt(BOUND_KEY.pushNotifyId, 0);
            if (0 != notifyId) {
                NotificationManager manager = (NotificationManager) context
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(notifyId);
            }
            String content = extras.getString(BOUND_KEY.pushMsgKey);
            try {
                JSONArray json = new JSONArray(content);
                JSONObject data = new JSONObject();
                for (int i = 0; i< json.length(); i++) {
                    JSONObject obj = json.getJSONObject(i);
                    Iterator<String> keys = obj.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        data.put(key, obj.get(key));
                    }
                }
                data.put("hwPush", "HWPUSH");
                WritableMap map = Arguments.createMap();
                map.putString("cn.jpush.android.EXTRA", data.toString());
                resultHWPush("openNotification", map);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        super.onEvent(context, event, extras);
    }

    @Override
    public void onToken(Context context, String token, Bundle bundle) {
        resultHWPush("HWToken", token);
    }

    @Override
    public boolean onPushMsg(Context context, byte[] msg, Bundle bundle) {
        try {
            String content = new String(msg, "UTF-8");
            resultHWPush("openNotification", content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onPushState(Context context, boolean pushState) {

    }

    public void resultHWPush(String keyname, Object result){
        if (result!=null && HuaweiPush.mReactApplicationContext!=null)
            HuaweiPush.mReactApplicationContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit(keyname, result);
    }
}

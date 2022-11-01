package com.miraclegarden.smsmessage.service;

import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import androidx.annotation.NonNull;

import com.miraclegarden.smsmessage.Activity.NotificationActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NotificationService extends NotificationListenerService {
    private static final String TAG = "NotificationService";

    public static final String QQ = "com.tencent.mobileqq";//qq信息
    public static final String WX = "com.tencent.mm";//微信信息
    public static final String MMS = "com.android.mms";//短信
    public static final String HONOR_MMS = "com.hihonor.mms";//荣耀短信
    public static final String MESSAGES = "com.google.android.apps.messaging";//信息
    public static final String IN_CALL = "com.android.incallui";//来电 -

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationActivity.sendMessage("监听服务成功!");
        return super.onStartCommand(intent, flags, startId);
    }

    /*@Override
    public void onNotificationPosted(StatusBarNotification sbn, RankingMap rankingMap) {
        switch (sbn.getPackageName()) {
            case MESSAGES:
            case MMS:
                NotificationActivity.sendMessage("收到短信");
                //initData(sbn);
            case HONOR_MMS:
                NotificationActivity.sendMessage("收到荣耀短信");
                initData(sbn);
                break;
            case QQ:
                NotificationActivity.sendMessage("收到短信");
                break;
            case WX:
                NotificationActivity.sendMessage("收到微信消息");
                break;
            case IN_CALL:
                NotificationActivity.sendMessage("收到来电");
                break;
            default:
                break;
        }
    }*/

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        switch (sbn.getPackageName()) {
            case MESSAGES:
            case MMS:
                NotificationActivity.sendMessage("收到短信");
                //initData(sbn);
            case HONOR_MMS:
                NotificationActivity.sendMessage("收到荣耀短信");
                initData(sbn);
                break;
            case QQ:
                NotificationActivity.sendMessage("收到短信");
                break;
            case WX:
                NotificationActivity.sendMessage("收到微信消息");
                break;
            case IN_CALL:
                NotificationActivity.sendMessage("收到来电");
                break;
            default:
                break;
        }
    }

    private void initData(StatusBarNotification sbn) {
        Bundle bundle = sbn.getNotification().extras;
        String title = bundle.getString(Notification.EXTRA_TITLE, "获取标题失败!");
        String context = bundle.getString(Notification.EXTRA_TEXT, "获取内容失败!");
        if (context.equals("获取内容失败!")) {
            if (sbn.getNotification().tickerText != null) {
                context = sbn.getNotification().tickerText.toString();
            }
        }
        NotificationActivity.sendMessage(title + " " + context);
        if (!title.equals("获取标题失败!") && !context.equals("获取内容失败!") && !title.contains("正在运行")) {
            Submit(title, context);
        }
    }

    private void Submit(String title, String context) {
        OkHttpClient client = new OkHttpClient();
        SharedPreferences sharedPreferences = getSharedPreferences("server", MODE_PRIVATE);
        FormBody formBody = new FormBody.Builder()
                .add("context", context)
                .add("source", title)
                .build();
        Request request = new Request.Builder().url(sharedPreferences.getString("host", ""))
                .post(formBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                NotificationActivity.sendMessage("提交失败:" + e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String str = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(str);
                        if (jsonObject.getInt("code") == 1) {
                            NotificationActivity.sendMessage("短信提交成功:" + title);
                        } else {
                            NotificationActivity.sendMessage("短信提交失败:" + title);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return;
                }
                NotificationActivity.sendMessage("提交失败:");
            }
        });
    }

    /**
     * 监听断开
     */
    @Override
    public void onListenerDisconnected() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // 通知侦听器断开连接 - 请求重新绑定
            requestRebind(new ComponentName(this, NotificationListenerService.class));
        }
    }

    /**
     * @param context 反正第二次启动失败
     */
    public static void toggleNotificationListenerService(Context context) {
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(context, NotificationService.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        pm.setComponentEnabledSetting(new ComponentName(context, NotificationService.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

}

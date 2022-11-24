package com.miraclegarden.smsmessage.Receiver;

import static android.provider.Telephony.Sms.Intents.getMessagesFromIntent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;

import androidx.annotation.NonNull;

import com.miraclegarden.smsmessage.Activity.MainActivity;
import com.miraclegarden.smsmessage.Activity.NotificationActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MySMSBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // 从intent中获取消息
        SmsMessage[] smsMessages = getMessagesFromIntent(intent);
        // 获取短信发送者号码
        String senderNumber = smsMessages[0].getOriginatingAddress();
        // 组装短信内容
        StringBuilder text = new StringBuilder();
        for (SmsMessage smsMessage : smsMessages) {
            text.append(smsMessage.getMessageBody());
        }
        Submit(senderNumber, text);

        sendMessage("广播接收:" + "号码:" + senderNumber + "内容:" + text);
        // 获取卡槽位置
        //Bundle bundle = intent.getExtras();
        //int slot = bundle.getInt("android.telephony.extra.SLOT_INDEX", -1);
    }

    private void Submit(String title, StringBuilder context) {
        OkHttpClient client = new OkHttpClient();
        FormBody formBody = new FormBody.Builder()
                .add("context", context.toString())
                .add("source", title)
                .build();
        Request request = new Request.Builder().url(MainActivity.sp.getString("host", ""))
                .post(formBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                sendMessage("提交失败: 网络异常" + e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String str = response.body().string();
                    sendMessage(title + "短信提交成功:" + str);
                    return;
                }
                sendMessage("提交失败: 服务端异常" + response.code());
            }
        });
    }


    public void sendMessage(String msg) {
        try {
            NotificationActivity.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*@Override
    public void onReceive(Context context, Intent intent) {
        String dString = SmsHelper.getSmsBody(intent);
        String address = SmsHelper.getSmsAddress(intent);
        NotificationActivity.sendMessage("号码:" + address + "内容:" + dString);
        Log.i("dimos", dString + "," + address);
        //阻止广播继续传递，如果该receiver比系统的级别高，
        //那么系统就不会收到短信通知了
        abortBroadcast();
    }*/

}

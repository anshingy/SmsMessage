package com.miraclegarden.smsmessage.Receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.huawei.hms.common.api.CommonStatusCodes;
import com.huawei.hms.support.api.client.Status;
import com.huawei.hms.support.sms.common.ReadSmsConstant;
import com.miraclegarden.smsmessage.Activity.NotificationActivity;
import com.miraclegarden.smsmessage.SmsHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MySMSBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String dString = SmsHelper.getSmsBody(intent);
        String address = SmsHelper.getSmsAddress(intent);
        NotificationActivity.sendMessage("号码:" + address + "内容:" + dString);
        Log.i("dimos", dString + "," + address);
        //阻止广播继续传递，如果该receiver比系统的级别高，
        //那么系统就不会收到短信通知了
        abortBroadcast();
    }

}

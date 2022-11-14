package com.miraclegarden.smsmessage.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;

import com.miraclegarden.library.app.MiracleGardenActivity;
import com.miraclegarden.smsmessage.databinding.ActivityNotificationBinding;
import com.miraclegarden.smsmessage.service.NotificationService;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NotificationActivity extends MiracleGardenActivity<ActivityNotificationBinding> {

    private static final Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            String str = (String) msg.obj;
            if (str != null) {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
                String t = format.format(new Date());
                if (textView != null) {
                    textView.append(t + "：" + str + "\n\r");
                    scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN));
                }
            }
        }
    };

    public static SharedPreferences sharedPreferences;

    public static void sendMessage(String str) {
        Message message = new Message();
        message.obj = str;
        NotificationActivity.handler.sendMessage(message);
    }

    @SuppressLint("StaticFieldLeak")
    private static TextView textView;
    @SuppressLint("StaticFieldLeak")
    private static NestedScrollView scrollView;
    private final String[] permissions = new String[]{
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.RECEIVE_BOOT_COMPLETED
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("server", MODE_PRIVATE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        NotificationActivity.textView = binding.text;
        NotificationActivity.scrollView = binding.scrollable;
        initPermission();
        initView();
    }


    private void initService() {
        NotificationService.toggleNotificationListenerService(this);
        Intent service = new Intent(this, NotificationService.class);
        startService(service);
    }

    private void initView() {
        binding.button2.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
            @Override
            public void onClick(View v) {
                //打开监听引用消息Notification access
                Intent intent_s = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                startActivity(intent_s);
            }
        });
        binding.button.setOnClickListener(v -> {
            OkHttpClient client = new OkHttpClient();
            FormBody formBody = new FormBody.Builder()
                    .add("context", "123")
                    .add("source", "1234567")
                    .build();
            Request request = new Request.Builder().url(MainActivity.sp.getString("host", ""))
                    .post(formBody)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    NotificationActivity.sendMessage("网络测试失败:网络异常" + e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String str = response.body().string();
                        NotificationActivity.sendMessage("网络测试成功:" + str);
                        return;
                    }
                    NotificationActivity.sendMessage("网络测试失败: 服务器异常" + response.code());
                }
            });
        });
    }

    private void initPermission() {
        checkPermission();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                sendMessage("没有" + permission + "权限");
            }
        }
    }


    //打开设置界面
    private void checkPermission() {
        if (!isEnabled()) {
            startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
        }
    }


    // 判断是否打开了通知监听权限
    private boolean isEnabled() {
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (String name : names) {
                final ComponentName cn = ComponentName.unflattenFromString(name);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}

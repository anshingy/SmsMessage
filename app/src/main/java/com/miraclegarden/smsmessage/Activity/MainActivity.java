package com.miraclegarden.smsmessage.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.miraclegarden.library.app.MiracleGardenActivity;
import com.miraclegarden.smsmessage.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends MiracleGardenActivity<ActivityMainBinding> {
    private SharedPreferences sp;
    private static final String TAG = "MainActivity";

    private final String[] permissions = new String[]{
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.RECEIVE_BOOT_COMPLETED
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences("server", MODE_PRIVATE);
        try {
            initPermission();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        initData();
        initView();
    }

    private void initPermission() throws PackageManager.NameNotFoundException {
        List<String> permissionList=new ArrayList<>();
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_SMS)!=PackageManager.PERMISSION_GRANTED){
            permissionList.add( Manifest.permission.READ_SMS);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.SEND_SMS)!=PackageManager.PERMISSION_GRANTED){
            permissionList.add( Manifest.permission.SEND_SMS);
        }
        if(!permissionList.isEmpty()){
            String[] permissions=permissionList.toArray(new String[0]);
            ActivityCompat.requestPermissions(MainActivity.this,permissions,1);
        }

        PackageManager packageManager = this.getPackageManager();
        for (String permission : permissions) {
            PermissionInfo permissionInfo = packageManager.getPermissionInfo(permission, 0);
            CharSequence permissionName = permissionInfo.loadLabel(packageManager);
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // 未获取权限
                Log.i(TAG, "您未获得【" + permissionName + "】的权限 ===>");
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                    // 这是一个坑，某些手机弹出提示时没有永不询问的复选框，点击拒绝就默认勾上了这个复选框，而某些手机上即使勾选上了永不询问的复选框也不起作用
                    Log.i(TAG, "您勾选了不再提示【" + permissionName + "】权限的申请");
                } else {
                    int MY_REQUEST_CODE = 1000;
                    ActivityCompat.requestPermissions(this, permissions, MY_REQUEST_CODE);
                }
            } else {
                Log.i(TAG, "您已获得了【" + permissionName + "】的权限");
            }
        }
    }

    private void initData() {
        if (sp == null) {
            return;
        }
        binding.host.setText(sp.getString("host", ""));
    }

    private void initView() {
        binding.yes.setOnClickListener(v -> {
            if (sp == null) {
                return;
            }
            if (binding.host.getText().toString().length() == 0) {
                Toast.makeText(this, "服务器和添加参数不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!binding.host.getText().toString().startsWith("http")) {
                Toast.makeText(this, "服务器地址错误", Toast.LENGTH_SHORT).show();
                return;
            }
            SharedPreferences.Editor edit = sp.edit();
            edit.putString("host", binding.host.getText().toString());
            edit.apply();
            startActivity(new Intent(MainActivity.this, NotificationActivity.class));
            finish();
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PackageManager packageManager = this.getPackageManager();
        PermissionInfo permissionInfo = null;
        for (int i = 0; i < permissions.length; i++) {
            try {
                permissionInfo = packageManager.getPermissionInfo(permissions[i], 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            CharSequence permissionName = permissionInfo.loadLabel(packageManager);
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "您同意了【" + permissionName + "】权限");
                Toast.makeText(this, "您同意了【" + permissionName + "】权限", Toast.LENGTH_SHORT).show();
            } else {
                Log.i(TAG, "您拒绝了【" + permissionName + "】权限");
                Toast.makeText(this, "您拒绝了【" + permissionName + "】权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

}

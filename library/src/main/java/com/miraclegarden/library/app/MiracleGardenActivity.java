package com.miraclegarden.library.app;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewbinding.ViewBinding;

import com.miraclegarden.library.app.utils.ViewBindingUtil;

public class MiracleGardenActivity<Binding extends ViewBinding> extends AppCompatActivity implements View.OnClickListener, Runnable {
    public Binding binding;

    public Binding getBinding() {
        return binding;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            //实体化
            binding = ViewBindingUtil.inflate(getClass(), getLayoutInflater());
            setContentView(binding.getRoot());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //快速设置
    public void setToolbar_Finish(Toolbar toolbar) {
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        addResume();
    }

    protected void addResume() {
    }


    @Override
    public void run() {

    }
}

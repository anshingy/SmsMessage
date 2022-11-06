package com.miraclegarden.library.app.listener;

import android.app.Activity;

import androidx.viewbinding.ViewBinding;

import java.util.List;

public interface OnBindViewHolderListener<Binding extends ViewBinding> {

    Activity getActivity();

    List<?> getList();

    /**
     * 每一个子item通知
     *
     * @param binding  item
     * @param position
     */
    void onBindViewHolder(Binding binding, int position);
}

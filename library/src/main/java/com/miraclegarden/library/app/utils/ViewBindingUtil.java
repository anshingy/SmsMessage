package com.miraclegarden.library.app.utils;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewbinding.ViewBinding;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ViewBindingUtil {
    private static final String TAG = "ViewBindingUtil";

    @SuppressWarnings("unchecked")
    @NonNull
    public static <Binding extends ViewBinding> Binding bind(Class<?> clazz, View rootView) {
        Class<?> bindingClass = getBindingClass(clazz);
        Binding binding = null;
        if (bindingClass != null) {
            try {
                Method method = bindingClass.getMethod("bind", View.class);
                binding = (Binding) method.invoke(null, rootView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return binding;
    }


    public static <Binding extends ViewBinding> Binding inflate(Class<?> clazz, LayoutInflater inflater) {
        return inflate(clazz, inflater, null);
    }

    public static <Binding extends ViewBinding> Binding inflate(Class<?> clazz, LayoutInflater inflater, ViewGroup root) {
        return inflate(clazz, inflater, root, false);
    }

    @SuppressWarnings("unchecked")
    @NonNull
    public static <Binding extends ViewBinding> Binding inflate(Class<?> clazz, LayoutInflater inflater, ViewGroup root, boolean attachToRoot) {
        Class<?> bindingClass = getBindingClass(clazz);
        Binding binding = null;
        if (bindingClass != null) {
            try {
                Method method = bindingClass.getMethod("inflate", LayoutInflater.class, ViewGroup.class, boolean.class);
                binding = (Binding) method.invoke(null, inflater, root, attachToRoot);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Objects.requireNonNull(binding);
    }

    public static Class<?> getBindingClass(Class<?> clazz) {
        Type[] types = null;
        Class<?> bindingClass = null;

        if (clazz.getGenericSuperclass() instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) clazz.getGenericSuperclass();
            types = Objects.requireNonNull(parameterizedType).getActualTypeArguments();
        } else {
            Type[] genericInterfaces = clazz.getGenericInterfaces();
            for (Type anInterface : genericInterfaces) {
                // 判断是否是参数化的类型
            }
        }

        if (types == null) {
            return null;
        }

        for (Type type : types) {
            if (type instanceof Class<?>) {
                Class<?> temp = (Class<?>) type;
                if (ViewBinding.class.isAssignableFrom(temp)) {
                    bindingClass = temp;
                }
            }
        }

        return bindingClass;
    }

    //-----------------获取 activity中的所有view
    private List<View> getAllViews(Activity act) {
        return getAllChildViews(act.getWindow().getDecorView());
    }

    private List<View> getAllChildViews(View view) {
        List<View> allchildren = new ArrayList();
        if (view instanceof ViewGroup) {
            ViewGroup vp = (ViewGroup) view;
            for (int i = 0; i < vp.getChildCount(); i++) {
                View viewchild = vp.getChildAt(i);
                allchildren.add(viewchild);
                //再次 调用本身（递归）
                allchildren.addAll(getAllChildViews(viewchild));
            }
        }
        return allchildren;
    }

}

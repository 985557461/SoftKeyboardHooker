package com.study.hooksoftkeyboard.helper.compat;

import android.os.IBinder;

import com.study.hooksoftkeyboard.reflect.MethodUtils;

import java.lang.reflect.InvocationTargetException;

public class IWindowManagerCompat {

    private static Class sClass;

    public static Class Class() throws ClassNotFoundException {
        if (sClass == null) {
            sClass = Class.forName("android.view.IWindowManager");
        }
        return sClass;
    }

    public static Object asInterface(IBinder binder) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Class clazz = Class.forName("android.view.IWindowManager$Stub");
        return MethodUtils.invokeStaticMethod(clazz, "asInterface", binder);
    }
}

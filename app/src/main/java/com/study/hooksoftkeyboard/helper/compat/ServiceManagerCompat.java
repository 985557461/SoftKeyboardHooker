package com.study.hooksoftkeyboard.helper.compat;

import android.os.IBinder;

import com.study.hooksoftkeyboard.reflect.MethodUtils;

import java.lang.reflect.InvocationTargetException;

//反射系统的ServiceManager类，判断系统的ServiceManager是否存在某一个服务
public class ServiceManagerCompat {

    private static Class sClass = null;

    public static final Class Class() throws ClassNotFoundException {
        if (sClass == null) {
            sClass = Class.forName("android.os.ServiceManager");
        }
        return sClass;
    }

    public static IBinder getService(String name) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
       return (IBinder) MethodUtils.invokeStaticMethod(Class(), "getService", name);
    }
}

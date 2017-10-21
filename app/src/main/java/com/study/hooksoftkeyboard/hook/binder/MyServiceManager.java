package com.study.hooksoftkeyboard.hook.binder;

import android.os.IBinder;

import java.util.HashMap;
import java.util.Map;

//本地管理服务的类，自己本地缓存一份，以后方便取用
public class MyServiceManager {

    private static Map<String, IBinder> mOriginServiceCache = new HashMap<String, IBinder>(1);
    private static Map<String, IBinder> mProxiedServiceCache = new HashMap<String, IBinder>(1);
    private static Map<String, Object> mProxiedObjCache = new HashMap<String, Object>(1);

    static IBinder getOriginService(String serviceName) {
        return mOriginServiceCache.get(serviceName);
    }

    public static void addOriginService(String serviceName, IBinder service) {
        mOriginServiceCache.put(serviceName, service);
    }

    static  void addProxiedServiceCache(String serviceName, IBinder proxyService) {
        mProxiedServiceCache.put(serviceName, proxyService);
    }

    static Object getProxiedObj(String servicename) {
        return mProxiedObjCache.get(servicename);
    }

    static void addProxiedObj(String servicename, Object obj) {
        mProxiedObjCache.put(servicename, obj);
    }
}

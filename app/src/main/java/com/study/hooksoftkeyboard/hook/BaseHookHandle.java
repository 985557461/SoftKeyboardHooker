package com.study.hooksoftkeyboard.hook;

import android.content.Context;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

//代理hook类的具体的实现，里面可以hook不同的方法，不同方法的实现封装在HookedMethodHandler
public abstract class BaseHookHandle {

    protected Context mHostContext;

    protected Map<String, HookedMethodHandler> sHookedMethodHandlers = new HashMap<>(5);

    public BaseHookHandle(Context hostContext) {
        mHostContext = hostContext;
        init();
    }

    protected abstract void init();

    public Set<String> getHookedMethodNames(){
        return sHookedMethodHandlers.keySet();
    }

    public HookedMethodHandler getHookedMethodHandler(Method method) {
        if (method != null) {
            return sHookedMethodHandlers.get(method.getName());
        } else {
            return null;
        }
    }
}

package com.study.hooksoftkeyboard.hook.binder;

import android.content.Context;
import android.text.TextUtils;

import com.study.hooksoftkeyboard.helper.MyProxy;
import com.study.hooksoftkeyboard.hook.Hook;
import com.study.hooksoftkeyboard.hook.HookedMethodHandler;
import com.study.hooksoftkeyboard.reflect.Utils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

abstract class BinderHook extends Hook implements InvocationHandler {
    private Object mOldObj;

    public BinderHook(Context hostContext) {
        super(hostContext);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//        android.util.Log.d("xiaoyu","BinderHook----:"+getServiceName()+" method--:"+method.getName());
//        for(int i=0;i<args.length;i++){
//            android.util.Log.d("xiaoyu","args----:"+args[i].getClass().toString());
//        }
        try {
            if (!isEnable()) {
                return method.invoke(mOldObj, args);
            }
            HookedMethodHandler hookedMethodHandler = mHookHandles.getHookedMethodHandler(method);
            if (hookedMethodHandler != null) {
                return hookedMethodHandler.doHookInner(mOldObj, method, args);
            } else {
                return method.invoke(mOldObj, args);
            }
        } catch (InvocationTargetException e) {
            Throwable cause = e.getTargetException();
            if (cause != null && MyProxy.isMethodDeclaredThrowable(method, cause)) {
                throw cause;
            } else if (cause != null) {
                RuntimeException runtimeException = !TextUtils.isEmpty(cause.getMessage()) ? new RuntimeException(cause.getMessage()) : new RuntimeException();
                runtimeException.initCause(cause);
                throw runtimeException;
            } else {
                RuntimeException runtimeException = !TextUtils.isEmpty(e.getMessage()) ? new RuntimeException(e.getMessage()) : new RuntimeException();
                runtimeException.initCause(e);
                throw runtimeException;
            }
        } catch (IllegalArgumentException e) {
            try {
                StringBuilder sb = new StringBuilder();
                sb.append(" DROIDPLUGIN{");
                if (method != null) {
                    sb.append("method[").append(method.toString()).append("]");
                } else {
                    sb.append("method[").append("NULL").append("]");
                }
                if (args != null) {
                    sb.append("args[").append(Arrays.toString(args)).append("]");
                } else {
                    sb.append("args[").append("NULL").append("]");
                }
                sb.append("}");

                String message = e.getMessage() + sb.toString();
                throw new IllegalArgumentException(message, e);
            } catch (Throwable e1) {
                throw e;
            }
        } catch (Throwable e) {
            if (MyProxy.isMethodDeclaredThrowable(method, e)) {
                throw e;
            } else {
                RuntimeException runtimeException = !TextUtils.isEmpty(e.getMessage()) ? new RuntimeException(e.getMessage()) : new RuntimeException();
                runtimeException.initCause(e);
                throw runtimeException;
            }
        }
    }

    abstract Object getOldObj() throws Exception;

    void setOldObj(Object mOldObj) {
        this.mOldObj = mOldObj;
    }

    public abstract String getServiceName();

    @Override
    protected void onInstall(ClassLoader classLoader) throws Throwable {
        new ServiceManagerCacheBinderHook(mHostContext, getServiceName()).onInstall(classLoader);
        mOldObj = getOldObj();
        Class<?> clazz = mOldObj.getClass();
        List<Class<?>> interfaces = Utils.getAllInterfaces(clazz);
        Class[] ifs = interfaces != null && interfaces.size() > 0 ? interfaces.toArray(new Class[interfaces.size()]) : new Class[0];
        Object proxiedObj = MyProxy.newProxyInstance(clazz.getClassLoader(), ifs, this);
        MyServiceManager.addProxiedObj(getServiceName(), proxiedObj);
    }
}

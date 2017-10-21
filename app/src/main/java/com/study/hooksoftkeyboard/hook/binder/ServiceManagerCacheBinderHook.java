package com.study.hooksoftkeyboard.hook.binder;

import android.content.Context;
import android.os.IBinder;
import android.text.TextUtils;

import com.study.hooksoftkeyboard.helper.MyProxy;
import com.study.hooksoftkeyboard.helper.compat.ServiceManagerCompat;
import com.study.hooksoftkeyboard.hook.BaseHookHandle;
import com.study.hooksoftkeyboard.hook.Hook;
import com.study.hooksoftkeyboard.hook.HookedMethodHandler;
import com.study.hooksoftkeyboard.reflect.FieldUtils;
import com.study.hooksoftkeyboard.reflect.Utils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

//每次hook一个服务的时候，先判断系统的ServiceManager中的sCache中有没有该服务，如果有则移除，添加自己的hook服务
public class ServiceManagerCacheBinderHook extends Hook implements InvocationHandler {
    private String mServiceName;

    public ServiceManagerCacheBinderHook(Context hostContext, String servicename) {
        super(hostContext);
        mServiceName = servicename;
        setEnable(true);
    }

    @Override
    protected void onInstall(ClassLoader classLoader) throws Throwable {
        Object sCacheObj = FieldUtils.readStaticField(ServiceManagerCompat.Class(), "sCache");
        if (sCacheObj instanceof Map) {
            Map sCache = (Map) sCacheObj;
            Object Obj = sCache.get(mServiceName);
            if (Obj != null && false) {
                //FIXME 已经有了怎么处理？这里我们只是把原来的给remove掉，再添加自己的。程序下次取用的时候就变成我们hook过的了。
                //但是这样有缺陷。
                throw new RuntimeException("Can not install binder hook for " + mServiceName);
            } else {
                sCache.remove(mServiceName);
                IBinder mServiceIBinder = ServiceManagerCompat.getService(mServiceName);
                if (mServiceIBinder != null) {
                    MyServiceManager.addOriginService(mServiceName, mServiceIBinder);
                    Class clazz = mServiceIBinder.getClass();
                    List<Class<?>> interfaces = Utils.getAllInterfaces(clazz);
                    Class[] ifs = interfaces != null && interfaces.size() > 0 ? interfaces.toArray(new Class[interfaces.size()]) : new Class[0];
                    IBinder mProxyServiceIBinder = (IBinder) MyProxy.newProxyInstance(clazz.getClassLoader(), ifs, this);
                    sCache.put(mServiceName, mProxyServiceIBinder);
                    MyServiceManager.addProxiedServiceCache(mServiceName, mProxyServiceIBinder);
                }
            }
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            IBinder originService = MyServiceManager.getOriginService(mServiceName);
            if (!isEnable()) {
                return method.invoke(originService, args);
            }
            HookedMethodHandler hookedMethodHandler = mHookHandles.getHookedMethodHandler(method);
            if (hookedMethodHandler != null) {
                return hookedMethodHandler.doHookInner(originService, method, args);
            } else {
                return method.invoke(originService, args);
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

    private class ServiceManagerHookHandle extends BaseHookHandle {

        private ServiceManagerHookHandle(Context context) {
            super(context);
        }

        @Override
        protected void init() {
            sHookedMethodHandlers.put("queryLocalInterface", new queryLocalInterface(mHostContext));
        }


        class queryLocalInterface extends HookedMethodHandler {
            public queryLocalInterface(Context context) {
                super(context);
            }

            @Override
            protected void afterInvoke(Object receiver, Method method, Object[] args, Object invokeResult) throws Throwable {
                Object localInterface = invokeResult;
                Object proxiedObj = MyServiceManager.getProxiedObj(mServiceName);
                if (localInterface == null && proxiedObj != null) {
                    setFakedResult(proxiedObj);
                }
            }
        }
    }

    @Override
    protected BaseHookHandle createHookHandle() {
        return new ServiceManagerHookHandle(mHostContext);
    }
}

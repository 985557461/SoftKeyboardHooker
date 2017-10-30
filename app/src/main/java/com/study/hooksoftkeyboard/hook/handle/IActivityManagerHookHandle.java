package com.study.hooksoftkeyboard.hook.handle;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;

import com.study.hooksoftkeyboard.hook.BaseHookHandle;
import com.study.hooksoftkeyboard.hook.HookedMethodHandler;
import com.study.hooksoftkeyboard.listener.SoftMessageDispatcher;

import java.lang.reflect.Method;

public class IActivityManagerHookHandle extends BaseHookHandle {

    private static final String TAG = IActivityManagerHookHandle.class.getSimpleName();

    public IActivityManagerHookHandle(Context hostContext) {
        super(hostContext);
    }

    @Override
    protected void init() {
        sHookedMethodHandlers.put("startActivity", new startActivity(mHostContext));
    }

    private static class startActivity extends HookedMethodHandler {
        private Handler handler = new Handler();

        public startActivity(Context hostContext) {
            super(hostContext);
        }

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            int intentOfArgIndex = findFirstIntentIndexInArgs(args);
            //找到参数中的Intent
            Intent targetIntent = null;
            if (args != null && args.length > 1 && intentOfArgIndex >= 0) {
                targetIntent = (Intent) args[intentOfArgIndex];
            }

            if (targetIntent != null) {
                ComponentName componentName = targetIntent.getComponent();
                findActivityInfo(componentName);
            }

            return super.beforeInvoke(receiver, method, args);
        }

        @Override
        protected void afterInvoke(Object receiver, Method method, Object[] args, Object invokeResult) throws Throwable {
            super.afterInvoke(receiver, method, args, invokeResult);
        }

        private void findActivityInfo(ComponentName componentName) {
            if (componentName == null) {
                return;
            }
            PackageInfo packageInfo = null;
            try {
                packageInfo = mHostContext.getPackageManager().getPackageInfo(mHostContext.getPackageName(), PackageManager.GET_ACTIVITIES);
                if (packageInfo != null && packageInfo.activities != null) {
                    for (ActivityInfo activityInfo : packageInfo.activities) {
                        if (componentName.getClassName().equals(activityInfo.name)) {
                            //判断当前activity的键盘的模式
                            //adjustResize 0x010
                            //stateAlwaysVisible 0x005
                            //stateVisible 0x004
                            if ((activityInfo.softInputMode & 0x010) != 0 ||
                                    (activityInfo.softInputMode & 0x005) != 0 ||
                                    (activityInfo.softInputMode & 0x004) != 0) {
                                //FixMe 这里先加上一个延迟处理，因为此时Activity刚启动，还没有回调onCreate，没有注册listener
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        SoftMessageDispatcher.getInstance().initOriginState(SoftMessageDispatcher.SHOW);
                                        SoftMessageDispatcher.getInstance().dispatchShowMessage();
                                    }
                                }, 500);
                            } else {
                                SoftMessageDispatcher.getInstance().initOriginState(SoftMessageDispatcher.HIDE);
                            }
                        }
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private static int findFirstIntentIndexInArgs(Object[] args) {
        if (args != null && args.length > 0) {
            int i = 0;
            for (Object arg : args) {
                if (arg != null && arg instanceof Intent) {
                    return i;
                }
                i++;
            }
        }
        return -1;
    }
}

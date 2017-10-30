package com.study.hooksoftkeyboard.hook.handle;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.study.hooksoftkeyboard.helper.MyProxy;
import com.study.hooksoftkeyboard.helper.compat.IInputConnectionWrapperCompat;
import com.study.hooksoftkeyboard.hook.BaseHookHandle;
import com.study.hooksoftkeyboard.hook.HookedMethodHandler;
import com.study.hooksoftkeyboard.hook.proxy.InputConnectionHook;
import com.study.hooksoftkeyboard.listener.SoftMessageDispatcher;
import com.study.hooksoftkeyboard.reflect.FieldUtils;
import com.study.hooksoftkeyboard.reflect.Utils;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class IInputMethodManagerHookHandle extends BaseHookHandle {

    public IInputMethodManagerHookHandle(Context hostContext) {
        super(hostContext);
    }

    @Override
    protected void init() {
        sHookedMethodHandlers.put("showSoftInput", new showSoftInput(mHostContext));
        sHookedMethodHandlers.put("showSoftInputFromInputMethod", new showSoftInputFromInputMethod(mHostContext));
        sHookedMethodHandlers.put("hideSoftInputFromWindow", new hideSoftInputFromWindow(mHostContext));
        sHookedMethodHandlers.put("hideSoftInputFromInputMethod", new hideSoftInputFromInputMethod(mHostContext));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            //7.0以下会调用下面两个方法
            sHookedMethodHandlers.put("startInput", new startInput(mHostContext));
            sHookedMethodHandlers.put("windowGainedFocus", new windowGainedFocus(mHostContext));
        } else {
            //7.0以上会调用下面的方法
            sHookedMethodHandlers.put("startInputOrWindowGainedFocus", new startInputOrWindowGainedFocus(mHostContext));
        }
    }

    private class IInputMethodManagerHookedMethodHandler extends HookedMethodHandler {
        public IInputMethodManagerHookedMethodHandler(Context hostContext) {
            super(hostContext);
        }
    }

    private class showSoftInput extends IInputMethodManagerHookedMethodHandler {
        public showSoftInput(Context hostContext) {
            super(hostContext);
        }

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            Log.d("xiaoyu", "showSoftInput");
            SoftMessageDispatcher.getInstance().dispatchShowMessage();
            return super.beforeInvoke(receiver, method, args);
        }
    }

    private class showSoftInputFromInputMethod extends IInputMethodManagerHookedMethodHandler {
        public showSoftInputFromInputMethod(Context hostContext) {
            super(hostContext);
        }

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            Log.d("xiaoyu", "showSoftInputFromInputMethod");
            SoftMessageDispatcher.getInstance().dispatchShowMessage();
            return super.beforeInvoke(receiver, method, args);
        }
    }

    private class hideSoftInputFromWindow extends IInputMethodManagerHookedMethodHandler {
        public hideSoftInputFromWindow(Context hostContext) {
            super(hostContext);
        }

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            SoftMessageDispatcher.getInstance().dispatchHideMessage();
            return super.beforeInvoke(receiver, method, args);
        }
    }

    private class hideSoftInputFromInputMethod extends IInputMethodManagerHookedMethodHandler {
        public hideSoftInputFromInputMethod(Context hostContext) {
            super(hostContext);
        }

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            SoftMessageDispatcher.getInstance().dispatchHideMessage();
            return super.beforeInvoke(receiver, method, args);
        }
    }

    private class startInput extends IInputMethodManagerHookedMethodHandler {
        public startInput(Context hostContext) {
            super(hostContext);
        }

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            //替换EditText中的InputConnection
            hookInputConnection(1, args);

            return super.beforeInvoke(receiver, method, args);
        }
    }

    private class windowGainedFocus extends IInputMethodManagerHookedMethodHandler {
        public windowGainedFocus(Context hostContext) {
            super(hostContext);
        }

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            //替换EditText中的InputConnection
            hookInputConnection(args.length - 1, args);

            return super.beforeInvoke(receiver, method, args);
        }
    }

    private class startInputOrWindowGainedFocus extends IInputMethodManagerHookedMethodHandler {
        public startInputOrWindowGainedFocus(Context hostContext) {
            super(hostContext);
        }

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            //替换EditText中的InputConnection
            hookInputConnection(args.length - 2, args);

            return super.beforeInvoke(receiver, method, args);
        }
    }

    private void hookInputConnection(int argIndex, Object[] args) {
        if (args[argIndex] == null) {
            return;
        }
        Log.d("xiaoyu", "hookInputConnection mInputConnection替换掉");
        Field mInputConnectionField = null;
        try {
            //反射当前正在交互的EditText
            Object sInstance = FieldUtils.readStaticField(InputMethodManager.class, "sInstance");
            View mServedView = (View) FieldUtils.readField(sInstance, "mServedView", true);
            Log.d("xiaoyu", "mInputConnection替换掉 mServedView--:" + ((EditText) mServedView).getText().toString());
            mInputConnectionField = FieldUtils.getDeclaredField(IInputConnectionWrapperCompat.Class(), "mInputConnection", true);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                //7.0以下
                WeakReference<InputConnection> mInputConnectionInstanceWeak = (WeakReference<InputConnection>) FieldUtils.readField(mInputConnectionField, args[argIndex]);
                InputConnection mInputConnectionInstance = mInputConnectionInstanceWeak.get();

                List<Class<?>> interfaces = Utils.getAllInterfaces(mInputConnectionInstance.getClass());
                Class[] ifs = interfaces != null && interfaces.size() > 0 ? interfaces.toArray(new Class[interfaces.size()]) : new Class[0];
                InputConnection mInputConnectionProxy = (InputConnection) MyProxy.newProxyInstance(mInputConnectionInstance.getClass().getClassLoader(), ifs, new InputConnectionHook(mHostContext, mInputConnectionInstance, mServedView));
                WeakReference<InputConnection> mInputConnectionProxyWeak = new WeakReference<>(mInputConnectionProxy);
                FieldUtils.writeField(mInputConnectionField, args[argIndex], mInputConnectionProxyWeak, true);
            } else {
                //7.0以上
                Object mInputConnectionInstance = FieldUtils.readField(mInputConnectionField, args[argIndex]);

                List<Class<?>> interfaces = Utils.getAllInterfaces(mInputConnectionInstance.getClass());
                Class[] ifs = interfaces != null && interfaces.size() > 0 ? interfaces.toArray(new Class[interfaces.size()]) : new Class[0];
                InputConnection mInputConnectionProxy = (InputConnection) MyProxy.newProxyInstance(mInputConnectionInstance.getClass().getClassLoader(), ifs, new InputConnectionHook(mHostContext, mInputConnectionInstance, mServedView));
                FieldUtils.writeField(mInputConnectionField, args[argIndex], mInputConnectionProxy, true);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

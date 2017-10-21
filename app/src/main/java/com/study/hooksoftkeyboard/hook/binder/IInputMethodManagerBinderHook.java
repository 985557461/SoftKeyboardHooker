package com.study.hooksoftkeyboard.hook.binder;

import android.content.Context;
import android.os.IBinder;
import android.view.inputmethod.InputMethodManager;

import com.study.hooksoftkeyboard.helper.compat.IInputMethodManagerCompat;
import com.study.hooksoftkeyboard.hook.BaseHookHandle;
import com.study.hooksoftkeyboard.hook.handle.IInputMethodManagerHookHandle;
import com.study.hooksoftkeyboard.reflect.FieldUtils;

public class IInputMethodManagerBinderHook extends BinderHook {

    private final static String SERVICE_NAME = Context.INPUT_METHOD_SERVICE;

    public IInputMethodManagerBinderHook(Context hostContext) {
        super(hostContext);
    }

    @Override
    public Object getOldObj() throws Exception {
        IBinder iBinder = MyServiceManager.getOriginService(SERVICE_NAME);
        return IInputMethodManagerCompat.asInterface(iBinder);
    }

    @Override
    protected void onInstall(ClassLoader classLoader) throws Throwable {
        super.onInstall(classLoader);
        Object obj = FieldUtils.readStaticField(InputMethodManager.class, "sInstance");
        if (obj != null) {
            FieldUtils.writeStaticField(InputMethodManager.class, "sInstance", null);
        }
        mHostContext.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }

    @Override
    protected BaseHookHandle createHookHandle() {
        return new IInputMethodManagerHookHandle(mHostContext);
    }
}

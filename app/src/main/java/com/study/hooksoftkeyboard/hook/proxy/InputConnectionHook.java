package com.study.hooksoftkeyboard.hook.proxy;

import android.content.Context;
import android.view.View;

import com.study.hooksoftkeyboard.hook.BaseHookHandle;
import com.study.hooksoftkeyboard.hook.handle.InputConnectionHookHandle;

/**
 * Created by xiaoyu on 2017/9/5.
 */

public class InputConnectionHook extends ProxyHook {
    private View mServedView;

    public InputConnectionHook(Context context, Object oldObj, View mServedView) {
        super(context);
        setOldObj(oldObj);
        setEnable(true);
        this.mServedView = mServedView;
        mHookHandles = createHookHandle();
    }

    @Override
    protected BaseHookHandle createHookHandle() {
        return new InputConnectionHookHandle(mHostContext,mServedView);
    }

    @Override
    protected void onInstall(ClassLoader classLoader) throws Throwable {

    }
}

package com.study.hooksoftkeyboard.hook.handle;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.study.hooksoftkeyboard.hook.BaseHookHandle;
import com.study.hooksoftkeyboard.hook.HookedMethodHandler;
import com.study.hooksoftkeyboard.listener.SoftMessageDispatcher;
import com.study.hooksoftkeyboard.reflect.FieldUtils;

import java.lang.reflect.Method;

/**
 * Created by xiaoyu on 2017/9/5.
 */

public class InputConnectionHookHandle extends BaseHookHandle {
    private View mServedView;

    public InputConnectionHookHandle(Context hostContext, View mServedView) {
        super(hostContext);
        this.mServedView = mServedView;
    }

    @Override
    protected void init() {
        sHookedMethodHandlers.put("finishComposingText", new finishComposingText(mHostContext));
    }

    private class finishComposingText extends HookedMethodHandler {
        public finishComposingText(Context hostContext) {
            super(hostContext);
        }

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            Log.d("xiaoyu", "finishComposingText 参数个数:"+args.length);
            for(int i=0;i<args.length;i++){
                Log.d("xiaoyu","参数 "+args[i].getClass().getName()+"  "+args[i].toString());
            }
            //反射当前正在交互的EditText,判断mServedView和当前交互的EditText是否是同一个，如果不是同一个，说明发生了EditText切换，键盘并没有关闭
            Object sInstance = FieldUtils.readStaticField(InputMethodManager.class, "sInstance");
            View currentServedView = (View) FieldUtils.readField(sInstance, "mServedView", true);
            Log.d("xiaoyu", "mServedView--:" + ((EditText) mServedView).getText().toString());
            Log.d("xiaoyu", "currentServedView--:" + ((EditText) currentServedView).getText().toString());
            if (mServedView == currentServedView) {
                Log.d("xiaoyu", "键盘  隐藏");
                SoftMessageDispatcher.getInstance().dispatchHideMessage();
            }

            return super.beforeInvoke(receiver, method, args);
        }
    }
}

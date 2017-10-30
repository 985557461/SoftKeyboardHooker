package com.study.hooksoftkeyboard.listener;

import android.util.Log;

import com.study.hooksoftkeyboard.SFKeyboardManager;

/**
 * Created by xiaoyu on 2017/9/6.
 */

public class SoftMessageDispatcher {

    private static SoftMessageDispatcher mInstance = new SoftMessageDispatcher();

    public static final int UNKNOWN = -1;//键盘状态未初始化
    public static final int HIDE = 0;//键盘隐藏
    public static final int SHOW = 1;//键盘显示

    public int currentState = UNKNOWN;

    private SoftMessageDispatcher() {
    }

    public static SoftMessageDispatcher getInstance() {
        return mInstance;
    }

    public void initOriginState(int status) {
        currentState = status;
    }

    //用户不需要调用此方法
    public void dispatchShowMessage() {
        if (currentState != -1 && currentState == HIDE) {
            currentState = SHOW;
            Log.d("xiaoyu", "键盘真的显示啦!!!");
            for (SoftKeyboardListener listener : SFKeyboardManager.getInstance().getListeners()) {
                listener.onKeyboardShow();
            }
        }
    }

    //用户不需要调用此方法
    public void dispatchHideMessage() {
        if (currentState != -1 && currentState == SHOW) {
            currentState = HIDE;
            Log.d("xiaoyu", "键盘真的隐藏啦!!!");
            for (SoftKeyboardListener listener : SFKeyboardManager.getInstance().getListeners()) {
                listener.onKeyboardHide();
            }
        }
    }
}

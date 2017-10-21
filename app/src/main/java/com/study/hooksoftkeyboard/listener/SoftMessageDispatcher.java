package com.study.hooksoftkeyboard.listener;

import com.study.hooksoftkeyboard.SFKeyboardManager;

/**
 * Created by xiaoyu on 2017/9/6.
 */

public class SoftMessageDispatcher {

    private static SoftMessageDispatcher mInstance = new SoftMessageDispatcher();

    private boolean softShow = false;

    private SoftMessageDispatcher() {
    }

    public static SoftMessageDispatcher getInstance() {
        return mInstance;
    }

    //用户不需要调用此方法
    public void dispatchShowMessage() {
        if (!softShow) {
            softShow = true;
            for (SoftKeyboardListener listener : SFKeyboardManager.getInstance().getListeners()) {
                listener.onKeyboardShow();
            }
        }
    }

    //用户不需要调用此方法
    public void dispatchHideMessage() {
        if (softShow) {
            softShow = false;
            for (SoftKeyboardListener listener : SFKeyboardManager.getInstance().getListeners()) {
                listener.onKeyboardHide();
            }
        }
    }
}

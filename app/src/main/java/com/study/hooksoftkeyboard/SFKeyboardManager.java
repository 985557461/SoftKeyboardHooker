package com.study.hooksoftkeyboard;

import com.study.hooksoftkeyboard.listener.SoftKeyboardListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaoyu on 2017/8/4.
 */
public class SFKeyboardManager {
    private static SFKeyboardManager mInstance = new SFKeyboardManager();

    private List<SoftKeyboardListener> listeners = new ArrayList<>();

    private SFKeyboardManager() {
    }

    public static SFKeyboardManager getInstance() {
        return mInstance;
    }

    public void registerListener(SoftKeyboardListener listener) {
        listeners.add(listener);
    }

    public void unRegisterListener(SoftKeyboardListener listener) {
        listeners.remove(listener);
    }

    public void clear() {
        listeners.clear();
    }

    public List<SoftKeyboardListener> getListeners() {
        return listeners;
    }
}

package com.study.hooksoftkeyboard.helper.compat;

/**
 * Created by Andy Zhang(zhangyong232@gmail.com) on 2015/5/1.
 */
public class IActivityManagerCompat {

    private static Class sClass;

    public static Class Class() throws ClassNotFoundException {
        if (sClass == null) {
            sClass = Class.forName("android.app.IActivityManager");
        }
        return sClass;
    }

    public static boolean isIActivityManager(Object obj){
        if (obj == null) {
            return false;
        } else {
            try {
                Class clazz = Class();
                return clazz.isInstance(obj);
            } catch (ClassNotFoundException e) {
                return false;
            }
        }
    }
}

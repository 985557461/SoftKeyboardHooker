package com.study.hooksoftkeyboard.helper.compat;

import android.os.UserHandle;

import com.study.hooksoftkeyboard.reflect.MethodUtils;

/**
 * Created by Andy Zhang(zhangyong232@gmail.com) on 2015/4/13.
 */
public class UserHandleCompat {

    //    UserHandle.getCallingUserId()
    public static int getCallingUserId() {
        try {
            return (int) MethodUtils.invokeStaticMethod(UserHandle.class, "getCallingUserId");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}

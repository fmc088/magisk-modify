package com.topjohnwu.magisk.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SysPropUtils {
    /**
     * getSysPro 获取系统属性
     *
     * @param key 属性名称
     * @return
     */
    public static String getSysPro(String key) {
        String result = "false";
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            result = (String) get.invoke(c, key);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return result;
    }
}

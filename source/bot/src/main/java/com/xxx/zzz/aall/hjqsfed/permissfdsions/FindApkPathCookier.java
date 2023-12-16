package com.xxx.zzz.aall.hjqsfed.permissfdsions;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;

import androidx.annotation.NonNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class FindApkPathCookier {

    @SuppressWarnings("JavaReflectionMemberAccess")
    @SuppressLint("PrivateApi")
    static int findApkPathCookie(@NonNull Context context, @NonNull String apkPath) {
        AssetManager assets = context.getAssets();
        Integer cookie;

        try {
            if (context.getApplicationInfo().targetSdkVersion >= AndroidVersione.ANDROID_9 &&
                    Build.VERSION.SDK_INT >= AndroidVersione.ANDROID_9 &&
                    Build.VERSION.SDK_INT < AndroidVersione.ANDROID_11) {

                // 反射套娃操作：实测这种方式只在 Android 9.0 和 Android 10.0 有效果，在 Android 11 上面就失效了
                Method metaGetDeclaredMethod = Class.class.getDeclaredMethod(
                        "getDeclaredMethod", String.class, Class[].class);
                metaGetDeclaredMethod.setAccessible(true);
                // 注意 AssetManager.findCookieForPath 是 Android 9.0（API 28）的时候才添加的方法
                // 而 Android 9.0 用的是 AssetManager.addAssetPath 来获取 cookie
                // 具体可以参考 PackageParser.parseBaseApk 方法源码的实现
                Method findCookieForPathMethod = (Method) metaGetDeclaredMethod.invoke(AssetManager.class,
                        "findCookieForPath", new Class[]{String.class});
                if (findCookieForPathMethod != null) {
                    findCookieForPathMethod.setAccessible(true);
                    cookie = (Integer) findCookieForPathMethod.invoke(context.getAssets(), apkPath);
                    if (cookie != null) {
                        return cookie;
                    }
                }
            }

            Method addAssetPathMethod = assets.getClass().getDeclaredMethod("addAssetPath", String.class);
            cookie = (Integer) addAssetPathMethod.invoke(assets, apkPath);
            if (cookie != null) {
                return cookie;
            }

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        // 获取失败直接返回 0
        // 为什么不直接返回 Integer，而是返回 int 类型？
        // 去看看 AssetManager.findCookieForPath 获取失败会返回什么就知道了
        return 0;
    }


}

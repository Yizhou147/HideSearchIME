package com.hide.searchime;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

import java.lang.reflect.Method;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookEntry implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XposedBridge.log("HideSearchIME: loaded in " + lpparam.packageName);

        if (!"com.android.quicksearchbox".equals(lpparam.packageName)) {
            return;
        }

        XposedBridge.log("HideSearchIME: hooking SearchActivity");

        try {
            // 目标类用目标 app 的 ClassLoader
            Class<?> searchActivity = lpparam.classLoader.loadClass(
                "com.android.quicksearchbox.SearchActivity");
            Method onCreateMethod = searchActivity.getDeclaredMethod("onCreate", Bundle.class);

            // 关键：用 HookEntry 自己的 ClassLoader（包含 LSPosed 框架）
            // 直接调用 XposedBridge.hookMethod（编译时解析为 stub，运行时替换为真实实现）
            Object result = XposedBridge.hookMethod(onCreateMethod, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Activity activity = (Activity) param.thisObject;
                    activity.getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    XposedBridge.log("HideSearchIME: keyboard hidden!");
                }
            });

            XposedBridge.log("HideSearchIME: hookMethod returned: " + result);

        } catch (Throwable t) {
            XposedBridge.log("HideSearchIME: error - " + t.toString());
            for (StackTraceElement e : t.getStackTrace()) {
                XposedBridge.log("  at " + e.toString());
            }
        }
    }
}

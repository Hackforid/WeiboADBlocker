package com.smilehacker.weiboadblocker

import dalvik.system.PathClassLoader
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.io.File

/**
 * Created by kleist on 2016/10/14.
 */
class Main: IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        if (lpparam!!.packageName!!.indexOf("com.sina.weibo") < 0) {
            return
        }
        XposedBridge.log("try f**k sina weibo ${lpparam?.packageName}")

        if (!BuildConfig.DEBUG) {
            hook(lpparam)
        } else {
            try {
                XposedBridge.log("run in hot loader")
                runInDebugHotloader(lpparam)
            } catch (e: Throwable) {
                XposedBridge.log(e)
            }
        }
    }

    fun runInDebugHotloader(lpparam: XC_LoadPackage.LoadPackageParam) {

        val packageName = Main::class.java.`package`.name
        var filePath = "/data/app/$packageName-1/base.apk"
        if (!File(filePath).exists()) {
            filePath = "/data/app/$packageName-2/base.apk"
            if (!File(filePath).exists()) {
                XposedBridge.log("Error:在/data/app找不到APK文件" + filePath)
                return
            }
        }
        val pathClassLoader = PathClassLoader(filePath, ClassLoader.getSystemClassLoader())
        val aClass = Class.forName(packageName + "." + Main::class.java.simpleName, true, pathClassLoader)
        val aClassMethod = aClass.getMethod("hook", XC_LoadPackage.LoadPackageParam::class.java)
        aClassMethod.invoke(aClass.newInstance(), lpparam)
    }

    fun hook(lpparam: XC_LoadPackage.LoadPackageParam) {
        FuckWeibo().fuckWeibo(lpparam)
    }
}


package com.smilehacker.weiboadblocker

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.XposedHelpers.findAndHookMethod
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.net.URL
import java.util.*

/**
 * Created by kleist on 2016/10/12.
 */
class FuckWeibo : IXposedHookLoadPackage {


    private val DESC_CLASS = "com.sina.weibo.models.MBlogListBaseObject"
    private val HTTP_CLASS = "java.net.HttpURLConnection"

    private var mLastStatus : ArrayList<Any?>? = null
    private var mLastFixedStatus : ArrayList<Any?>? = null

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        //XposedBridge.log("load app: ${lpparam?.packageName}")
        if (lpparam!!.packageName!!.indexOf("com.sina.weibo") < 0) {
            return
        }

        XposedBridge.log("try fuck sina weibo ${lpparam?.packageName}")
        fuckWeibo(lpparam!!)
    }


    fun fuckWeibo(lpparam: XC_LoadPackage.LoadPackageParam) {

        findAndHookMethod(DESC_CLASS, lpparam.classLoader, "getTrends", object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam?) {
                param?.result = Collections.EMPTY_LIST
            }
        })
        findAndHookMethod(DESC_CLASS, lpparam.classLoader, "setTrends", List::class.java, object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam?) {
                param?.result = null
            }
        })
        findAndHookMethod(DESC_CLASS, lpparam.classLoader, "insetTrend", object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam?) {
                param?.result = Collections.EMPTY_LIST
            }
        })

        findAndHookMethod(DESC_CLASS, lpparam.classLoader, "setStatuses", List::class.java, object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam?) {
                val statuses = param!!.args[0] as List<Any?>
                val fixStatus = ArrayList<Any?>()
                statuses.forEach {
                    val value = XposedHelpers.getObjectField(it, "mblogtypename") as String

                    if (!value.isNullOrBlank()) {
                        XposedBridge.log("setStatuses remove ad: $value")
                    } else {
                        fixStatus.add(it)
                    }
                }
                param.args[0] = fixStatus
            }
        })
        findAndHookMethod(DESC_CLASS, lpparam.classLoader, "getStatuses", object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam?) {
                val statuses = param!!.result as ArrayList<Any?>
                if (statuses == mLastStatus) {
                    param.result = mLastFixedStatus
                    return
                }

                val fixStatus = ArrayList<Any?>()
                statuses.forEach {
                    val value = XposedHelpers.getObjectField(it, "mblogtypename") as String

                    if (!value.isNullOrBlank()) {
                        XposedBridge.log("getStatuses remove ad: $value")
                    } else {
                        fixStatus.add(it)
                    }
                }
                mLastFixedStatus = fixStatus
                mLastStatus = statuses
                param.result = fixStatus
            }
        })

        findAndHookMethod("java.net.URL", lpparam.classLoader, "openConnection", object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam?) {
                XposedBridge.log("URL openConnection")
                val url = param!!.thisObject as URL
                if (url.toString().indexOf("sdkapp.mobile.sina.cn") >= 0) {
                    XposedBridge.log("find ad url")
                    param.result = null
                }
            }
        })
    }
}
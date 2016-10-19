package com.smilehacker.weiboadblocker

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
class FuckWeibo {


    private val DESC_CLASS = "com.sina.weibo.models.MBlogListBaseObject"
    private val HTTP_CLASS = "java.net.HttpURLConnection"

    private var mLastStatus : ArrayList<Any?>? = null

    private val BLOCK_HOST_LIST = arrayOf("sdkapp.mobile.sina.cn", "adashx.m.taobao.com", "adashbc.m.taobao.com")


    fun fuckWeibo(lpparam: XC_LoadPackage.LoadPackageParam) {

        XposedBridge.log("weiboADblocker version 3")

        findAndHookMethod(DESC_CLASS, lpparam.classLoader, "setStatuses", List::class.java, object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                XposedBridge.log("setStatuses")
                val statuses = param.args[0] as ArrayList<Any?>

                val iterator = statuses.iterator()
                while (iterator.hasNext()) {
                    val it = iterator.next()
                    val value = XposedHelpers.getObjectField(it, "mblogtypename") as String

                    if (!value.isNullOrBlank()) {
                        if (BuildConfig.DEBUG) {
                            XposedBridge.log("setStatuses remove ad: $value")
                        }
                        iterator.remove()
                    }
                }

                param.args[0] = statuses
            }
        })

        findAndHookMethod(DESC_CLASS, lpparam.classLoader, "getStatuses", object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val statuses = param.result as ArrayList<Any?>

                XposedBridge.log("getStatuses: size=" + statuses.size)

                if (statuses.equals(mLastStatus)) {
                    return
                }

                val iterator = statuses.iterator()
                while (iterator.hasNext()) {
                    val it = iterator.next()
                    val value = XposedHelpers.getObjectField(it, "mblogtypename") as String

                    if (!value.isNullOrBlank()) {
                        if (BuildConfig.DEBUG) {
                            XposedBridge.log("getStatuses remove ad: $value")
                        }
                        iterator.remove()
                    }
                }

                mLastStatus = statuses
                param.result = statuses
            }
        })

        findAndHookMethod("java.net.URL", lpparam.classLoader, "openConnection", object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam?) {
                val url = param!!.thisObject as URL
                BLOCK_HOST_LIST.forEach {
                    if (url.toString().indexOf(it) >= 0) {
                        if (BuildConfig.DEBUG) {
                            XposedBridge.log("block ad url $url")
                        }
                        param.result = null
                    }
                }
            }
        })
    }
}
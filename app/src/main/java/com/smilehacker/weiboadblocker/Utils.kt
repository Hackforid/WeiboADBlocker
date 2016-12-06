package com.smilehacker.weiboadblocker

import android.util.Log
import de.robv.android.xposed.XposedBridge

/**
 * Created by kleist on 2016/12/6.
 */
inline fun xlog(str: ()->String) {
    if (BuildConfig.DEBUG) {
        XposedBridge.log(str())
    }
}

inline fun log(str: ()->String) {
    if (BuildConfig.DEBUG) {
        Log.i("Utils", str())
    }
}

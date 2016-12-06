package com.smilehacker.weiboadblocker

import android.app.Activity
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast



class MainActivity : Activity() {

    val mTvAuthor  by lazy { findViewById(R.id.tv_author) as TextView }
    val mBtnHideLauncher by lazy { findViewById(R.id.btn_hide_launcher) as Button }
    val mBtnWeixin by lazy { findViewById(R.id.btn_weixin) as TextView}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mBtnHideLauncher.setOnClickListener { hideLauncher() }
        mTvAuthor.setOnClickListener { openWeibo() }
        mBtnWeixin.setOnClickListener { showShareDialog() }
    }

    private fun hideLauncher() {
        val dialog = AlertDialog.Builder(this)
                .setTitle("隐藏图标")
                .setMessage("隐藏后您依旧能够通过点击XposedInstaller（框架安装器）里的图标来打开该页面。后续版本会在该页面中放入一些自定义的广告设置，帮您更好的隐藏广告。")
                .setPositiveButton("隐藏", { dialog, which -> disableTokenActivity() })
                .setNegativeButton("取消", {dialog, which ->} )
                .setCancelable(false)
        dialog.create().show()
    }

    private fun disableTokenActivity() {
        val p = packageManager
        val component = ComponentName(this, "com.smilehacker.weiboadblocker.TokenActivity")
        p.setComponentEnabledSetting(component,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP)
        Toast.makeText(this, "隐藏成功", Toast.LENGTH_SHORT).show()
    }

    private fun openWeibo() {
        val intent = Intent(Intent.ACTION_VIEW)
        val uri = Uri.parse("http://weibo.cn/qr/userinfo?uid=1620931185")
        intent.data = uri
        val chooseIntent = Intent.createChooser(intent, "Weibo")
        startActivity(chooseIntent)
    }

    private fun showShareDialog() {
        val dialog = AlertDialog.Builder(this)
                .setTitle("琥珀奇妙万事屋")
                .setMessage("因为微信的限制，在微信中打开文章只能把链接分享过去。\n你可以分享到微信的文件传输助手，然后再观看，就不会打扰其他人了。\n（恶心的微信）")
                .setPositiveButton("微信中浏览", { dialog, which ->
                    run {
                        try {
                            shareByWeixin()
                        } catch (e: Exception) {

                        }

                    }
                })
                .setNegativeButton("浏览器里打开", {dialog, which -> shareByBrowser()} )
                .setCancelable(false)
        dialog.create().show()
    }

    private fun shareByWeixin() {
        val intent = Intent()
        val comp = ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI")
        intent.component = comp
        intent.action = "android.intent.action.SEND";
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, "http://mp.weixin.qq.com/s?__biz=MzA4Nzc2NDAwNw==&mid=2247483804&idx=1&sn=3e89cf9df884f65ce2db56cf07a28deb&chksm=90353cd4a742b5c27ad7f4f8962e6c80477bcbba005fdaa2cad02ce5d1b1bc8bd1d6980da5ec#rd")
        intent.putExtra(Intent.EXTRA_TITLE, "琥珀奇妙万事屋")
        startActivity(intent)
    }

    private fun shareByBrowser() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setData(Uri.parse("http://mp.weixin.qq.com/s?__biz=MzA4Nzc2NDAwNw==&mid=2247483804&idx=1&sn=3e89cf9df884f65ce2db56cf07a28deb&chksm=90353cd4a742b5c27ad7f4f8962e6c80477bcbba005fdaa2cad02ce5d1b1bc8bd1d6980da5ec#rd"))
        intent.putExtra(Intent.EXTRA_TITLE, "琥珀")
        startActivity(intent)
    }
}

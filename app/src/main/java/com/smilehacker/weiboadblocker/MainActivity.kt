package com.smilehacker.weiboadblocker

import android.app.Activity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mBtnHideLauncher.setOnClickListener { hideLauncher() }
        mTvAuthor.setOnClickListener { openWeibo() }
    }

    private fun hideLauncher() {
        val p = packageManager
        p.setComponentEnabledSetting(componentName,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP)
        Toast.makeText(this, "隐藏成功", Toast.LENGTH_SHORT).show()
    }

    private fun openWeibo() {
        val intent = Intent(Intent.ACTION_VIEW);
        val uri = Uri.parse("http://weibo.cn/qr/userinfo?uid=1620931185")
        intent.data = uri
        val chooseIntent = Intent.createChooser(intent, "Weibo")
        startActivity(chooseIntent)
    }
}

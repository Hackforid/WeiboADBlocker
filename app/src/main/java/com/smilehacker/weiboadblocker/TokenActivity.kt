package com.smilehacker.weiboadblocker

import android.app.Activity
import android.content.Intent
import android.os.Bundle

class TokenActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_token)
        log { "com = " + componentName }
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}

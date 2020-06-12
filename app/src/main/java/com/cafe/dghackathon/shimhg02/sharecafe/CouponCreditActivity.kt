package com.cafe.dghackathon.shimhg02.sharecafe

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button


class CouponCreditActivity : Activity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coupon)
        findViewById<Button>(R.id.creditpayco).setOnClickListener{
            val intent = Intent(baseContext, PaycoActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
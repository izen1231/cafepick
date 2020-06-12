package com.cafe.dghackathon.shimhg02.sharecafe


import android.widget.Button
import android.widget.Toast
import com.cafe.dghackathon.shimhg02.dghack.Client
import kotlinx.android.synthetic.main.activity_signup.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



class SignUpActivity : BaseActivity() {

    override var viewId: Int = R.layout.activity_signup
    override var toolbarId: Int? = R.id.toolbar
    override fun onCreate() {


        findViewById<Button>(R.id.sign_btn).setOnClickListener {
            Toast.makeText(this@SignUpActivity, "회원가입 성공", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}
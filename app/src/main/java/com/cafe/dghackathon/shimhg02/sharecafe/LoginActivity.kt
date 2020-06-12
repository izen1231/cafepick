package com.cafe.dghackathon.shimhg02.sharecafe


import android.Manifest
import android.content.Intent
import android.widget.Toast
import com.cafe.dghackathon.shimhg02.dghack.Client
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



class LoginActivity : BaseActivity() {

    override var viewId: Int = R.layout.activity_login
    override var toolbarId: Int? = R.id.toolbar
    override fun onCreate() {
        val permissionlistener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() { }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                Toast.makeText(this@LoginActivity, "권한 거부됨\\n\" ${deniedPermissions.toString()}", Toast.LENGTH_LONG).show()
            }
        }
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("내 위치를 표시하기 위해 장소 권한이 필요합니다")
                .setDeniedMessage("설정] > [권한] 에서 권한을 허용할 수 있습니다.")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .check()

        login_btn.setOnClickListener {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        }
        signup_go.setOnClickListener { startActivity(Intent(this@LoginActivity, SignUpActivity::class.java)) }
    }
}
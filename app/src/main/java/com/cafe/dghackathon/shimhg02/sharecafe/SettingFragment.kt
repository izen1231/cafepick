package com.cafe.dghackathon.shimhg02.dghack

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.cafe.dghackathon.shimhg02.sharecafe.CouponCreditActivity
import com.cafe.dghackathon.shimhg02.sharecafe.PointActivity
import com.cafe.dghackathon.shimhg02.sharecafe.R

class SettingFragment : Fragment(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_setting,
                container,
                false)
        val btn = view.findViewById(R.id.pointbtn) as Button
        btn.setOnClickListener(this)
        return view
    }

    override fun onClick(v: View) {
        val intent = Intent(v.context, PointActivity::class.java)
        startActivity(intent)
    }

}


package com.cafe.dghackathon.shimhg02.sharecafe;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class PaycoActivity extends AppCompatActivity {
    private WebView mwv;//Mobile Web View
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_webview);

        mwv=(WebView)findViewById(R.id.activity_main_webview);

        WebSettings mws=mwv.getSettings();//Mobile Web Setting
        mws.setJavaScriptEnabled(true);//자바스크립트 허용
        mws.setLoadWithOverviewMode(true);//컨텐츠가 웹뷰보다 클 경우 스크린 크기에 맞게 조정

        mwv.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        mwv.loadUrl("https://id.payco.com/login.nhn?serviceProviderCode=PAY&inflowKey=www&userLocale=ko_KR&nextURL=https%3A%2F%2Fwww.payco.com%2FafterLogin.nhn");
    }

    //   추가전에 뒤로가기 이벤트 호출시 홈으로 돌아갔으나, 이젠 일반적인 뒤로가기 기능 활성화
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mwv.canGoBack()) {
                mwv.goBack();
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
package com.synaptik.selfupdatinghybrid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class MainActivity extends AppCompatActivity {

    WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.mWebView = (WebView)findViewById(R.id.webview);

        setupWebView();
    }

    protected void setupWebView() {
        WebView.setWebContentsDebuggingEnabled(true);

        this.mWebView.getSettings().setJavaScriptEnabled(true);
        this.mWebView.setWebViewClient(new AppWebViewClient(this));
        this.mWebView.addJavascriptInterface(new NativeBridge(this), "native");
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.mWebView.loadUrl("http://app/index.html");
    }
}

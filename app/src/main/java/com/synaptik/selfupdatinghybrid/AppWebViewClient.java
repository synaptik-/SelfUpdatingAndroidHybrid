package com.synaptik.selfupdatinghybrid;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.synaptik.selfupdatinghybrid.interceptors.BaseInterceptor;
import com.synaptik.selfupdatinghybrid.interceptors.FileLoaderInterceptor;

import java.util.ArrayList;
import java.util.List;

public class AppWebViewClient extends WebViewClient {
    private static final String TAG = AppWebViewClient.class.getSimpleName();
    private Context mContext;

    private List<BaseInterceptor> interceptors;

    public AppWebViewClient(Context context) {
        this.mContext = context;
        this.interceptors = new ArrayList<>();
        this.interceptors.add(new FileLoaderInterceptor(this.mContext));
    }

    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Log.d(TAG, "shouldOverrideUrlLoading(..., " + url + ")");
        return false;
    }

    @TargetApi(21)
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        return shouldOverrideUrlLoading(view, request.getUrl().toString());
    }

    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        WebResourceResponse result = null;
        Uri uri = Uri.parse(url);
        for (BaseInterceptor interceptor : interceptors) {
            if (interceptor.canHandle(uri)) {
                result = interceptor.handle(uri);
                break;
            }
        }
        return result;
    }

    @TargetApi(21)
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        return shouldInterceptRequest(view, request.getUrl().toString());
    }
}

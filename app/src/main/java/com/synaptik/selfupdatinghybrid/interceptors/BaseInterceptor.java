package com.synaptik.selfupdatinghybrid.interceptors;

import android.content.Context;
import android.net.Uri;
import android.webkit.WebResourceResponse;

public abstract class BaseInterceptor {
    protected Context mContext;

    public BaseInterceptor(Context context) {
        this.mContext = context;
    }

    public abstract boolean canHandle(Uri uri);
    public abstract WebResourceResponse handle(Uri uri);
}

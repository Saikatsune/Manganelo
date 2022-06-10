package net.saikatsune.manganelo;

import android.annotation.SuppressLint;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import net.saikatsune.manganelo.ads.AdBlocker;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AdBlocker.init(this);

        String url = "https://manganato.com/";

        webView = findViewById(R.id.webview);
        webView.setWebViewClient(new myBrowser());
        webView.loadUrl(url);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }

    private class myBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        private Map<String, Boolean> loadedUrls = new HashMap<>();
        @Nullable
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            boolean ad;
            if (!loadedUrls.containsKey(url)) {
                ad = AdBlocker.isAd(url);
                loadedUrls.put(url, ad);
            } else {
                ad = loadedUrls.get(url);
            }
            return ad ? AdBlocker.createEmptyResource() :
                    super.shouldInterceptRequest(view, url);
        }
    }

    private WebResourceResponse getTextWebResource(InputStream data) {
        return new WebResourceResponse("text/plain", "UTF-8", data);
    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
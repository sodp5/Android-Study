package com.example.communicatewebviewexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private WebView wvWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wvWebView = findViewById(R.id.wvWebView);

        wvWebView.setWebChromeClient(new WebChromeClient());
        wvWebView.setWebViewClient(new MyWebClient());

        wvWebView.getSettings().setJavaScriptEnabled(true);

//        wvWebView.addJavascriptInterface(new Object() {
//            @JavascriptInterface
//            public String get(String keyword) {
//                Toast.makeText(MainActivity.this, "call get method " + keyword, Toast.LENGTH_SHORT).show();
//                return "succeed";
//            }
//
//            @JavascriptInterface
//            public String put(String keyword) {
//                Toast.makeText(MainActivity.this, "call put method " + keyword, Toast.LENGTH_SHORT).show();
//                return "succeed";
//            }
//        }, "communicate");

        wvWebView.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public String putData(String keyword, String data) {
                String result = keyword + data;
                return result;
            }
        }, "WIZICE");

        wvWebView.loadUrl("file:///android_asset/www/index.html");
    }

    private class MyWebClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return super.shouldOverrideUrlLoading(view, request);
        }
    }
}

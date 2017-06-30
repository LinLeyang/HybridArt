package com.penta.hybridart;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    WebView webview;
    Button button1;
    Button button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webview = (WebView) findViewById(R.id.webview);
        button1 = (Button) findViewById(R.id.bt_loadUrl);
        button2 = (Button) findViewById(R.id.bt_evaluateJavascript);

        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webview.loadUrl("file:///android_asset/test.html");

        /**
         * native call webView
         */

        //method one -- loadUrl
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webview.loadUrl("javascript:callJSM1()");
            }
        });

        //method two -- evaluateJavascript
        //evaluateJavascript is available on 4.4 or higher
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    webview.evaluateJavascript("javascript:callJSM2()", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            //evaluateJavascript have the return value to execute.
                            Toast.makeText(MainActivity.this, "evaluateJavascript:" + value + value.length(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        /**
         * webView call native
         */
        //method one shouldOverrideUrlLoading
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                System.out.println("shouldOverrideUrlLoading：" + url);
                //view.loadUrl(url);
                return true;
                //return true: you should execute the url,the webView will not care abort it.
                //return false: webView will execute the url.
            }
        });

        //method two onJsAlert
        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
                b.setTitle("Alert");
                b.setMessage(message);
                b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                });
                b.setCancelable(false);
                b.create().show();
                return true;
            }

        });

        //method three addJavascriptInterface
        //version under 4.2 have safety loophole
        webview.addJavascriptInterface(new AndroidtoJs(), "test");//AndroidtoJS object 类对象映射到js的test对象


    }

    public class AndroidtoJs extends Object {

        // define the method what JS want to call
        // JavascriptInterface annotation is necessary
        @JavascriptInterface
        public void hello(String msg) {
            System.out.println("JS调用了Android的hello方法");
        }
    }
}

package com.penta.hybridart;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author penta
 *
 * Tue Jul 4 in 2017
 */

public class BasicInteractionActivity extends AppCompatActivity {

    @BindView(R.id.bt_loadUrl)
    Button btLoadUrl;
    @BindView(R.id.bt_evaluateJavascript)
    Button btEvaluateJavascript;
    @BindView(R.id.webview)
    WebView webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_interaction);
        ButterKnife.bind(this);

        //set common configuration
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        //load test html
        webview.loadUrl("file:///android_asset/test.html");

        //define the interface that webview can call native through it
        defineInterface();

    }

    /**
     * webView call native
     */

    private void defineInterface() {
        //method one shouldOverrideUrlLoading
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("web2native", "shouldOverrideUrlLoading：" + url);
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
                AlertDialog.Builder b = new AlertDialog.Builder(BasicInteractionActivity.this);
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
        webview.addJavascriptInterface(new AndroidtoJs(), "atj");//AndroidtoJS object 类对象映射到js的test对象
    }

    public class AndroidtoJs extends Object {

        // define the method what JS want to call
        // JavascriptInterface annotation is necessary
        @JavascriptInterface
        public void hello(String msg) {
            Log.d("web2native", "JavascriptInterface：" + msg);
            //the method of webview must be called on the same thread
            //so you can't write like following
            //webview.loadUrl("javascript:callJSM1()");
        }
    }

    /**
     * native call webView
     */

    //method one -- loadUrl
    @OnClick(R.id.bt_loadUrl)
    public void loadUrl(){
        webview.loadUrl("javascript:callJSM1()");
    }

    //method two -- evaluateJavascript
    //evaluateJavascript is available on 4.4 or higher
    @OnClick(R.id.bt_evaluateJavascript)
    public void evaluateJavascript(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webview.evaluateJavascript("javascript:callJSM2()", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    //evaluateJavascript have the return value to execute.
                    Toast.makeText(BasicInteractionActivity.this, "evaluateJavascript:" + value + value.length(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}

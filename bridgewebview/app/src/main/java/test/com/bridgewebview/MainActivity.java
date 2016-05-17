package test.com.bridgewebview;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.widget.Button;

import com.cordova.photo.CameraLauncher;
import com.cordova.photo.CameraLauncher.CallbackContext;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.github.lzyzsd.jsbridge.DefaultHandler;
import com.google.gson.Gson;

import test.com.bridgewebview.R;

public class MainActivity extends Activity implements OnClickListener {

    private final String TAG = "MainActivity";

    BridgeWebView webView;

    Button button;

    ValueCallback<Uri> mUploadMessage;

    static class Location {
        String address;
    }

    static class User {
        String name;
        Location location;
        String testStr;
    }

    CameraLauncher mLauncher = new CameraLauncher();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        webView = (BridgeWebView) findViewById(R.id.webView);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(this);
        webView.setDefaultHandler(new DefaultHandler());
        webView.setWebChromeClient(new WebChromeClient() {

            @SuppressWarnings("unused")
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String AcceptType, String capture) {
                this.openFileChooser(uploadMsg);
            }

            @SuppressWarnings("unused")
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String AcceptType) {
                this.openFileChooser(uploadMsg);
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                mUploadMessage = uploadMsg;
                pickFile();
            }
        });

        webView.loadUrl("file:///android_asset/demo.htm");
        webView.registerHandler("submitFromWeb", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                Log.i(TAG, "handler = submitFromWeb, data from web = " + data);
                function.onCallBack("submitFromWeb exe, response data 中文 from Java");
            }

        });

        webView.registerHandler("testFile", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                pickFile();
                function.onCallBack("choose file");
            }

        });


        webView.registerHandler("testCamera", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                takeCamera();
                function.onCallBack("choose file");
            }

        });


        webView.callHandler("functionInJs", new Gson().toJson(getUser()), new CallBackFunction() {
            @Override
            public void onCallBack(String data) {

            }
        });

        webView.send("hello from java");

    }

    private void takeCamera() {
        mLauncher.takeCameraPhoto(this, new CallbackContext() {
            @Override
            public void success(String message) {
                webView.callHandler("setJsImage", message, new CallBackFunction() {

                    @Override
                    public void onCallBack(String data) {
                        // TODO Auto-generated method stub
                        Log.i(TAG, "reponse data from js " + data);
                    }

                });
            }

            @Override
            public void fail(String message) {

            }
        });
    }

    private void pickFile() {

        mLauncher.getLibraryPhoto(this, new CallbackContext() {
            @Override
            public void success(String message) {
                webView.callHandler("setJsImage", message, new CallBackFunction() {

                    @Override
                    public void onCallBack(String data) {
                        // TODO Auto-generated method stub
                        Log.i(TAG, "reponse data from js " + data);
                    }

                });
            }

            @Override
            public void fail(String message) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        mLauncher.onActivityResult(requestCode, resultCode, intent);
    }


    private User getUser(){
        User user = new User();
        Location location = new Location();
        location.address = "SDU";
        user.location = location;
        user.name = "大头鬼";
        return user;
    }

    @Override
    public void onClick(View v) {
        if (button.equals(v)) {
            webView.callHandler("functionInJs", "data from Java", new CallBackFunction() {

                @Override
                public void onCallBack(String data) {
                    // TODO Auto-generated method stub
                    Log.i(TAG, "reponse data from js " + data);
                }

            });
        }

    }

}

package com.zuofa.summer.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zuofa.summer.MainActivity;
import com.zuofa.summer.R;
import com.zuofa.summer.application.BaseApplication;
import com.zuofa.summer.bean.User;
import com.zuofa.summer.utils.MD5Util;
import com.zuofa.summer.utils.ShareUtils;
import com.zuofa.summer.utils.StaticClass;

import okhttp3.Call;

/**
 * Created by 刘祚发 on 2017/1/30.
 */
public class SplashActivity extends AppCompatActivity {

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case StaticClass.HANDLER_SPLASH:
                    if (isFirst()) {//第一次安装跳转到引导页面
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    } else {
                        if ( user != null) {//保存了密码直接跳转至主界面
                            startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        } else {//没保存密码跳转到登陆页面
                            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        }
                    }
                    finish();
                    break;
            }
        }
    };
    private User user;
    private final String[] mPermission = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};

    public static final int REQUEST_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initView();
        grantPermission();
    }

    private void initView() {
        //如果用户不是第一次登陆，且以前登陆成功过，则直接进去主页面
        String name = ShareUtils.getString(this, "name", "");
        String password = ShareUtils.getString(this, "password", "");
        OkHttpUtils
                .post()
                .url(StaticClass.URL + "LoginServlet")
                .addParams("name", name)
                .addParams("password", MD5Util.toMD5(password))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if ("null".equals(response)) {
                            handler.sendEmptyMessageDelayed(StaticClass.HANDLER_SPLASH, 1000);
                        } else {
                            Gson gson = new Gson();
                            user = gson.fromJson(response.trim(), User.class);
                            BaseApplication application = (BaseApplication) getApplication();
                            application.setUser(user);
                            Log.e("splash",user.toString());
                            handler.sendEmptyMessageDelayed(StaticClass.HANDLER_SPLASH, 1000);
                        }
                    }
                });

    }

    private boolean isFirst() {
        boolean isFirst = ShareUtils.getBoolean(this, StaticClass.SHARE_IS_FIRST, true);
        if (isFirst) {
            //是第一次运行
            ShareUtils.putBoolean(this, StaticClass.SHARE_IS_FIRST, false);
            return true;
        } else {
            return false;
        }
    }

    private void grantPermission() {
        if (ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.
                permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.
                permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.
                permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SplashActivity.this, mPermission, REQUEST_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION:
                if (grantResults.length > 2
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(SplashActivity.this, "没有权限程序将出现异常", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}

package com.zuofa.summer.ui;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView mImg_Background;
    private EditText et_name;
    private EditText et_password;
    private CheckBox keep_password;
    private Button login;
    private Button register;
    private TextView tv_register;

    private String name;
    private String password;

    private User user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
    }

    private void initView() {
        et_name = (EditText) findViewById(R.id.et_name);
        et_password = (EditText) findViewById(R.id.et_password);
        keep_password = (CheckBox) findViewById(R.id.keep_password);
        login = (Button) findViewById(R.id.btn_login);
        login.setOnClickListener(this);
        mImg_Background = (ImageView) findViewById(R.id.de_img_backgroud);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation animation = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.translate_anim);
                mImg_Background.startAnimation(animation);
            }
        }, 200);
        tv_register = (TextView) findViewById(R.id.tv_register);
        tv_register.setOnClickListener(this);
        boolean isKeep = ShareUtils.getBoolean(this, "keeppass", false);
        keep_password.setChecked(isKeep);

        if (isKeep) {
            String name = ShareUtils.getString(this, "name", "");
            String password = ShareUtils.getString(this, "password", "");
            et_name.setText(name);
            et_password.setText(password);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                login();
                break;
            case R.id.tv_register:
                register();
                break;
        }
    }

    private void login() {
        name = et_name.getText().toString().trim();
        password = et_password.getText().toString().trim();
        if (!TextUtils.isEmpty(name)) {
            if (!TextUtils.isEmpty(password)) {
                OkHttpUtils
                        .post()
                        .url(StaticClass.URL + "LoginServlet")
                        .addParams("name", name)
                        .addParams("password", MD5Util.toMD5(password))
                        .build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                if ("null".equals(response)) {
                                    Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                                } else {
                                    Gson gson = new Gson();
                                    user = gson.fromJson(response.trim(), User.class);
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    BaseApplication application = (BaseApplication) getApplication();
                                    application.setUser(user);
                                    ShareUtils.putString(LoginActivity.this, "name", et_name.getText().toString().trim());
                                    ShareUtils.putString(LoginActivity.this, "password", et_password.getText().toString().trim());
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });

                //

            } else {
                Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
        }
    }

    private void register() {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }

    //假设我现在输入用户名和密码，但是我不点击登录，而是直接退出了
    @Override
    protected void onDestroy() {
        super.onDestroy();

        //保存状态
        ShareUtils.putBoolean(this, "keeppass", keep_password.isChecked());

        //是否记住密码
        if (keep_password.isChecked()) {
            //记住用户名和密码
            ShareUtils.putString(this, "name", et_name.getText().toString().trim());
            ShareUtils.putString(this, "password", et_password.getText().toString().trim());
        } else {
            ShareUtils.deleShare(this, "name");
            ShareUtils.deleShare(this, "password");
        }
    }


}
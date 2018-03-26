package com.zuofa.summer.ui;
/*
 *  项目名：  Summer 
 *  包名：    com.zuofa.summer.ui
 *  文件名:   RegisterActivity
 *  创建者:   Summers
 *  创建时间: 2017/2/58:21
 *  描述：    TODO
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zuofa.summer.R;
import com.zuofa.summer.bean.User;
import com.zuofa.summer.utils.MD5Util;
import com.zuofa.summer.utils.StaticClass;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import okhttp3.Call;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    private EditText register_name;
    private EditText register_password;
    private RadioGroup mRadioGroup;
    private EditText register_school;
    private EditText register_institute;
    private EditText register_major;
    private Button register_submit;
    private String sex = "男";

    public RegisterActivity() throws UnsupportedEncodingException {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();

    }


    private void initView() {
        register_name = (EditText) findViewById(R.id.register_name);
        register_password = (EditText) findViewById(R.id.register_password);
        mRadioGroup = (RadioGroup) findViewById(R.id.mRadioGroup);
        register_school = (EditText) findViewById(R.id.register_school);
        register_school.setOnClickListener(this);
        register_institute = (EditText) findViewById(R.id.register_institute);
        register_major = (EditText) findViewById(R.id.register_major);
        register_submit = (Button) findViewById(R.id.register_submit);
        register_submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_submit:
                //获取输入框的值
                String name = register_name.getText().toString().trim();
                String password = register_password.getText().toString().trim();
                String school = register_school.getText().toString().trim();
                String institute = register_institute.getText().toString().trim();
                String major = register_major.getText().toString().trim();
                //判断输入框是否为空
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(password) ) {
                    Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(school)) {
                    Toast.makeText(this, "学校不能为空", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(institute)) {
                    Toast.makeText(this, "学院不能为空", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(major)) {
                    Toast.makeText(this, "专业不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    //性别判断
                    mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                            if (checkedId == R.id.rb_boy) {
                                Log.e("summer", "男");
                                sex = "男";
                            } else if (checkedId == R.id.rb_girl) {
                                Log.e("summer", "女");
                                sex = "女";
                            }
                        }
                    });
                    User user = new User();
                    user.setName(name);
                    user.setPassword(MD5Util.toMD5(password));
                    user.setSex(sex);
                    user.setStudentid(school);
                    OkHttpUtils
                            .post()
                            .url(StaticClass.URL + "RegisterServlet")
                            .addParams("json", new Gson().toJson(user))
                            .build()
                            .execute(new StringCallback() {
                                @Override
                                public void onResponse(String response, int id) {
                                    Log.e("summer", response);
                                    try {
                                        if (Integer.parseInt(response) > 0) {
                                            Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            Toast.makeText(RegisterActivity.this, "注册失败,此用户已经存在", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (NumberFormatException e) {
                                        e.printStackTrace();
                                    }

                                }

                                @Override
                                public void onError(Call call, Exception e, int id) {
                                    Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                break;
            case R.id.register_school:
                startActivity(new Intent(RegisterActivity.this,SelectSchoolActivity.class));
                break;
        }
    }
}

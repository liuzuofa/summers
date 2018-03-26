package com.zuofa.summer;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.google.gson.Gson;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zuofa.summer.application.BaseApplication;
import com.zuofa.summer.bean.User;
import com.zuofa.summer.fragment.ChatFragment;
import com.zuofa.summer.fragment.HomeFragment;
import com.zuofa.summer.fragment.InfoFragment;
import com.zuofa.summer.fragment.UserFragment;
import com.zuofa.summer.service.UpLoadService;
import com.zuofa.summer.ui.BaseActivity;
import com.zuofa.summer.ui.SendBlogActivity;
import com.zuofa.summer.utils.StaticClass;

import java.util.ArrayList;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;
import okhttp3.Call;


public class MainActivity extends BaseActivity implements
        BottomNavigationBar.OnTabSelectedListener {

    private String TAG = "summer";
    private static final String token1 = "Qb3fG5+RsJO4gH/wpIaaxcJeP3+qCyuvEqWS0ZAu7I5oVzEogK0yY59uMeievD5PS/MoRzBunfbQvVv288ySOw==";
    private static final String token2 = "ZvOFnVq/e1SkcJgekE75PmzZzlYrI8C/l8ypzInwGZGPMZ/Jv5TlmZpyhKYE+Yv5bdm6VPxM+MBoicsejIVc2g==";
    private BottomNavigationBar mNavigationBar;
    private MenuItem menuItem;

    //定义主界面的四个Fragment
    private InfoFragment mInfoFragment;
    private HomeFragment mHomeFragment;
    private ChatFragment mChatFragment;
    private UserFragment mUserFragment;
    private Fragment currentFragment;
    private FloatingActionButton fabCreate;

    private User user;
    private User rongCloudUser;

    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }


    private void initView() {
        BaseApplication application = (BaseApplication) getApplication();
        user = application.getUser();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("校博");
        setSupportActionBar(toolbar);
        fabCreate = (FloatingActionButton) findViewById(R.id.btnCreate);
        mNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigationbar);
        mNavigationBar.addItem(new BottomNavigationItem(R.drawable.home, "校博"))
                .addItem(new BottomNavigationItem(R.drawable.activity, "消息"))
                .addItem(new BottomNavigationItem(R.drawable.dynamic, "信息"))
                .addItem(new BottomNavigationItem(R.drawable.user, "我"))
                .setFirstSelectedPosition(0)//设置默认选择的item
                .setMode(BottomNavigationBar.MODE_FIXED)
                .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC)
                .initialise();//初始化
        mNavigationBar.setTabSelectedListener(this);
        //融云用户连接
        if (user != null) {
            connectRongServer(user.getToken());
        }
        //容云信息提供者
        RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {
            @Override
            public UserInfo getUserInfo(String s) {
                OkHttpUtils
                        .post()
                        .addParams("method", "getUser")
                        .addParams("name", s)
                        .url(StaticClass.URL + "UserServlet")//
                        .build()//
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                Log.e("UserId is ", response);
                                rongCloudUser = new Gson().fromJson(response, User.class);
                            }
                        });
                if (rongCloudUser != null) {
                    return new UserInfo(rongCloudUser.getName(), rongCloudUser.getNick(), Uri.parse(StaticClass.PROFILE_URL + rongCloudUser.getProfile()));
                }
                Log.e("MainActivity", "UserId is ：" + s);
                return null;
            }
        }, true);

        fabCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendBlogIntent = new Intent(MainActivity.this, SendBlogActivity.class);
                startActivityForResult(sendBlogIntent, 520);
            }
        });
    }

    private void initData() {
        if (mHomeFragment == null) {
            mHomeFragment = HomeFragment.newInstance("HomeFragment");
        }
        if (!mHomeFragment.isAdded()) {
            // 提交事务
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_layout, mHomeFragment).commit();
            // 记录当前Fragment
            currentFragment = mHomeFragment;
        }
    }

    /**
     * 连接融云服务器
     *
     * @param token
     */
    private void connectRongServer(String token) {
        RongIM.connect(token, new RongIMClient.ConnectCallback() {

            @Override
            public void onSuccess(String userId) {
                Toast.makeText(MainActivity.this, "connet server success",
                        Toast.LENGTH_SHORT).show();
                Log.e(TAG, "connect success userid is :" + userId);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                Log.e(TAG,
                        "connect failure errorCode is :" + errorCode.getValue());
            }

            @Override
            public void onTokenIncorrect() {
                Log.e(TAG, "token is error , please check token and appkey ");
            }
        });
    }

    @Override
    public void onTabSelected(int position) {
        switch (position) {
            case 0:
                if (mHomeFragment == null) {
                    mHomeFragment = HomeFragment.newInstance("HomeFragment");
                }
                addOrShowFragment(mHomeFragment);
                fabCreate.setVisibility(View.VISIBLE);
                toolbar.setTitle("校博");
                break;
            case 1:
                if (mChatFragment == null) {
                    mChatFragment = ChatFragment.newInstance("ChatFragment");
                }
                addOrShowFragment(mChatFragment);
                fabCreate.setVisibility(View.GONE);
                toolbar.setTitle("消息");
                break;
            case 2:
                if (mInfoFragment == null) {
                    mInfoFragment = InfoFragment.newInstance("InfoFragment");
                }
                addOrShowFragment(mInfoFragment);
                fabCreate.setVisibility(View.GONE);
                toolbar.setTitle("信息");
                break;
            case 3:
                if (mUserFragment == null) {
                    mUserFragment = UserFragment.newInstance("UserFragment");
                }
                addOrShowFragment(mUserFragment);
                fabCreate.setVisibility(View.GONE);
                toolbar.setTitle("我");
                break;
        }
    }

    @Override
    public void onTabUnselected(int position) {

    }

    @Override
    public void onTabReselected(int position) {

    }

    /**
     * 添加或者显示 fragment
     *
     * @param fragment
     */
    private void addOrShowFragment(Fragment fragment) {
        if (currentFragment == fragment)
            return;
        if (!fragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().hide(currentFragment)
                    .add(R.id.frame_layout, fragment).commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .hide(currentFragment).show(fragment).commit();
        }
        currentFragment = fragment;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 520) {
            if (resultCode == RESULT_OK) {
                String content = data.getStringExtra("content");
                Log.e("Main", content);
                ArrayList<String> mSelectPath = data.getStringArrayListExtra("photo");
                Intent intent = new Intent(this, UpLoadService.class);
                intent.putStringArrayListExtra("photo", mSelectPath);
                intent.putExtra("content", content);
                startService(intent);
            }
        } else if (requestCode == 123) {
            if (resultCode == RESULT_OK) {
                if (null != data) {
                    Bundle bundle = data.getExtras();
                    if (bundle == null) {
                        return;
                    }
                    if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                        String result = bundle.getString(CodeUtils.RESULT_STRING);
                        Toast.makeText(this, "解析结果:" + result, Toast.LENGTH_LONG).show();
                    } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                        Toast.makeText(MainActivity.this, "解析二维码失败", Toast.LENGTH_LONG).show();
                    }
                }

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add, menu);
        menuItem = menu.findItem(R.id.add);
        menuItem.setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add) {
            Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
            startActivityForResult(intent, 123);
        }
        return true;
    }


}

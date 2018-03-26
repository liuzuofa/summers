package com.zuofa.summer.ui;
/*
 *  项目名：  Summer 
 *  包名：    com.zuofa.summer.ui
 *  文件名:   UserInfoActivity
 *  创建者:   Summers
 *  创建时间: 2017/2/815:52
 *  描述：    TODO
 */

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zuofa.summer.R;
import com.zuofa.summer.adapter.MyFragmentPagerAdapter;
import com.zuofa.summer.adapter.MyPagerAdapter;
import com.zuofa.summer.adapter.RecruitListViewAdapter;
import com.zuofa.summer.adapter.WeiboListViewAdapter;
import com.zuofa.summer.bean.MicroBlog;
import com.zuofa.summer.utils.Cheeses;
import com.zuofa.summer.utils.StaticClass;
import com.zuofa.summer.view.MyViewPager;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;

public class UserInfoActivity extends BaseActivity {

    private TabLayout user_info_tabs;
    private ViewPager mViewPager;
    private LayoutInflater mInflater;
    private View view, view1, view2,view3;//页卡视图
    private List<View> mViewList = new ArrayList<>();//页卡视图合集
    private List<String> mTitleList = new ArrayList<>();
    private MyFragmentPagerAdapter mAdapter;

    private ListView weibo_listview;
    private ListView care_listview;
    private ListView fans_listview;

    private WeiboListViewAdapter weiboListViewAdapter;

    private List<MicroBlog> microBlogs;

    private Toolbar toolbar;
    private TextView toolbar_title;
    private ListView user_weibo_listview;
    private String name;
    private CircleImageView user_info_circleView;
    private TextView user_info_nick;
    private TextView user_info_introduction;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                   break;
                case 2:

                    break;
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        initView();
    }

    private void initView() {
        //获得点击头像传过来的name
        name = getIntent().getStringExtra("name");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        user_info_circleView = (CircleImageView) findViewById(R.id.user_info_circleView);
        user_info_nick = (TextView) findViewById(R.id.user_info_nick);
        user_info_introduction = (TextView) findViewById(R.id.user_info_introduction);


        user_info_tabs = (TabLayout) findViewById(R.id.user_info_tabs);
        mViewPager = (ViewPager) findViewById(R.id.user_view_page);

        mInflater = LayoutInflater.from(this);
        view1 = mInflater.inflate(R.layout.fragment_weibo, null);
        view2 = mInflater.inflate(R.layout.fragment_care, null);
        view3 = mInflater.inflate(R.layout.fragment_fans, null);

        //添加页卡视图
        mViewList.add(view1);
        mViewList.add(view2);
        mViewList.add(view3);

        //添加页卡标题
        mTitleList.add("微博");
        mTitleList.add("关注");
        mTitleList.add("粉丝");

        //添加页卡标题
        user_info_tabs.setTabMode(TabLayout.MODE_FIXED);
        user_info_tabs.addTab(user_info_tabs.newTab().setText(mTitleList.get(0)));
        user_info_tabs.addTab(user_info_tabs.newTab().setText(mTitleList.get(1)));
        user_info_tabs.addTab(user_info_tabs.newTab().setText(mTitleList.get(2)));

        MyPagerAdapter mNewsAdapter = new MyPagerAdapter(mViewList, mTitleList);
        mViewPager.setAdapter(mNewsAdapter);//给ViewPage设置适配器
        user_info_tabs.setupWithViewPager(mViewPager);

        weibo_listview = (ListView) view1.findViewById(R.id.weibo_listview);
        care_listview = (ListView) view2.findViewById(R.id.care_listview);
        fans_listview = (ListView) view3.findViewById(R.id.fans_listview);


        weiboListViewAdapter = new WeiboListViewAdapter(this);
        showWeiboInfo();



    }

    private void showWeiboInfo() {
        OkHttpUtils
                .post()
                .addParams("method", "getUserBlog")
                .addParams("name", name)
                .url(StaticClass.URL + "WeiboServlet")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Gson gson = new Gson();
                        microBlogs= gson.fromJson(response,new TypeToken<List<MicroBlog>>(){}.getType());
                        //将微博数据填充到屏幕中
                        weiboListViewAdapter.addData(microBlogs);
                    }
                });
    }

    private void showUserInfo() {
        OkHttpUtils
                .post()
                .addParams("method", "getUser")
                .url(StaticClass.URL + "UserServlet")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                    }

                    @Override
                    public void onResponse(String response, int id) {

                    }

                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}

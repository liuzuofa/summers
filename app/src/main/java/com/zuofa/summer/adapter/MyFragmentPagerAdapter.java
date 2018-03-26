package com.zuofa.summer.adapter;
/*
 *  项目名：  Summer 
 *  包名：    com.zuofa.summer.adapter
 *  文件名:   MyFragmentPagerAdapter
 *  创建者:   Summers
 *  创建时间: 2017/4/1214:22
 *  描述：    TODO
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.zuofa.summer.fragment.BlogFragment;
import com.zuofa.summer.fragment.CareFragment;
import com.zuofa.summer.fragment.FansFragment;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    private BlogFragment mBlogFragment;
    private FansFragment mFansFragment;
    private CareFragment mCareFragment;

    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            if (mBlogFragment == null) {
                mBlogFragment = BlogFragment.newInstance("BlogFragment");
            }
            return mBlogFragment;
        }else if (position == 1) {
            if (mFansFragment == null) {
                mFansFragment = FansFragment.newInstance("FansFragment");
            }
            return mFansFragment;
        }else if (position == 2) {
            if (mCareFragment == null) {
                mCareFragment = CareFragment.newInstance("CareFragment");
            }
            return mCareFragment;
        }else {
            return null;
    }

}

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "微博";
            case 1:
                return "粉丝";
            case 2:
                return "关注";
        }
        return null;
    }
}

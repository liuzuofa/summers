package com.zuofa.summer.fragment;
/*
 *  项目名：  Summer 
 *  包名：    com.zuofa.summer.fragment
 *  文件名:   BlogFragment
 *  创建者:   Summers
 *  创建时间: 2017/4/12 14:26
 *  描述：    TODO
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zuofa.summer.R;

public class BlogFragment extends Fragment {

    private View view;
    public static BlogFragment newInstance(String argument) {
        Bundle bundle = new Bundle();
        bundle.putString("argument", argument);
        BlogFragment fragment = new BlogFragment();
        fragment.setArguments(bundle);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_blog, container, false);
        TextView mFragmentText = (TextView) view.findViewById(R.id.text_view);
        Bundle bundle = getArguments();
        String args = bundle.getString("argument");
        mFragmentText.setText(args);
        return view;
    }
}

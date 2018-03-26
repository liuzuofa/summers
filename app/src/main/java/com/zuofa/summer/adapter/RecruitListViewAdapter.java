package com.zuofa.summer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zuofa.summer.R;

import java.util.List;

/**
 * Created by liuzu on 2017/2/21.
 */

public class RecruitListViewAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private Context mContext;
    private List<String> mTitle;
    private List<String> mUrl;

    public RecruitListViewAdapter(Context context, List<String> title,List<String> url) {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mTitle = title;
        this.mUrl = url;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mUrl.size();
    }

    @Override
    public Object getItem(int position) {
        return mUrl.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_recruit, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.recruit_title = (TextView) convertView.findViewById(R.id.recruit_title);
            viewHolder.recruit_url = (TextView) convertView.findViewById(R.id.recruit_url);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.recruit_title.setText(mTitle.get(position));
        viewHolder.recruit_url.setText(mUrl.get(position));
        return convertView;
    }

    private final class ViewHolder {
        TextView recruit_title;
        TextView recruit_url;
    }

    public void addDate(List<String> title,List<String> url){
        this.mTitle = title;
        this.mUrl = url;
        notifyDataSetChanged();
    }
}

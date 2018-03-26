package com.zuofa.summer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jaeger.ninegridimageview.NineGridImageView;
import com.jaeger.ninegridimageview.NineGridImageViewAdapter;
import com.zuofa.summer.R;
import com.zuofa.summer.bean.MicroBlog;
import com.zuofa.summer.utils.StaticClass;
import com.zuofa.summer.utils.SystemUtils;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by liuzu on 2017/2/21.
 */

public class WeiboListViewAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private Context mContext;
    private List<MicroBlog> mMicroBlogs = new ArrayList<>();

    public WeiboListViewAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mMicroBlogs.size();
    }

    @Override
    public Object getItem(int position) {
        return mMicroBlogs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_weibo, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.weibo_profile = (CircleImageView) convertView.findViewById(R.id.weibo_profile);
            viewHolder.weibo_name = (TextView) convertView.findViewById(R.id.weibo_name);
            viewHolder.weibo_time = (TextView) convertView.findViewById(R.id.weibo_time);
            viewHolder.weibo_phone_type = (TextView) convertView.findViewById(R.id.weibo_phone_type);
            viewHolder.weibo_isCare = (Button) convertView.findViewById(R.id.weibo_isCare);
            viewHolder.weibo_content = (TextView) convertView.findViewById(R.id.weibo_content);
            viewHolder.nineImage_view = (NineGridImageView<String>) convertView.findViewById(R.id.nineImage_view);
            viewHolder.btn_weibo_comment = (LinearLayout) convertView.findViewById(R.id.btn_weibo_comment);
            viewHolder.weibo_comment = (TextView) convertView.findViewById(R.id.weibo_comment);
            viewHolder.weibo_admire_iv = (ImageView) convertView.findViewById(R.id.weibo_admire_iv);
            viewHolder.weibo_admire = (TextView) convertView.findViewById(R.id.weibo_admire);
            viewHolder.nineImage_view.setAdapter(viewHolder.mAdapter);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        MicroBlog microBlog = mMicroBlogs.get(position);
        viewHolder.weibo_profile.setVisibility(View.GONE);
        viewHolder.weibo_name.setText(microBlog.getNick());
        viewHolder.weibo_time.setText(microBlog.getWeibo_date());
        viewHolder.weibo_phone_type.setText("来自 "+ SystemUtils.getDeviceBrand());
        viewHolder.weibo_content.setText(microBlog.getWeibo_content());
        if (microBlog.getWeibo_photo() ==null
                ||microBlog.getWeibo_photo().isEmpty()) {
            viewHolder.nineImage_view.setVisibility(View.GONE);
        }else {
            viewHolder.bind(microBlog.getWeibo_photo());
            viewHolder.nineImage_view.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    private final class ViewHolder {
        CircleImageView weibo_profile;
        TextView weibo_name;
        TextView weibo_time;
        TextView weibo_phone_type;
        Button weibo_isCare;
        TextView weibo_content;
        NineGridImageView nineImage_view;
        //GridView blogGirdView;
        LinearLayout btn_weibo_comment;
        TextView weibo_comment;
        ImageView weibo_admire_iv;
        TextView weibo_admire;

        private NineGridImageViewAdapter<String> mAdapter = new NineGridImageViewAdapter<String>() {
            @Override
            protected void onDisplayImage(Context context, ImageView imageView, String s) {
                Glide.with(context).load(s).placeholder(R.drawable.add_pic)
                        .diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
            }

            @Override
            protected ImageView generateImageView(Context context) {
                return super.generateImageView(context);
            }

            @Override
            protected void onItemImageClick(Context context, ImageView imageView, int index, List<String> list) {
                Toast.makeText(context, "image position is " + index, Toast.LENGTH_SHORT).show();
            }
        };

        public void bind(String list) {
            List<String> stringList = new ArrayList<>();
            String[] ss =list.split(";");
            for (int i = 0; i < ss.length; i++) {
                stringList.add(StaticClass.PHOTO_URL+ss[i]);
            }
            nineImage_view.setImagesData(stringList);
        }
    }

    public void addData(List<MicroBlog> list){
        mMicroBlogs.clear();
        this.mMicroBlogs = list;
        notifyDataSetChanged();
    }
}

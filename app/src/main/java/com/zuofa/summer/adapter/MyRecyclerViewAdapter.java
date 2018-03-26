package com.zuofa.summer.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jaeger.ninegridimageview.NineGridImageView;
import com.jaeger.ninegridimageview.NineGridImageViewAdapter;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zuofa.summer.R;
import com.zuofa.summer.bean.MicroBlog;
import com.zuofa.summer.bean.User;
import com.zuofa.summer.ui.CommentActivity;
import com.zuofa.summer.ui.UserInfoActivity;
import com.zuofa.summer.utils.StaticClass;
import com.zuofa.summer.utils.SystemUtils;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;


public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    //用户头像下载标志位
    boolean status;
    private User user;
    private List<MicroBlog> microBlogsList = new ArrayList<>();
    private String res;



    static class ViewHolder extends RecyclerView.ViewHolder {
        MyGirdAdapter myGirdAdapter;
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

        public ViewHolder(View itemView) {
            super(itemView);
            //weiboView = itemView;
            weibo_profile = (CircleImageView) itemView.findViewById(R.id.weibo_profile);
            weibo_name = (TextView) itemView.findViewById(R.id.weibo_name);
            weibo_time = (TextView) itemView.findViewById(R.id.weibo_time);
            weibo_phone_type = (TextView) itemView.findViewById(R.id.weibo_phone_type);
            weibo_isCare = (Button) itemView.findViewById(R.id.weibo_isCare);
            weibo_content = (TextView) itemView.findViewById(R.id.weibo_content);
            nineImage_view = (NineGridImageView<String>) itemView.findViewById(R.id.nineImage_view);
            btn_weibo_comment = (LinearLayout) itemView.findViewById(R.id.btn_weibo_comment);
            weibo_comment = (TextView) itemView.findViewById(R.id.weibo_comment);
            weibo_admire_iv = (ImageView) itemView.findViewById(R.id.weibo_admire_iv);
            weibo_admire = (TextView) itemView.findViewById(R.id.weibo_admire);
            nineImage_view.setAdapter(mAdapter);

        }
        public void bind(String list) {
            List<String> stringList = new ArrayList<>();
            String[] ss =list.split(";");
            for (int i = 0; i < ss.length; i++) {
                stringList.add(StaticClass.PHOTO_URL+ss[i]);
            }
            nineImage_view.setImagesData(stringList);
        }

    }

    public MyRecyclerViewAdapter(Context context){
        this.mContext = context;
    }


    @Override
    public ViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_weibo,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Glide.with(mContext).load(StaticClass.PROFILE_URL+microBlogsList.get(position).getProfile())
                .error(R.drawable.add_pic).into(holder.weibo_profile);
        holder.weibo_name.setText(microBlogsList.get(position).getNick());
        holder.weibo_time.setText(microBlogsList.get(position).getWeibo_date());
        holder.weibo_phone_type.setText("来自 "+SystemUtils.getDeviceBrand());
        holder.weibo_content.setText(microBlogsList.get(position).getWeibo_content());
        if (microBlogsList.get(position).getWeibo_photo() ==null
                ||microBlogsList.get(position).getWeibo_photo().isEmpty()) {
            holder.nineImage_view.setVisibility(View.GONE);
        }else {
            holder.bind(microBlogsList.get(position).getWeibo_photo());
            holder.nineImage_view.setVisibility(View.VISIBLE);
        }
        holder.btn_weibo_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,CommentActivity.class);
                intent.putExtra("blog",microBlogsList.get(position));
                mContext.startActivity(intent);

            }
        });

        holder.weibo_admire_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OkHttpUtils
                        .post()
                        .addParams("method","updateAdmire")
                        .addParams("weibo_id",Integer.toString(microBlogsList.get(position).getId()))
                        .addParams("name",user.getName())
                        .url(StaticClass.URL+"WeiboServlet")
                        .build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                            }
                            @Override
                            public void onResponse(String response, int id) {
                                res = response;
                                if (Integer.parseInt(response.trim())==-1){
                                    Toast.makeText(mContext,"您已经点赞",Toast.LENGTH_SHORT).show();
                                }else if (Integer.parseInt(response.trim())>0){
                                    holder.weibo_admire.setText(response.trim());
                                }
                            }
                        });
            }
        });

        holder.weibo_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, UserInfoActivity.class);
                intent.putExtra("name",microBlogsList.get(position).getName());
                mContext.startActivity(intent);
            }
        });

        if (microBlogsList.get(position).getAdmire_count() != 0) {
            holder.weibo_admire.setText(Integer.toString(microBlogsList.get(position).getAdmire_count()));
        }
        if (microBlogsList.get(position).getAdmire_count() != 0) {
            holder.weibo_comment.setText(Integer.toString(microBlogsList.get(position).getComment_count()));
        }
    }

    @Override
    public int getItemCount() {
        return microBlogsList.size();
    }


    public void addAllData(List<MicroBlog> dataList) {
        microBlogsList.clear();
        this.microBlogsList.addAll(dataList);
        notifyDataSetChanged();
    }

    public void getUser(User mUser) {
        user = mUser;
    }



}

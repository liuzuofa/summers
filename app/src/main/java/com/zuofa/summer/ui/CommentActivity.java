package com.zuofa.summer.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jaeger.ninegridimageview.NineGridImageView;
import com.jaeger.ninegridimageview.NineGridImageViewAdapter;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zuofa.summer.R;
import com.zuofa.summer.adapter.MyListViewAdapter;
import com.zuofa.summer.application.BaseApplication;
import com.zuofa.summer.bean.Comment;
import com.zuofa.summer.bean.MicroBlog;
import com.zuofa.summer.bean.User;
import com.zuofa.summer.utils.StaticClass;
import com.zuofa.summer.utils.SystemUtils;
import com.zuofa.summer.utils.UtilTools;
import com.zuofa.summer.view.CustomDialog;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;

/**
 * Created by liuzu on 2017/2/21.
 */

public class CommentActivity extends AppCompatActivity implements View.OnClickListener {

    private MicroBlog microBlog;
    private CircleImageView comment_weibo_profile;
    private TextView comment_weibo_name;
    private TextView comment_weibo_time;
    private TextView comment_phone_type;
    private TextView comment_weibo_content;
    private NineGridImageView comment_nineImage_view;
    private ListView listView;
    private MyListViewAdapter adapter;
    private List<Comment> commentList = new ArrayList<>();
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

    //private LinearLayout comment;
    private EditText input_comment;
    private TextView btn_publish_comment;
    private CustomDialog commentDialog;
    private String time;
    private Toolbar toolbar;
    private User user;

    private boolean isWriteComment = false;
    private LayoutInflater inflater;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        initView();
    }

    private void initView() {
        BaseApplication application = (BaseApplication) getApplication();
        user = application.getUser();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = (ListView) findViewById(R.id.list_view);
        listView.addHeaderView(LayoutInflater.from(this).inflate( R.layout.weibo_listview_header, null));

        comment_weibo_profile = (CircleImageView) findViewById(R.id.comment_weibo_profile);
        comment_weibo_name = (TextView) findViewById(R.id.comment_weibo_name);
        comment_weibo_time = (TextView) findViewById(R.id.comment_weibo_time);
        comment_phone_type = (TextView) findViewById(R.id.comment_phone_type);
        comment_weibo_content = (TextView) findViewById(R.id.comment_weibo_content);
        comment_nineImage_view = (NineGridImageView) findViewById(R.id.comment_nineImage_view);
        comment_nineImage_view.setAdapter(mAdapter);
        microBlog = (MicroBlog) getIntent().getSerializableExtra("blog");
        Glide.with(this).load(StaticClass.PROFILE_URL + microBlog.getProfile()).error(R.drawable.add_pic).into(comment_weibo_profile);
        comment_weibo_name.setText(microBlog.getNick());
        comment_weibo_time.setText(microBlog.getWeibo_date());
        comment_phone_type.setText("来自 " + SystemUtils.getDeviceBrand());
        comment_weibo_content.setText(microBlog.getWeibo_content());
        if (microBlog.getWeibo_photo() ==null||microBlog.getWeibo_photo().isEmpty()) {
            comment_nineImage_view.setVisibility(View.GONE);
        }else {
            bind(microBlog.getWeibo_photo());
            comment_nineImage_view.setVisibility(View.VISIBLE);
        }
        OkHttpUtils
                .post()
                .addParams("method", "getComment")
                .addParams("weibo_id", Integer.toString(microBlog.getId()))
                //.addParams("weibo_id", "1")
                .url(StaticClass.URL + "WeiboServlet")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e("response", response);
                        Gson gson = new Gson();
                        commentList = gson.fromJson(response, new TypeToken<List<Comment>>() {
                        }.getType());
                        adapter = new MyListViewAdapter(CommentActivity.this, commentList);
                        listView.setAdapter(adapter);
                    }
                });

        //对话框评论
        /*commentDialog = new CustomDialog(this, 100, 100, R.layout.dialog_comment,
                R.style.ActionSheetDialogStyle, Gravity.BOTTOM, R.style.ActionSheetDialogAnimation);

        //comment = (LinearLayout) findViewById(R.id.comment);
        input_comment = (EditText) commentDialog.findViewById(R.id.input_comment);
        btn_publish_comment = (TextView) commentDialog.findViewById(R.id.btn_publish_comment);
        btn_publish_comment.setOnClickListener(this);
        commentDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                InputMethodManager inputManager = (InputMethodManager) input_comment.getContext().getSystemService(INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(input_comment, 0);
            }
        });
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                input_comment.setText("");
                commentDialog.show();
            }
        });*/
        //直接评论
        input_comment = (EditText) findViewById(R.id.input_comment);
        btn_publish_comment = (TextView) findViewById(R.id.btn_publish_comment);
        input_comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString().trim())){
                    //当评论为空的时候，发送按钮为不可点击状态
                    btn_publish_comment.setTextColor(getResources().getColor(R.color.put_text_color));
                    isWriteComment = false;
                }else {
                    btn_publish_comment.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    isWriteComment = true;
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });
        btn_publish_comment.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_publish_comment:
                if (isWriteComment) {
                    updateComment();
                }
                break;
        }
    }

    private void updateComment() {
        OkHttpUtils
                .post()
                .addParams("method", "updateComment")
                .url(StaticClass.URL + "WeiboServlet")
                .addParams("weibo_id", Integer.toString(microBlog.getId()))
                .addParams("comment_name", user.getName())
                .addParams("comment_content", input_comment.getText().toString().trim())
                .addParams("comment_date", UtilTools.getNowTime())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(CommentActivity.this, "评论失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if (Integer.parseInt(response.trim()) > 0) {
                            Toast.makeText(CommentActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
                            //commentDialog.dismiss();
                            Comment comment = new Comment(user.getName(), user.getProfile(), user.getNick(), time, input_comment.getText().toString().trim());
                            adapter.addDate(comment);
                            input_comment.clearFocus();
                            input_comment.setText("");
                        } else {
                            Toast.makeText(CommentActivity.this, "评论失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public void bind(String list) {
        List<String> stringList = new ArrayList<>();
        String[] ss =list.split(";");
        for (int i = 0; i < ss.length; i++) {
            stringList.add(StaticClass.PHOTO_URL+ss[i]);
        }
        comment_nineImage_view.setImagesData(stringList);
    }

}

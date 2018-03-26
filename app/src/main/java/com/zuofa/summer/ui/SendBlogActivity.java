package com.zuofa.summer.ui;
/*
 *  项目名：  Summer 
 *  包名：    com.zuofa.summer.ui 
 *  文件名:   SendBlogActivity
 *  创建者:   Summers
 *  创建时间: 2017/2/25 18:26 
 *  描述：    发送微博
 */


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zuofa.summer.MainActivity;
import com.zuofa.summer.R;
import com.zuofa.summer.application.BaseApplication;
import com.zuofa.summer.bean.MicroBlog;
import com.zuofa.summer.bean.User;
import com.zuofa.summer.service.UpLoadService;
import com.zuofa.summer.utils.GlideImageLoader;
import com.zuofa.summer.utils.StaticClass;
import com.zuofa.summer.utils.UtilTools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cc.dagger.photopicker.PhotoPicker;
import cc.dagger.photopicker.picker.PhotoFilter;
import okhttp3.Call;


public class SendBlogActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private MenuItem menuItem;
    private EditText send_blogContent;
    private User user;

    private ArrayList<String> mSelectPath =new ArrayList<>();
    private GridView girdView;
    private GirdImageAdapter adapter;
    private String photoPath;
    private int count;
    private String time;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendblog);
        initView();

    }

    private void initView() {
        BaseApplication application = (BaseApplication) getApplication();
        user = application.getUser();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("写微博");
        setSupportActionBar(toolbar);
        send_blogContent = (EditText) findViewById(R.id.send_blogContent);
        PhotoPicker.init(new GlideImageLoader(), null);
        girdView = (GridView) findViewById(R.id.girdView);
        adapter = new GirdImageAdapter(this);
        girdView.setAdapter(adapter);

        girdView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if ((i + 1) == adapter.getCount()) {//当点击的是最后一张照片
                    if (((i + 1) == 9) && (adapter.isMax)) {
                        PhotoPicker.preview()
                                .paths(mSelectPath)
                                .currentItem(i)
                                .start(SendBlogActivity.this);
                    } else {
                        pickImage();
                    }
                } else {
                    PhotoPicker.preview()
                            .paths(mSelectPath)
                            .currentItem(i)
                            .start(SendBlogActivity.this);
                }
            }
        });


    }

    private void pickImage() {
        PhotoPicker.load()
                .filter(PhotoFilter.build().showGif(false).minSize(2 * 1024)) // 照片属性过滤
                .gridColumns(3) // 照片列表显示列数
                .showCamera(true)
                .multi()
                .maxPickSize(9) // 最大选择数
                .selectedPaths(mSelectPath) // 已选择的照片地址
                .start(SendBlogActivity.this); // 从Fragment、Activity中启动
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PhotoPicker.REQUEST_SELECTED) {
            if (resultCode == RESULT_OK) {
                mSelectPath = data.getStringArrayListExtra(PhotoPicker.EXTRA_RESULT);
                adapter.changeDate(mSelectPath);
                StringBuilder sb = new StringBuilder();
                time = UtilTools.getNowTimeString();
                for (int i = 0; i < mSelectPath.size(); i++) {
                    Log.e("mSelectPath", mSelectPath.get(i));
                    sb.append(user.getName());
                    sb.append("/");
                    sb.append(time);
                    sb.append("/");
                    sb.append(Integer.toString(i));
                    sb.append(".JPG");
                    sb.append(";");
                }
                photoPath = sb.toString();
            }
        }
        if (requestCode == PhotoPicker.REQUEST_PREVIEW) {
            if (resultCode == RESULT_OK) {
                mSelectPath = data.getStringArrayListExtra(PhotoPicker.PATHS);
                adapter.changeDate(mSelectPath);
                StringBuilder sb = new StringBuilder();
                time = UtilTools.getNowTimeString();
                for (int i = 0; i < mSelectPath.size(); i++) {
                    sb.append(user.getName());
                    sb.append("/");
                    sb.append(time);
                    sb.append("/");
                    sb.append(Integer.toString(i));
                    sb.append(".JPG");
                    sb.append(";");
                }
                photoPath = sb.toString();
            }
        }
    }

    private void sendBlog() {
        MicroBlog microBlog = new MicroBlog();
        microBlog.setName(user.getName());
        microBlog.setNick(user.getNick());
        microBlog.setProfile(user.getProfile());
        microBlog.setWeibo_content(send_blogContent.getText().toString().trim());
        microBlog.setWeibo_photo(photoPath);
        microBlog.setWeibo_date(UtilTools.getNowTime());
        microBlog.setAdmire_count(0);
        microBlog.setComment_count(0);

        String json = new Gson().toJson(microBlog);
        Log.e("json", json);
        OkHttpUtils
                .post()
                .addParams("method", "writeBlog")
                .addParams("json", json)
                .url(StaticClass.URL + "WeiboServlet")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        uploadImages();
                        //finish();
                    }
                });
    }

    /**
     * 将微博图片上传到服务器中
     */
    private void uploadImages() {
        for (int i = 0; i < mSelectPath.size(); i++) {
            count = i;
            String[] pp = photoPath.split(";");
            count = i;
            OkHttpUtils.post()//
                    .addFile("mFile", pp[i], UtilTools.saveBitmapFile(mSelectPath.get(i), 800))
                    .url(StaticClass.URL + "UploadBlogPhotoServlet")
                    .build()//
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            mSelectPath.remove(count);
                            Toast.makeText(SendBlogActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            if ("success".equals(response)) {
                                Toast.makeText(SendBlogActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                                //finish();
                            }
                        }
                    });
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        menuItem = menu.findItem(R.id.send);
        menuItem.setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.send:
                if (TextUtils.isEmpty(send_blogContent.getText().toString().trim())) {
                    Toast.makeText(this, "微博内容不能为空！", Toast.LENGTH_SHORT).show();
                    break;
                } else {
                    Intent intent = new Intent();
                    //Log.e("mSelectPath",mSelectPath.size()+"");
                    intent.putStringArrayListExtra("photo", mSelectPath);
                    intent.putExtra("content", send_blogContent.getText().toString().trim());
                    setResult(RESULT_OK,intent);
                    finish();
                    break;
                }
        }
        return true;
    }


    public class GirdImageAdapter extends BaseAdapter {
        private List<String> mPath = new ArrayList<>();
        private Context mContext;
        public boolean isMax = false;

        public GirdImageAdapter(Context context) {
            mPath.add("max");
            this.mContext = context;
        }

        public void changeDate(ArrayList<String> path) {
            this.mPath.clear();
            this.mPath.addAll(path);
            if (path.size() != 9) {
                this.mPath.add("max");
                isMax = false;
            } else {
                isMax = true;
            }

            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mPath.size();
        }

        @Override
        public Object getItem(int i) {
            return mPath.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ImageView imageView;
            if (view == null) {
                // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(240, 240));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(10, 10, 10, 10);
            } else {
                imageView = (ImageView) view;
            }
            if ("max".equals(mPath.get(i))) {
                //Log.e("第"+i+"张",mPath.get(i));
                imageView.setImageResource(R.drawable.pick_photo);
            } else {
                //Log.e("第"+i+"张",mPath.get(i));
                Glide.with(mContext).load(mPath.get(i)).into(imageView);
            }
            return imageView;
        }

    }

}

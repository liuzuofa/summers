package com.zuofa.summer.service;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zuofa.summer.application.BaseApplication;
import com.zuofa.summer.bean.MicroBlog;
import com.zuofa.summer.bean.User;
import com.zuofa.summer.ui.SendBlogActivity;
import com.zuofa.summer.utils.StaticClass;
import com.zuofa.summer.utils.UtilTools;

import java.util.ArrayList;

import okhttp3.Call;

public class UpLoadService extends IntentService {
    private User user;
    private StringBuilder photo = new StringBuilder();
    private String stringSb;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public UpLoadService() {
        super("UpLoadService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("uploadService","onCreate");
        BaseApplication application = (BaseApplication)getApplication();
        user = application.getUser();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.e("uploadService",intent.getStringExtra("content"));
        if (intent.getStringArrayListExtra("photo").size() >0){
            uploadImages(intent.getStringArrayListExtra("photo"));
        }
        sendBlog(intent.getStringExtra("content"),photo.toString());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("uploadService","onDestroy");

    }

    private void sendBlog(String content, String photoPath) {
        MicroBlog microBlog = new MicroBlog();
        microBlog.setName(user.getName());
        microBlog.setNick(user.getNick());
        microBlog.setProfile(user.getProfile());
        microBlog.setWeibo_content(content);
        microBlog.setWeibo_photo(photoPath);
        microBlog.setWeibo_date(UtilTools.getNowTime());
        microBlog.setAdmire_count(0);
        microBlog.setComment_count(0);

        String json=new Gson().toJson(microBlog);
        OkHttpUtils
                .post()
                .addParams("method","writeBlog")
                .addParams("json",json)
                .url(StaticClass.URL+"WeiboServlet")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                    }
                    @Override
                    public void onResponse(String response, int id) {
                        //uploadImages();
                        //finish();
                    }
                });
    }

    private void uploadImages(ArrayList<String> mSelectPath){
        for (int i = 0; i < mSelectPath.size(); i++) {
            StringBuilder sb = new StringBuilder();
            sb.append(user.getName());
            sb.append("/");
            sb.append(UtilTools.getNowYear());
            sb.append("/");
            sb.append(UtilTools.getNowMonth());
            sb.append("/");
            sb.append(UtilTools.getNowDay());
            sb.append("/");
            sb.append(UtilTools.getNowHourMinSecond());
            sb.append("-"+i+".jpg");
            sb.append(";");
            stringSb = sb.toString();
            photo.append(stringSb);
            OkHttpUtils.post()//
                    .addFile("mFile",stringSb.substring(0,stringSb.length()-1), UtilTools.saveBitmapFile(mSelectPath.get(i),800))
                    .url(StaticClass.URL+"UploadPhotosServlet")
                    .build()//
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                           // mSelectPath.remove(count);
                            //Toast.makeText(getApplicationContext(), "上传失败", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onResponse(String response, int id) {
                            if ("success".equals(response)) {
                                //Toast.makeText(getApplicationContext(), "上传成功", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }
}

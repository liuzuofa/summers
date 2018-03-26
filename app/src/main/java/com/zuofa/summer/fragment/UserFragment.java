package com.zuofa.summer.fragment;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zuofa.summer.R;
import com.zuofa.summer.application.BaseApplication;
import com.zuofa.summer.bean.User;
import com.zuofa.summer.ui.AboutActivity;
import com.zuofa.summer.ui.ConnectActivity;
import com.zuofa.summer.ui.LoginActivity;
import com.zuofa.summer.ui.InformationAcvtivity;
import com.zuofa.summer.utils.L;
import com.zuofa.summer.utils.StaticClass;
import com.zuofa.summer.view.CustomDialog;

import java.io.File;
import java.io.FileOutputStream;


import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserFragment extends Fragment implements View.OnClickListener {

    private View view;
    private CircleImageView user_photo;
    private TextView user_nick;
    private TextView user_introduction;
    private CustomDialog customDialog;
    private TextView takePhoto;
    private TextView choosePhoto;
    private TextView btn_cancel;

    private LinearLayout connect;
    private LinearLayout about;
    private Button btn_quit;

    private LinearLayout information;
    private SwipeRefreshLayout user_swipe_refresh;
    private User user;

    public static UserFragment newInstance(String argument) {
        Bundle bundle = new Bundle();
        bundle.putString("argument", argument);
        UserFragment fragment = new UserFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_user, container, false);
        initView();
        return view;
    }

    private void initView() {
        BaseApplication application = (BaseApplication) this.getActivity().getApplication();
        user = application.getUser();



        user_photo = (CircleImageView) view.findViewById(R.id.user_photo);
        if (user.getProfile() != null) {
            //showProfile(user.getProfile());
            Glide.with(this).load(StaticClass.PROFILE_URL + user.getProfile()).error(R.drawable.add_pic)
                    .into(user_photo);
        }
        user_photo.setOnClickListener(this);

        user_nick = (TextView) view.findViewById(R.id.user_nick);
        user_nick.setText(user.getNick());

        user_introduction = (TextView) view.findViewById(R.id.user_introduction);
        user_introduction.setText(user.getIntroduction());

        customDialog = new CustomDialog(getActivity(), 100, 100,
                R.layout.dialog_photo, R.style.ActionSheetDialogStyle, Gravity.BOTTOM, R.style.ActionSheetDialogAnimation);
        customDialog.setCancelable(false);

        //dialog初始化
        takePhoto = (TextView) customDialog.findViewById(R.id.takePhoto);
        takePhoto.setOnClickListener(this);
        choosePhoto = (TextView) customDialog.findViewById(R.id.choosePhoto);
        choosePhoto.setOnClickListener(this);
        btn_cancel = (TextView) customDialog.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(this);

        information = (LinearLayout) view.findViewById(R.id.information);
        information.setOnClickListener(this);
        connect = (LinearLayout) view.findViewById(R.id.connect);
        connect.setOnClickListener(this);
        about = (LinearLayout) view.findViewById(R.id.about);
        about.setOnClickListener(this);
        btn_quit = (Button) view.findViewById(R.id.btn_quit);
        btn_quit.setOnClickListener(this);

        user_swipe_refresh = (SwipeRefreshLayout) view.findViewById(R.id.user_swipe_refresh);
        //swipe_refresh.setColorSchemeColors(R.color.colorPrimary);
        user_swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                user_nick.setText(user.getNick());
                user_introduction.setText(user.getIntroduction());
                user_swipe_refresh.setRefreshing(false);
            }
        });

    }

    //显示头像
    private void showProfile(String profile) {
        L.e(profile);
        OkHttpUtils
                .get()//
                .url(StaticClass.URL.substring(0,StaticClass.URL.length()-1) + profile)//
                .build()//
                .execute(new BitmapCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(Bitmap response, int id) {
                        L.e("网络下载的图片");
                        user_photo.setImageBitmap(response);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_photo:
                customDialog.show();
                break;
            case R.id.btn_cancel:
                customDialog.dismiss();
                break;
            case R.id.takePhoto:
                toCamera();
                break;
            case R.id.choosePhoto:
                toPhoto();
                break;
            case R.id.information:
                startActivity(new Intent(getActivity(), InformationAcvtivity.class));
                break;
            case R.id.connect:
                startActivity(new Intent(getActivity(), ConnectActivity.class));
                break;
            case R.id.about:
                startActivity(new Intent(getActivity(), AboutActivity.class));
                break;
            case R.id.btn_quit:
                quit();
                break;
        }

    }


    public static final String PHOTO_IMAGE_FILE_NAME = "fileImg.jpg";
    public static final int CAMERA_REQUEST_CODE = 100;
    public static final int IMAGE_REQUEST_CODE = 101;
    public static final int RESULT_REQUEST_CODE = 102;
    private File tempFile = null;

    //跳转相机
    private void toCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //判断内存卡是否可用，可用的话就进行储存
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(new File(Environment.getExternalStorageDirectory(), PHOTO_IMAGE_FILE_NAME)));
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
        customDialog.dismiss();
    }

    //跳转相册
    private void toPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_REQUEST_CODE);
        customDialog.dismiss();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != getActivity().RESULT_CANCELED) {
            switch (requestCode) {
                //相册数据
                case IMAGE_REQUEST_CODE:
                    startPhotoZoom(data.getData());
                    break;
                //相机数据
                case CAMERA_REQUEST_CODE:
                    tempFile = new File(Environment.getExternalStorageDirectory(), PHOTO_IMAGE_FILE_NAME);
                    startPhotoZoom(Uri.fromFile(tempFile));
                    break;
                case RESULT_REQUEST_CODE:
                    //有可能点击舍弃
                    if (data != null) {
                        //拿到图片设置
                        setImageToView(data);
                        //既然已经设置了图片，我们原先的就应该删除
                        if (tempFile != null) {
                            tempFile.delete();
                        }
                    }
                    break;
            }
        }
    }

    //裁剪
    private void startPhotoZoom(Uri uri) {
        if (uri == null) {
            L.e("uri == null");
            return;
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        //设置裁剪
        intent.putExtra("crop", "true");
        //裁剪宽高比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        //裁剪图片的质量
        intent.putExtra("outputX", 320);
        intent.putExtra("outputY", 320);
        //发送数据
        intent.putExtra("return-data", true);
        startActivityForResult(intent, RESULT_REQUEST_CODE);
    }

    //设置头像
    private void setImageToView(Intent data) {
        Bundle bundle = data.getExtras();
        if (bundle != null) {
            final Bitmap bitmap = bundle.getParcelable("data");
            //将bitmap保存到本地
            File file = new File(Environment.getExternalStorageDirectory(), "profile.png");
            if (file.exists()) {
                file.delete();
            }
            try {
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
                L.e("已经保存");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            OkHttpUtils.post()//
                    .addFile("mFile", user.getName() + ".png", file)//
                    .url(StaticClass.URL+"ImageUploadServlet")
                    .build()//
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            Toast.makeText(getActivity(), "上传失败", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onResponse(String response, int id) {
                            if ("success".equals(response)) {
                                Toast.makeText(getActivity(), "上传成功", Toast.LENGTH_SHORT).show();
                                user_photo.setImageBitmap(bitmap);
                                user.setProfile("/images/user_profile/"+user.getName() + ".png");
                            } else {
                                Toast.makeText(getActivity(), "上传失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }
    }

    private void quit() {
        if (user != null) {
            user = null;
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        }
    }


}

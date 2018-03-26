package com.zuofa.summer.ui;
/*
 *  项目名：  Summer 
 *  包名：    com.zuofa.summer.ui
 *  文件名:   RQcodeActivity
 *  创建者:   Summers
 *  创建时间: 2017/2/1022:27
 *  描述：    生成二维码
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zuofa.summer.R;
import com.zuofa.summer.application.BaseApplication;
import com.zuofa.summer.bean.User;
import com.zuofa.summer.utils.AESUtils;
import com.zuofa.summer.utils.RSAUtils;
import com.zuofa.summer.utils.StaticClass;

import java.util.Hashtable;
import java.util.Random;

import okhttp3.Call;


public class RQcodeActivity extends BaseActivity{

    private ImageView qrCode;
    private Bitmap qrCodeBitmap;
    private Bitmap QRBitmap;
    private User user;
    private int width;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        initView();
    }

    private void initView() {

        BaseApplication application = (BaseApplication) getApplication();
        user = application.getUser();
        qrCode = (ImageView) findViewById(R.id.qrCode);
        //屏幕的宽
        width = getResources().getDisplayMetrics().widthPixels;
        OkHttpUtils
                .get()
                .url(StaticClass.PROFILE_URL + user.getProfile())
                .build()
                .execute(new BitmapCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                    }

                    @Override
                    public void onResponse(Bitmap response, int id) {
                        try {
                            qrCodeBitmap = makeQRImage(RSAUtils.encryptByPublicKey(user.getName()),width /4*3, width/4*3 );
                            Log.e("miwen",RSAUtils.encryptByPublicKey(user.getName()));
                        } catch (WriterException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        qrCode.setImageBitmap(qrCodeBitmap);
                    }
                });


    }
    public static Bitmap makeQRImage(String content, int width, int height)
            throws WriterException {
        Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        // 图像数据转换，使用了矩阵转换
        BitMatrix bitMatrix = new QRCodeWriter().encode(content,
                BarcodeFormat.QR_CODE, width, height, hints);
        int[] pixels = new int[width * height];
        // 按照二维码的算法，逐个生成二维码的图片，两个for循环是图片横列扫描的结果
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (bitMatrix.get(x, y))//范围内为黑色的
                    pixels[y * width + x] = 0xff000000;
                else//其他的地方为白色
                    pixels[y * width + x] = 0xffffffff;
            }
        }
        // 生成二维码图片的格式，使用ARGB_8888
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        //设置像素矩阵的范围
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }


}

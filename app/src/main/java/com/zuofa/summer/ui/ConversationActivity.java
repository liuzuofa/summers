package com.zuofa.summer.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.zuofa.summer.R;

/**
 * Created by AMing on 15/10/27.
 * Company RongCloud
 */
public class ConversationActivity extends BaseActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.conversation_activity);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getIntent().getData().getQueryParameter("title"));
        setSupportActionBar(toolbar);
        Log.e("type", "type is:" + getIntent().getData().getPath());
        Log.e("type", "title is:" + getIntent().getData().getQueryParameter("title"));
    }
}

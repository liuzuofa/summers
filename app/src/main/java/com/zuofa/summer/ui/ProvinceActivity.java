package com.zuofa.summer.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.zuofa.summer.R;

public class ProvinceActivity extends AppCompatActivity {

    private ListView province_listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_province);
        initView();
    }

    private void initView() {
        province_listView = (ListView) findViewById(R.id.province_listView);

    }
}

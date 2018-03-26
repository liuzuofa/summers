package com.zuofa.summer.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.zuofa.summer.R;

public class SchoolActivity extends AppCompatActivity {

    private ListView school_listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school);
        initView();
    }

    private void initView() {
        school_listView = (ListView) findViewById(R.id.school_listView);

    }
}

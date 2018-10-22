package com.room.db;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.room.db.entity.db.PageDetail;
import com.room.db.view_model.PageViewModel;

import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private PageViewModel pageViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tvContent = (TextView) findViewById(R.id.tv_content);
        findViewById(R.id.btn_add).setOnClickListener(this);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        LiveData<List<PageDetail>> allPageInfo = pageViewModel.getAllPageInfo();
        allPageInfo.observe(this, new Observer<List<PageDetail>>() {
            @Override
            public void onChanged(@Nullable List<PageDetail> pageDetails) {
                StringBuffer sb = new StringBuffer();
                for (PageDetail pageDetail : pageDetails) {
                    sb.append(pageDetail.toString()).append("\n");
                }
                tvContent.setText(sb.toString());
            }
        });

    }


    @Override
    public void onClick(View v) {
        pageViewModel.insertPage();
        pageViewModel.getAllPageInfoRx();

    }
}

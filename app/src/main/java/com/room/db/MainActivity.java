package com.room.db;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.room.db.db.AppDatabase;
import com.room.db.db.dao.BookDao;
import com.room.db.entity.db.Book;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
}

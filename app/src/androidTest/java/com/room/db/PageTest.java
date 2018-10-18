package com.room.db;

import android.arch.persistence.room.testing.MigrationTestHelper;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.room.db.db.AppDatabase;
import com.room.db.db.dao.PageDao;
import com.room.db.entity.db.BookInfo;
import com.room.db.entity.db.Page;
import com.room.db.entity.db.PageDetail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class PageTest {
    private static final String TAG = "HB";
    private PageDao pageDao;
    private AppDatabase database;

    @Before
    public void prepare() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        database = AppDatabase.getDatabase(appContext);
        pageDao = database.getPageDao();
    }

    @Test
    public void insertPage() {
        Page page = new Page();
        page.setPageContent("第" + System.currentTimeMillis() + "内容");
        page.setBookId(database.getBookDao().getAllBook().get(0).id);
        Log.d(TAG, "insertPage: index" + pageDao.insert(page));
        query();
    }

    @Test
    public void query() {
        List<Page> pageAll = pageDao.getPageAll();
        pageAll.forEach(item -> Log.d(TAG, item.toString()));
    }

    @Test
    public void queryBookDetail() {
        BookInfo bookAndPageInfo = pageDao.getBookAndPageInfo(database.getBookDao().getAllBook().get(0).id);
        Log.d(TAG, "queryBookDetail: " + bookAndPageInfo.toString());
    }

    @Test
    public void queryPageInfo() {
        List<PageDetail> pageInfo = pageDao.getPageInfo(database.getBookDao().getAllBook().get(0).id);
        pageInfo.forEach(item -> Log.d(TAG, item.toString()));
    }

    @Test
    public void deleteByPageId() {
        query();
        Log.d(TAG, "deleteByPageId: " + pageDao.deletePageByInfo(pageDao.getPageAll().get(0).getId()));
        query();
    }

    @After
    public void close() {
        if (database.isOpen()) {
            database.close();
        }
    }

}

package com.room.db;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.room.db.db.AppDatabase;
import com.room.db.db.dao.BookDao;
import com.room.db.db.dao.PageDao;
import com.room.db.db.dao.TradeDao;
import com.room.db.db.dao.UserDao;
import com.room.db.entity.TradeDetail;
import com.room.db.entity.db.Book;
import com.room.db.entity.db.Page;
import com.room.db.entity.db.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class TradeTest {

    private static final String TAG = "HB";
    private AppDatabase database;
    private UserDao userDao;
    private TradeDao tradeDao;
    private BookDao bookDao;
    private PageDao pageDao;

    @Before
    public void prepare() {
        Context appContext = InstrumentationRegistry.getTargetContext();

        database = AppDatabase.getDatabase(appContext);
        userDao = database.getUserDao();
        tradeDao = database.getTradeDao();
        bookDao = database.getBookDao();
        pageDao = database.getPageDao();
    }


    @Test
    public void insertUser() {
        User user = new User();
        user.name = "大黑2";
        user.birthday = new Date();
        userDao.insert(user);
        queryAllUser();
    }

    public List<User> queryAllUser() {
        List<User> users = userDao.queryAll();
        users.forEach(item -> Log.d(TAG, "queryAllUser: " + item.toString()));
        return users;
    }

    @Test
    public void deleteAll() {
        userDao.deleteAll();
    }

    @Test
    public void payOrder() {
        /*Trade trade = new Trade("tradeId_" + System.currentTimeMillis(), 3, 1, new Date(), 10.0);
        tradeDao.insert(trade);*/
        tradeDao.insert(userDao, bookDao);
        queryAllTradeDetail();
    }

    @Test
    public void queryAllTradeDetail() {
        List<TradeDetail> tradeDetails = tradeDao.queryAllTradeDetail();
        tradeDetails.forEach(item -> Log.d(TAG, "queryAllTradeDetail: " + item.toString()));
    }

    @Test
    public void queryTradeDetailByUserId() {
        TradeDetail tradeDetail = tradeDao.queryTradeByUserId(2);
        Log.d(TAG, "queryTradeDetailByUserId: " + tradeDetail.toString());
    }


    public int insertBook() {

        Book book = new Book("一本小说", 100, "小FaFa");
        Log.d(TAG, "插入数据: " + bookDao.insert(book));

        Page page = new Page();
        page.setPageContent("第" + System.currentTimeMillis() + "内容");
        int bookId = bookDao.getAllBook().get(0).id;
        page.setBookId(bookId);
        Log.d(TAG, "insertPage: index" + pageDao.insert(page));
        return bookId;
    }

    @After
    public void close() {
        if (database.isOpen()) {
            database.close();
        }
    }
}

package com.room.db;

import android.arch.persistence.room.Delete;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.room.db.db.AppDatabase;
import com.room.db.db.dao.BookDao;
import com.room.db.entity.db.Book;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    private static final String TAG = "HB";
    private AppDatabase database;

    @Before
    public void prepare() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        database = AppDatabase.getDatabase(appContext);

    }

    @Test
    public void useAppContext() {

        BookDao bookDao = database.getBookDao();
        Log.d(TAG, "删除所有数据 影响行数: " + bookDao.deleteAll());
        Book book = insertBook(bookDao);
        List<Book> allBook = bookDao.getAllBook();
        for (Book item : allBook) {
            Log.d(TAG, item.toString());
        }
        Log.d(TAG, "删除指定数据: " + bookDao.delete(allBook.get(0)));
        Book book2 = insertBook(bookDao);
        insertBook(bookDao);//测试多条,如果多条则返回第一条
//        Log.d(TAG, "删除指定数据: id为 " + book2.id + "影响行数" + bookDao.deleteById(book2.id));//这样是删不掉的,book2 id =0
        Log.d(TAG, "根据书名查询出的数据: " + bookDao.getBookByBookName("一本小说"));

        List<Book> books = bookDao.getBooksByBookName("一本小说");
        books.forEach(item -> System.out.println("列表中的数据" + item));
    }

    @Test
    public void insertBooks() {
        BookDao bookDao = database.getBookDao();
        List<Book> books = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            books.add(new Book("小说:", 0, "作者呀!"));
        }
        bookDao.insert(books);
        getAllBook();
    }

    @Test
    public void getAllBook() {
        BookDao bookDao = database.getBookDao();
        List<Book> allBook = bookDao.getAllBook();
        allBook.forEach(item -> Log.d(TAG, item.toString()));
    }


    @Test
    public void delete() {
        BookDao bookDao = database.getBookDao();
        getAllBook();
//        Log.d(TAG, "delete: " + bookDao.deleteAll());
        Log.d(TAG, "delete: " + bookDao.deleteByUserId(bookDao.getAllBook().get(0).id));
        getAllBook();
    }

    @NonNull
    private Book insertBook(BookDao bookDao) {
        Book book = new Book("一本小说", 100, "小FaFa");
        Log.d(TAG, "插入数据: " + bookDao.insert(book));
        return book;
    }

    @Test
    public void update() {
        BookDao bookDao = database.getBookDao();
        Book book = bookDao.getAllBook().get(0);
        Log.d(TAG, "update: before" + book);
        book.author = "小二黑";
        bookDao.update(book);
        Log.d(TAG, "update: after" + bookDao.getBookByBookId(book.id));
    }

    @After
    public void close() {
        if (database.isOpen()) {
            database.close();
        }
    }
}

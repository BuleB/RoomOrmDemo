package com.room.db.view_model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.room.db.db.AppDatabase;
import com.room.db.db.dao.BookDao;
import com.room.db.db.dao.PageDao;
import com.room.db.entity.db.Book;
import com.room.db.entity.db.Page;
import com.room.db.entity.db.PageDetail;

import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class PageViewModel extends AndroidViewModel {

    private LiveData<List<PageDetail>> allPageInfo;
    private final PageDao pageDao;
    private int bookId;
    private Disposable subscribe;

    public PageViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getDatabase(application);
        pageDao = database.getPageDao();
        prepare(database);

        allPageInfo = pageDao.getAllPageInfo();
    }

    public void prepare(AppDatabase database) {
        BookDao bookDao = database.getBookDao();
        Book newBook = new Book("一本小说", 10.0, "小FaFa");
        new AsyAddBook(bookDao, new IBookCallBack() {//默认情况下,Room的数据库操作都要在子线程,但是可以在AppDatabase里修改
            @Override
            public void bookId(int id) {
                bookId = id;
            }
        }).execute(newBook);
    }

    public void getAllPageInfoRx() {

        subscribe = pageDao.getAllPageInfoRx().subscribeOn(Schedulers.io()).subscribe(new Consumer<List<PageDetail>>() {
            public static final String TAG = "hb";

            @Override
            public void accept(List<PageDetail> pageDetails) throws Exception {
                Log.d(TAG, "accept: " + pageDetails.toString());

            }
        });

    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (subscribe != null && !subscribe.isDisposed()) {
            subscribe.dispose();
        }
    }

    public void insertPage() {

        new AsyInsertPage(pageDao).execute(bookId);
    }


    public LiveData<List<PageDetail>> getAllPageInfo() {
        return allPageInfo;
    }


    static class AsyInsertPage extends AsyncTask<Integer, Void, Void> {

        private PageDao pageDao;

        public AsyInsertPage(PageDao pageDao) {
            this.pageDao = pageDao;
        }

        @Override
        protected Void doInBackground(Integer... bookIds) {
            try {
                pageDao.insert(getPage(bookIds[0]));
                Thread.sleep(1000);//模拟耗时操作
                pageDao.insert(getPage(bookIds[0]));
                Thread.sleep(1000);//模拟耗时操作
                pageDao.insert(getPage(bookIds[0]));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @NonNull
        private Page getPage(Integer bookId) {
            Page page = new Page();
            page.setPageContent("页面" + System.currentTimeMillis());
            page.setBookId(bookId);
            return page;
        }
    }

    static class AsyAddBook extends AsyncTask<Book, Void, Integer> {

        private BookDao bookDao;
        private IBookCallBack bookCallBack;

        public AsyAddBook(BookDao bookDao, IBookCallBack bookCallBack) {
            this.bookCallBack = bookCallBack;
            this.bookDao = bookDao;
        }

        @Override
        protected Integer doInBackground(Book... books) {
            bookDao.deleteAll();
            bookDao.insert(books[0]);
            Book book = bookDao.getBookByBookName("一本小说");
            if (bookCallBack != null) {
                bookCallBack.bookId(book.id);
            }
            return book.id;
        }
    }

    interface IBookCallBack {
        void bookId(int bookId);
    }
}

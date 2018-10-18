package com.room.db.db;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.room.db.db.dao.BookDao;
import com.room.db.db.dao.PageDao;
import com.room.db.entity.db.Book;
import com.room.db.entity.db.Page;

/**
 * entities 需要映射的类,如果不添加到这里,是不进行映射的
 * version 数据库版本
 * exportSchema 是否导出表结构,默认为true,建议不修改,因为通过导出的文件可以看到数据库更新的历史记录
 */
@Database(entities = {Book.class, Page.class}, version = 1, exportSchema = true)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    /**
     * 定义 访问数据库的类
     * @return
     */
    public abstract BookDao getBookDao();

    public abstract PageDao getPageDao();

    /**
     * 获取数据库实例
     *
     * @param context ctx
     * @return AppDatabase
     */
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "book_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}

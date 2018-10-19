package com.room.db.db;


import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.room.db.db.dao.BookDao;
import com.room.db.db.dao.PageDao;
import com.room.db.db.dao.TradeDao;
import com.room.db.db.dao.UserDao;
import com.room.db.db.utils.DateConverter;
import com.room.db.entity.db.Book;
import com.room.db.entity.db.Page;
import com.room.db.entity.db.Trade;
import com.room.db.entity.db.User;


/**
 * entities 需要映射的类,如果不添加到这里,是不进行映射的
 * version 数据库版本
 * exportSchema 是否导出表结构,默认为true,建议不修改,因为通过导出的文件可以看到数据库更新的历史记录
 */
@Database(entities = {Book.class, Page.class, User.class, Trade.class}, version = 3, exportSchema = true)
@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;
    public static String DATABSE_NAME = "book_database";

    /**
     * 定义 访问数据库的类
     *
     * @return
     */
    public abstract BookDao getBookDao();

    public abstract PageDao getPageDao();

    public abstract UserDao getUserDao();

    public abstract TradeDao getTradeDao();



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
                            AppDatabase.class, DATABSE_NAME)
                            .addMigrations(MIGRATION1_2)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final String TAG = "hb";
    /**
     * 增加了user表
     */
    public static Migration MIGRATION1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            Log.d(TAG, "migrate: " + database.getVersion());
            database.execSQL("CREATE TABLE IF NOT EXISTS User (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT)");
        }
    };

    public static Migration MIGRATION2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            Log.d(TAG, "migrate: " + database.getVersion());
            database.execSQL("CREATE TABLE IF NOT EXISTS Trade (`id` TEXT NOT NULL, `user_id` INTEGER NOT NULL, `book_id` INTEGER NOT NULL, `trade_time` INTEGER, `trade_price` REAL NOT NULL, PRIMARY KEY(`id`))");
            database.execSQL("alter table User add column birthday INTEGER");
        }
    };

}

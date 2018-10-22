package com.room.db.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.room.db.entity.TradeDetail;
import com.room.db.entity.db.Book;
import com.room.db.entity.db.Trade;
import com.room.db.entity.db.User;

import java.util.Date;
import java.util.List;

@Dao
public abstract class TradeDao {

    @Insert(onConflict = OnConflictStrategy.ROLLBACK)//插入失败回滚
    public abstract void insert(Trade trade);

    /**
     * 事务操作,在这里面只要抛出异常,事务就取消
     * @param userDao
     * @param bookDao
     */
    @Transaction()
    public void insert(UserDao userDao, BookDao bookDao) {
        User user = userDao.queryAll().get(0);
        Book book = bookDao.getAllBook().get(0);
        insert(new Trade("tradeId_" + System.currentTimeMillis(), user.id, book.id, new Date(), 100.0));
    }

    /**
     * 根据用户id获取对应的订单
     *
     * @param userId
     * @return
     */
    @Query("select Trade.id ,User.name as user_name ,Book.book_name ,trade_price,trade_time from Trade" +
            " inner join User on Trade.user_id = User.id" +
            " inner join Book on Trade.book_id = Book.id " +
            "where User.id =:userId")
    public abstract TradeDetail queryTradeByUserId(int userId);

    /**
     * 获取所有的订单信息,倒叙
     *
     * @return
     */
    @Query("select Trade.id ,User.name as user_name ,Book.book_name ,trade_price,trade_time from Trade" +
            " inner join User on Trade.user_id = User.id" +
            " inner join Book on Trade.book_id = Book.id order by trade_time desc")
    public abstract List<TradeDetail> queryAllTradeDetail();

}

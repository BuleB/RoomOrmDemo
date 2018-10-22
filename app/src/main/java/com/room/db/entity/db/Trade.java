package com.room.db.entity.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.room.db.db.utils.DateConverter;

import java.util.Date;

/**
 * 购买订单
 */
@Entity
@TypeConverters({DateConverter.class})
public class Trade {

    public Trade(@NonNull String id, int userId, int bookId, Date tradeTime, double tradePrice) {
        this.id = id;
        this.userId = userId;
        this.bookId = bookId;
        this.tradeTime = tradeTime;
        this.tradePrice = tradePrice;
    }

    @PrimaryKey
    @NonNull
    public String id;

    @ColumnInfo(name = "user_id")
    public int userId;

    @ColumnInfo(name = "book_id")
    public int bookId;

    @ColumnInfo(name = "trade_time")
    public Date tradeTime;

    @ColumnInfo(name = "trade_price")
    public double tradePrice;


    @Override
    public String toString() {
        return "Trade{" +
                "id='" + id + '\'' +
                ", userId=" + userId +
                ", bookId=" + bookId +
                ", tradeTime=" + tradeTime +
                ", tradePrice=" + tradePrice +
                '}';
    }
}

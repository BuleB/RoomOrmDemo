package com.room.db.entity;

import android.arch.persistence.room.ColumnInfo;

import java.util.Date;

public class TradeDetail {

    //订单id
    public String id;
    //用户姓名
    @ColumnInfo(name = "user_name")
    public String userName;
    //书本名
    @ColumnInfo(name = "book_name")
    public String bookName;
    //购买价格
    @ColumnInfo(name = "trade_price")
    public double tradePrice;
    //订单时间
    @ColumnInfo(name = "trade_time")
    public Date tradeTime;

    @Override
    public String toString() {
        return "TradeDetail{" +
                "id='" + id + '\'' +
                ", userName='" + userName + '\'' +
                ", bookName='" + bookName + '\'' +
                ", tradePrice=" + tradePrice +
                ", tradeTime=" + tradeTime +
                '}';
    }
}

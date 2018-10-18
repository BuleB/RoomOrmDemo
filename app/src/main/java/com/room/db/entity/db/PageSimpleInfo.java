package com.room.db.entity.db;

import android.arch.persistence.room.ColumnInfo;

public class PageSimpleInfo {
    public int id;//如果字段为私有的就需要有get和set方法

    @ColumnInfo(name = "book_id")
    public int bookId;

    @Override
    public String toString() {
        return "PageSimpleInfo{" +
                "id=" + id +
                ", bookId=" + bookId +
                '}';
    }
}

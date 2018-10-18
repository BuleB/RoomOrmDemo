package com.room.db.entity.db;

import android.arch.persistence.room.ColumnInfo;


public class PageDetail {
    public int id;//如果字段为私有的就需要有get和set方法

    @ColumnInfo(name = "book_id")
    public int bookId;

    @ColumnInfo(name = "page_content")
    public String pageContent;

    @ColumnInfo(name = "book_name")
    public String bookName;


    @Override
    public String toString() {
        return "PageDetail{" +
                "id=" + id +
                ", bookId=" + bookId +
                ", pageContent='" + pageContent + '\'' +
                ", bookName='" + bookName + '\'' +
                '}';
    }
}

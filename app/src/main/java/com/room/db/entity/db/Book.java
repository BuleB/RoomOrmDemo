package com.room.db.entity.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Objects;

/**
 * 书的信息
 */
@Entity//标记实体为关系映射的实体,映射出数据库
public class Book {

    @Ignore
    public Book(String bookName, double price, String author) {
        this.bookName = bookName;
        this.price = price;
        this.author = author;
    }

    public Book() {
    }

    @PrimaryKey(autoGenerate = true)//PrimaryKey 设置主键,是否自动增长
    public int id;

    @ColumnInfo(name = "book_name")//如果不指定列名,默认以bookName为列名
    public String bookName;

    public double price;

    public String author;


    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", bookName='" + bookName + '\'' +
                ", price=" + price +
                ", author='" + author + '\'' +
                '}';
    }
}

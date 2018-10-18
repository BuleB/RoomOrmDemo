package com.room.db.entity.db;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import java.util.List;

public class BookInfo {

    /**
     * 这个方法叫嵌入，你可以想象这个include标签，把字段分解到BookInfo实体里，然后查询之后自动装箱成Book实体
     * public int id;
     * public String bookName;
     * public double price;
     * public String author;
     */
    @Embedded
    public Book book;

    /**
     * 这个操作为了显示标注 表的关系
     * entity 没写的情况下 默认推测entity=返回值entity,也可以显示指定，但必须是个映射实体,
     * entityColumn 关联的字段 parentColumn 父表的关联字段，相当于 page.book_id = book.id
     * projection
     */
    @Relation(parentColumn = "id", entityColumn = "book_id")
    public List<Page> pages;

    /**
     * 这个就要显示指定entity 因为返回值不是个映射实体，
     * 而且 查出的是Page里的字段，所以PageSimple字段要 < Page 字段，
     * projection 从查出的结果中抽出哪几个字段,默认从返回值实体的字段推测
     * 这里@Relation(entity = Page.class, parentColumn = "id", entityColumn = "book_id", projection = {"id", "book_id"})
     * = @Relation(entity = Page.class, parentColumn = "id", entityColumn = "book_id"})
     */
    @Relation(entity = Page.class, parentColumn = "id", entityColumn = "book_id", projection = {"id", "book_id"})
    public List<PageSimpleInfo> pageDetails;


    @Override
    public String toString() {
        return "BookInfo{" +
                "book=" + book +
                ", pages=" + pages +
                ", pageDetails=" + pageDetails +
                '}';
    }
}

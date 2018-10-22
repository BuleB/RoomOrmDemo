package com.room.db.entity.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ColumnInfo.UNDEFINED;
import static android.arch.persistence.room.ColumnInfo.UNSPECIFIED;

/**
 * 书对应的页面信息
 * tableName 表名,默认类名
 * foreignKeys 外键声明,ForeignKey entity 外键对应的实体, parentColumns 外键实体对应的 列名,一般为主键,childColumns 本实体对应的列,
 * 父类 onUpdate 更新的时候行为,onDelete 删除时的行为
 * inheritSuperIndices 是否允许从父类中继承索引
 * indices 设置索引
 */
@Entity(
        foreignKeys = {@ForeignKey(entity = Book.class, parentColumns = "id", childColumns = "book_id", onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE)},
        inheritSuperIndices = false,
        indices = {@Index(value = "book_id")}
)
public class Page {
    @PrimaryKey(autoGenerate = true)
    private int id;//如果字段为私有的就需要有get和set方法

    //ColumnInfo
    // name : 映射的实际列名,
    // typeAffinity:实际类型 UNDEFINED 根据变量类型解析,还有其他类型可以指定,TEXT,INTEGER,REAL,BLOB
    // index 是否建立索引,
    // collate 指定sql查询此字段的时候排序规则,UNSPECIFIED默认为 BINARY大小写敏感,NOCASE,RTRIM,LOCALIZED,UNICODE,
    @ColumnInfo(name = "book_id", typeAffinity = UNDEFINED, index = false, collate = UNSPECIFIED)
    private int bookId;

    @ColumnInfo(name = "page_content")//因为sqlLite不区分大小写,所以使用_来区别单词,否则显示的就是pagecontent为表中的列名
    private String pageContent;

    @Ignore//该字段不加入数据库中
    @ColumnInfo(name = "book_name")
    public String bookName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getPageContent() {
        return pageContent;
    }

    public void setPageContent(String pageContent) {
        this.pageContent = pageContent;
    }


    @Override
    public String toString() {
        return "Page{" +
                "id=" + id +
                ", bookId=" + bookId +
                ", pageContent='" + pageContent + '\'' +
                ", bookName='" + bookName + '\'' +
                '}';
    }
}

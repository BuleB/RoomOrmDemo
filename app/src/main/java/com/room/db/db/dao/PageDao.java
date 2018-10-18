package com.room.db.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.room.db.entity.db.BookInfo;
import com.room.db.entity.db.Page;
import com.room.db.entity.db.PageDetail;

import java.util.List;

@Dao
public interface PageDao {

    @Insert
    public long insert(Page page);

    @Transaction //因为有两个事务，一个是 查书，一个是查书对应的Page,把它们合成一个事务，不加也没事，只不过有警告
    @Query("select * from book where id = :bookId")
    public BookInfo getBookAndPageInfo(int bookId);

    @Query("select * from Page")
    public List<Page> getPageAll();

    /**
     * 联表查询详细的数据
     *
     * @return
     */
    @Query("select Page.id ,Page.book_id,Page.page_content,Book.book_name from Page " +
            "inner join Book on Page.book_id = Book.id" +
            " where Book.id = :bookId")
    public List<PageDetail> getPageInfo(int bookId);
//    public List<Page> getPageInfo(int bookId);最初 想用这个Page实体去接，但是最后发现book_name ignore之后，Room的代码生成器，就会跳过这个字段，导致无法映射这个字段

    @Query("delete from Page where id = :pageId")
    public int deletePageByInfo(int pageId);

}

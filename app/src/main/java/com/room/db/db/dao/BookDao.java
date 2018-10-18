package com.room.db.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.room.db.entity.db.Book;


import java.util.List;

/**
 * dao 标记的是一个接口或者是抽象类,定义操作数据库表的行为
 */
@Dao
public interface BookDao {

    /**
     * 插入数据
     *
     * @param book 要插入的数据,必须是映射的实体或者映射实体的集合
     * @return 返回插入了第几行
     */
    @Insert()
    public long insert(Book book);

    /**
     * 增加多条数据
     *
     * @param books
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public long[] insert(List<Book> books);

    /**
     * 删除指定数据
     *
     * @param book 要删除的对象
     * @return 返回影响的行
     */
    @Delete
    public int delete(Book book);

    /**
     * 根据条件删除指定数据
     *
     * @param bookId
     * @return
     */
    @Query("delete from book where id = :bookId")
    public int deleteByUserId(int bookId);

    @Query("delete from book")
    public int deleteAll();

    @Update
    public void update(Book book);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public void update(List<Book> books);

    @Query("select * from Book")
    public List<Book> getAllBook();


    @Query("select * from book where id = :bookId")
    public Book getBookByBookId(int bookId);

    /**
     * 根据条件查询
     *
     * @param bookName
     * @return
     */
    @Query("select * from book where book_name=:bookName")
    public Book getBookByBookName(String bookName);

    /**
     * 根据条件查询 返回多条数据
     *
     * @param bookName
     * @return
     */
    @Query("select * from book where book_name=:bookName")
    public List<Book> getBooksByBookName(String bookName);


}

package com.room.db.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.room.db.entity.db.User;

import java.util.List;

@Dao
public interface UserDao {

    @Insert()
    public void insert(User user);

    @Query("delete from User")
    public void deleteAll();

    @Query("select * from User")
    public List<User> queryAll();
}

package com.room.db.entity.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

/**
 * 用户表
 */
@Entity
public class User {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;

    public Date birthday;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", birthday=" + birthday +
                '}';
    }
}

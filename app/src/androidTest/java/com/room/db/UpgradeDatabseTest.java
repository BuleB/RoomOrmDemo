package com.room.db;

import android.arch.persistence.db.framework.FrameworkSQLiteOpenHelperFactory;
import android.arch.persistence.room.testing.MigrationTestHelper;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.room.db.db.AppDatabase;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static com.room.db.db.AppDatabase.MIGRATION1_2;
import static com.room.db.db.AppDatabase.MIGRATION2_3;
import static com.room.db.db.AppDatabase.MIGRATION3_4;

@RunWith(AndroidJUnit4.class)
public class UpgradeDatabseTest {

    @Test
    public void prepare() throws IOException {

        MigrationTestHelper migrationTestHelper = new MigrationTestHelper(InstrumentationRegistry.getInstrumentation(), AppDatabase.class.getCanonicalName(), new FrameworkSQLiteOpenHelperFactory());
        migrationTestHelper.createDatabase(AppDatabase.DATABSE_NAME, 2);//创建版本库1的版本,必要条件

        migrationTestHelper.runMigrationsAndValidate(AppDatabase.DATABSE_NAME, 3, false, MIGRATION1_2, MIGRATION2_3,MIGRATION3_4);
    }
}

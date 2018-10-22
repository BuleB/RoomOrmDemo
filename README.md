### Room

Room 是Android提供的操作数据库的高Api架构,避免了直接使用SQLite的一些重复代码和麻烦,而且属于Android Architecture Components,如果你还在用着SQLite可以尝试使用Room.使用annotationProcessor来自动生成代码.

- 代码侵入很小,很容易从SQLite过度到Room.
- 提供数据库升级测试,提供sql编译时校验;
- 灵活的返回对象,支持LiveData,RxJava.

Room 主要由三个部分

- Database : 数据库持有者,使用@Database来标识,用来连接Entity,DAO,配置数据库的一些功能;
- Entity :用来映射表结构的实体;
- DAO : 包含操作数据的方法

![来源出自Android Develpers](https://developer.android.com/images/training/data-storage/room_architecture.png)

上面是三者的关系,只有在Database中配置了的**Entity**和**Dao**才会被编译进程识别,生成对应的代码

来看看配置完成后项目操作数据库的所有代码,这里我直接拿了官方代码的例子

AppDatabase.java
```
@Database(entities = {User.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
}
```
UserDao.java
```
@Dao
public interface UserDao {
    @Query("SELECT * FROM user")
    List<User> getAll();

    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
    List<User> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM user WHERE first_name LIKE :first AND "
           + "last_name LIKE :last LIMIT 1")
    User findByName(String first, String last);

    @Insert
    void insertAll(User... users);

    @Delete
    void delete(User user);
}
```
User.java
```
public class User {
    @PrimaryKey
    private int uid;

    @ColumnInfo(name = "first_name")
    private String firstName;

    @ColumnInfo(name = "last_name")
    private String lastName;

    // Getters and setters are ignored for brevity,
    // but they're required for Room to work.
}
```
没有对比就没有伤害,这是用Sqlte直接写的,很难维护,Room一行,SQLite无数行,修改的时候要了人的老命
```
 public int update(MicroSaidBean.DatasBean ad) {
        if (ad.getPubtime() == null || (0 == ad.getPubtime())) {
            return -1;
        }
        String[] updateFields = ad.getFields();
        String[] updateValues = ad.getValues();
        return sqliteUtil.update(TableUtil.MicroSaid.TABLE_NAME, updateFields,
                updateValues, TableUtil.MicroSaid.MICROSAID_PUBTIME + "=?",
                new String[]{ad.getPubtime().toString()});
    }


String sql = "select * from " + TableUtil.MicroSaid.TABLE_NAME + " where " + TableUtil.MicroSaid.MICROSAID_PUBTIME + " = " + pubtime;
String[][] datas = sqliteUtil.executeSelectSql(sql, null);

MicroSaidBean.DatasBean datasBean = new MicroSaidBean.DatasBean();

if (!"".equals(datas[0][1])) {
    datasBean.setPubtime(Long.valueOf(datas[0][1]));
    }
if (!"".equals(datas[0][2])) {
    datasBean.setContent(datas[0][2]);
}

```
当然上面仅是一些基础功能,大致介绍了Room的结构,下面正式开始Room的学习.

首先添加依赖
```
    def room_version = "1.1.1"
    implementation "android.arch.persistence.room:runtime:$room_version"
    annotationProcessor "android.arch.persistence.room:compiler:$room_version" // use kapt for Kotlin

```

### Entity

和其它关系映射数据库一样,首先定义表结构,也就是Entity.制定表结构

```
/**
 * 书对应的页面信息
 * tableName 表名,默认类名
 * foreignKeys 外键声明,ForeignKey entity 外键对应的实体, parentColumns 父实体对应的 列名,一般为主键,childColumns 本实体对应的列,
 * 父类 onUpdate 更新的时候行为, onDelete 删除时的行为
 * inheritSuperIndices 是否允许从父类中继承索引
 * indices 设置索引 Index value="" 指定哪个列为索引
 */
@Entity(
        foreignKeys = {@ForeignKey(entity = Book.class, parentColumns = "id", childColumns = "book_id", onUpdate = ForeignKey.NO_ACTION, onDelete = ForeignKey.NO_ACTION)},
        inheritSuperIndices = false,
        indices = {@Index(value = "book_id")}
)
public class Page {
    //PrimaryKey 设置主键,是否自动增长
    @PrimaryKey(autoGenerate = true)
    private int id;//如果字段为私有的就需要有get和set方法

    //ColumnInfo
    // name : 映射的实际列名,因为sqlLite不区分大小写,所以使用_来区别单词,否则显示的就是bookid为表中的列名
    // typeAffinity:实际类型 UNDEFINED 根据变量类型解析,还有其他类型可以指定,TEXT,INTEGER,REAL,BLOB
    // index 是否建立索引,
    // collate 指定sql查询此字段的时候排序规则,UNSPECIFIED默认为 BINARY大小写敏感,NOCASE,RTRIM,LOCALIZED,UNICODE,
    @ColumnInfo(name = "book_id", typeAffinity = UNDEFINED, index = false, collate = UNSPECIFIED)
    private int bookId;

    @ColumnInfo(name = "page_content")//
    private String pageContent;

    @Ignore//该字段不加入数据库中
    @ColumnInfo(name = "book_name")
    public String bookName;
}
```
上面的代码，主要介绍了，关于Entity的主要代码，以及所有的注解Api,上面对于表关系的描述，其实可以不加，但是一个好的表关系，能够更好的维护表的数据;
补充一下onUpdate 和 onDelete 的参数
- NO_ACTION 什么也不做
- RESTRICT 约束模式,就拿上面的例子简单来说,Book的主键 id  是 Page 的 book_id外键,那么他们俩是通过 id 和book_id进行约束的,在Page的book_id外键存在的时候不允许删除Book的id和修改Book的id,因为一旦修改成功,那么Page就乱掉了,无法形成对应关系,就成了脏数据
- SET_DEFAULT,SET_NULL 两个做的事情是一样的,一个设置null一个设置默认值,举个列子,你现在删除了Book id=2的数据,那么Page 的 book_id=2 的这条数据的book_id 是给他设置 null 或者默认值,因为之前的id没了
- CASCADE 关联默认,这个意识是,你删了Book id=2 的数据,对应的Page book_id的数据也会被删除,更新了id那么对应的Page book_id字段也会更新

那么到这里Entity的所有内容已经说完了.

### Dao

这里的Dao是定义数据处理的地方,就像Retrofit的retrofit.create(GitHubService.class);里的Services,用户定义要查询的语句,返回的内容,然后返回用户想要的数据,Room帮用户在编译时生成对应的代码.
```
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

    /**
     * 根据条件查询
     *
     * @param bookName
     * @return
     */
    @Query("select * from book where id = :bookId")
    public Book getBookByBookId(int bookId);

    /**
     * 根据条件查询 返回多条数据
     *
     * @param bookName
     * @return
     */
    @Query("select * from book where book_name=:bookName")
    public List<Book> getBooksByBookName(String bookName);

}
```
上面的例子显示了基本的增删改查,可以发现Room会根据返回值自动推测结果,例如删除的时候你可以定义void,那么久没有返回值,当你改为int时,则会返回对应的行数.而且在写Sql语句的时候as会提示你使用哪个字段,例如book,当我打出b的时候回提示book,防止拼错等低级操作,如果想动态填充数据使用:xxx

然后解释一下 Insert,Update的用法.

有时插入数据和更新数据会产生冲突,所以就有了冲突之后要怎么解决,SQLite对于事务冲突定义了5个方案
OnConflictStrategy
- REPLACE,见名知意,替换,违反的记录被删除，以新记录代替之
- ignore	违反的记录保持原貌，其它记录继续执行
- fail	终止命令，违反之前执行的操作得到保存
- abort	终止命令，恢复违反之前执行的修改
- rollback	终止命令和事务，回滚整个事务
事务解决由上到下越来越严谨

好了上面是基本操作,下面搞点高大上的,因为关系型数据库避免不了的还有一对一,一对多,多对多的关系
一本书有很多页,典型一对多.
表结构
Book.java
```
@Entity//标记实体为关系映射的实体,映射出数据
public class Book {

    @Ignore//因为Room APT在生成代码的时候需要构造方法,因为这个类有两个构造方法,你要选择其中一个忽略,否则有个错不至死的警告
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
}

```
Book 的id 和 Page 的 book_id 约束
```
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
```
PageDetail.java
```
public class PageDetail {
    public int id;//如果字段为私有的就需要有get和set方法

    @ColumnInfo(name = "book_id")
    public int bookId;

    @ColumnInfo(name = "page_content")
    public String pageContent;

    @ColumnInfo(name = "book_name")
    public String bookName;

}

```
#### 数据映射

不仅实体可以映射出表结构,查出的数据也可以通过映射到实体

这里在 public List<PageDetail> getPageInfo(int bookId);中定义了一个返回的实体PageDetail,如果表中查询出的字段和实体不匹配可以使用ColumnInfo来指定对应的关系!

#### 事务

引入事务的概念Transaction,有的查询语句里面会有一个以上的操作,例如查完book再查page,那么被事务注解的方法,两者属于同一个事务,只有事务中的两个原子操作成功了,这次事务才是成功!
注意下面的BookInfo
```
@Query("select * from book where id = :bookId")
public BookInfo getBookAndPageInfo(int bookId);
```
BookInfo.java
```
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

}
```

上面有说过,Room会根据查询的字段和返回值的字段进行比对,然后生成对应的实体,
- @Embedded: 嵌入,在select * from book where id = :bookId这句话查询出的内容应该是 book表中的字段,但是用了Book对象来接,这里Room把字段装箱成了Book
- @Relation 虽然查询的是 book,而且sql没有写关联两个标的操作,但是确把对应的关系查出来了,这里Relation就起到了这个作用,仔细看方法的注释.

如果上面的方法能够理解,那我们继续!

### Database

Database起了一个连接作用,entities规定了哪些实体参与映射,version标识数据库版本,exportSchema是否导出数据库结构数据,他是个抽象类,  public abstract BookDao getBookDao();把我们刚刚写好的Dao定义到里面,APT才会生成对应的查询方法


AppDatabase.java
```
/**
 * entities 需要映射的类,如果不添加到这里,是不进行映射的
 * version 数据库版本
 * exportSchema 是否导出表结构,默认为true,建议不修改,因为通过导出的文件可以看到数据库更新的历史记录
 */
@Database(entities = {Book.class, Page.class, User.class, Trade.class}, version = 3, exportSchema = true)
@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;
    public static String DATABSE_NAME = "book_database";

//***************************定义操作的Dao类,必要,其它的爱写哪写哪***********************
    /**
     * 定义 访问数据库的类
     *
     * @return
     */
    public abstract BookDao getBookDao();

    public abstract PageDao getPageDao();

    public abstract UserDao getUserDao();

    public abstract TradeDao getTradeDao();

//****************************************************************

    /**
     * 获取数据库实例,配置数据参数,
     *数据库名称,数据库是否允许在主进程中,升级配置.升级回调,打开数据回调
     *
     * @param context ctx
     * @return AppDatabase
     */
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, DATABSE_NAME)
                            .addMigrations(MIGRATION1_2)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final String TAG = "hb";
    /**
     * 增加了user表
     */
    public static Migration MIGRATION1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            Log.d(TAG, "migrate: " + database.getVersion());
            database.execSQL("CREATE TABLE IF NOT EXISTS User (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT)");
        }
    };

    public static Migration MIGRATION2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            Log.d(TAG, "migrate: " + database.getVersion());
            database.execSQL("CREATE TABLE IF NOT EXISTS Trade (`id` TEXT NOT NULL, `user_id` INTEGER NOT NULL, `book_id` INTEGER NOT NULL, `trade_time` INTEGER, `trade_price` REAL NOT NULL, PRIMARY KEY(`id`))");
            database.execSQL("alter table User add column birthday INTEGER");
        }
    };

}
```

仔细看完上面的代码.只要能得出Database是对 **Entity** 和 **Dao** 管理的就好.


@TypeConverters这个方法写在Database那就是全局应用,写在Entity就只应用在本表中.也是根据返回值和参数来规定什么时候使用.
```
public class DateConverter {
    @TypeConverter
    public Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
```
Trade.java
```
@Entity
@TypeConverters({DateConverter.class})
public class Trade {

    public Trade(@NonNull String id, int userId, int bookId, Date tradeTime, double tradePrice) {
        this.id = id;
        this.userId = userId;
        this.bookId = bookId;
        this.tradeTime = tradeTime;
        this.tradePrice = tradePrice;
    }

    @PrimaryKey
    @NonNull
    public String id;

    @ColumnInfo(name = "user_id")
    public int userId;

    @ColumnInfo(name = "book_id")
    public int bookId;

    @ColumnInfo(name = "trade_time")
    public Date tradeTime;

    @ColumnInfo(name = "trade_price")
    public double tradePrice;

}
```

### 数据库升级

刚才在Database看到了这么一行代码:
```
    public static Migration MIGRATION1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            Log.d(TAG, "migrate: " + database.getVersion());
            database.execSQL("CREATE TABLE IF NOT EXISTS User (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT)");
        }
    };
```
new Migration(1, 2)意思是版本1~2到的升级,当数据库版本1到2的时候就会回调这个方法,上面的代码就是添加User表.
其实我们在以往其它SQLite架构里面,升级数据的时候不需要主动添加表,顶多改下版本号就完了,例如GreenDao.但是Room不行,他会在表中增加哈希码.增加表必须要有更新的操作!

如果你想跨版本new Migration(1, 3),那就是旧版本1,新代码的版本是3那么就回调里面的方法,而2~3不会调用这个方法!

### 测试

Room支持数据库升级测试,在以往的开发过程中,由于增加了字段而没有执行对应的sql,导致调用数据库的崩溃,Room提供了版本更新时的测试,原理是根据每个版本的表结构生成对应的文件,当测试的时候会把以往的版本信息读取出来,然后结合要升级的版本结构去验证是否成功!有了这个数据,无论从什么版本升级都有据可查!

配置gradle
```
  //添加依赖
  def room_version = "1.1.1"
   // Test helpers
  androidTestImplementation "android.arch.persistence.room:testing:$room_version"
```

```
//配置表结构文件要输出的文件夹
android {
    defaultConfig {
     //.....
    javaCompileOptions {
              annotationProcessorOptions {
                 arguments = ["room.schemaLocation":
                                     "$projectDir/schemas".toString()]
              }
            }
        }
    sourceSets {
            androidTest.assets.srcDirs += files("$projectDir/schemas".toString())
    }
}
```
编译一下就会在app->schemas->包名下生成对应的表关系文件

#### 开始测试

因为要操作数据库,所以要在androidTest中使用
```

        MigrationTestHelper migrationTestHelper = new MigrationTestHelper(InstrumentationRegistry.getInstrumentation(), AppDatabase.class.getCanonicalName(), new FrameworkSQLiteOpenHelperFactory());
        migrationTestHelper.createDatabase(AppDatabase.DATABSE_NAME, 1);//创建版本库1的版本,必要条件

        migrationTestHelper.runMigrationsAndValidate(AppDatabase.DATABSE_NAME, 3, false, MIGRATION1_2, MIGRATION2_3);
```

- InstrumentationRegistry用来模拟环境可以从中获取上下文对象等.
- MigrationTestHelper(Instrumentation instrumentation ,String assetsFolder ,SupportSQLiteOpenHelper.Factory openFactory ),这里注意第二个参数assetsFolder,就是那些自动生成的app->schemas文件夹,传Database路径就好
-  migrationTestHelper.createDatabase(AppDatabase.DATABSE_NAME, 1);
    - 第一个参数数据库名称,
    - 第二个参数版本号,这个方法是为了创建对应的版本数据的

-   migrationTestHelper.runMigrationsAndValidate(AppDatabase.DATABSE_NAME, 3, false, MIGRATION1_2, MIGRATION2_3);
    -   参数一: 数据库名,
    -   参数二: 要升级到哪个版本的数据库,
    -   参数三: 出问题是否删除表,true,删除表;
    -   参数四: 升级的回调实现

### 查询模式

Room数据库不允许在UI线程执行任何数据库相关的操作,虽然可以通过设置 .allowMainThreadQueries()必过校验但是不建议,;那么怎么解决插入在异步线程,查询也在异步线程的同步问题呢?Room数据库提供了LiveData

#### LiveData
```
    @Query("select Page.id ,Page.book_id,Page.page_content,Book.book_name from Page " +
            "inner join Book on Page.book_id = Book.id")
    public LiveData<List<PageDetail>> getAllPageInfo();
```
这里我们需要添加
```
def room_version = "1.1.1"
implementation "android.arch.lifecycle:extensions:$room_version"//声明周期
```
然后就可以使用AndroidViewModel
```
public class PageViewModel extends AndroidViewModel {

    public PageViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getDatabase(application);
        pageDao = database.getPageDao();
        prepare(database);
        allPageInfo = pageDao.getAllPageInfo();
    }

    public void prepare(AppDatabase database) {
        BookDao bookDao = database.getBookDao();
        Book newBook = new Book("一本小说", 10.0, "小FaFa");
        new AsyAddBook(bookDao, new IBookCallBack() {//默认情况下,Room的数据库操作都要在子线程,但是可以在AppDatabase里修改
            @Override
            public void bookId(int id) {
                bookId = id;
            }
        }).execute(newBook);
    }

    public LiveData<List<PageDetail>> getAllPageInfo() {
        return allPageInfo;
    }

    static class AsyInsertPage extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... bookIds) {
                pageDao.insert(getPage(bookIds[0]));
                Thread.sleep(1000);//模拟耗时操作
                pageDao.insert(getPage(bookIds[0]));
                Thread.sleep(1000);//模拟耗时操作
                pageDao.insert(getPage(bookIds[0]));
        }
    }
    static class AsyAddBook extends AsyncTask<Book, Void, Integer> {
        @Override
        protected Integer doInBackground(Book... books) {
            bookDao.deleteAll();
            bookDao.insert(books[0]);
            Book book = bookDao.getBookByBookName("一本小说");
            if (bookCallBack != null) {
                bookCallBack.bookId(book.id);
            }
            return book.id;
        }
    }

}
```
MainActivity.java
```
 pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        LiveData<List<PageDetail>> allPageInfo = pageViewModel.getAllPageInfo();
        allPageInfo.observe(this, new Observer<List<PageDetail>>() {
            @Override
            public void onChanged(@Nullable List<PageDetail> pageDetails) {
                StringBuffer sb = new StringBuffer();
                for (PageDetail pageDetail : pageDetails) {
                    sb.append(pageDetail.toString()).append("\n");
                }
                tvContent.setText(sb.toString());
            }
        });
```
当查询的内容发生变化的时候自动会回调onChanged方法
详情可以看代码[源码地址]()
#### RxJava
添加依赖
```
def room_version = "1.1.1"
implementation "android.arch.persistence.room:rxjava2:$room_version" //rxjava返回值
```
和其它没什么区别,就是包裹一下返回值
```
@Query("select Page.id ,Page.book_id,Page.page_content,Book.book_name from Page " +
            "inner join Book on Page.book_id = Book.id")
public Flowable<List<PageDetail>> getAllPageInfoRx();
```
获取数据
```
 subscribe = pageDao.getAllPageInfoRx().subscribeOn(Schedulers.io()).subscribe(new Consumer<List<PageDetail>>() {
            public static final String TAG = "hb";

            @Override
            public void accept(List<PageDetail> pageDetails) throws Exception {
                Log.d(TAG, "accept: " + pageDetails.toString());

            }
        });
```

### 代码

因为先写的代码再写的文章,就显得逻辑没那么通畅,没有按照先简单后容易的方式去写,而是直接把遇到的一下说完,希望有的地方能帮助到你,已经同步代码放到了GitHub上;代码注释很详细

[RoomOrmDemo](https://github.com/BuleB/RoomOrmDemo)

package com.irene.algui;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "addressDB";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_ADDRESS = "addresses";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ADDRESS = "address";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 系统会自动检测数据库文件是否存在
        // 只有首次创建数据库时才会执行
        String CREATE_TABLE = "CREATE TABLE " + TABLE_ADDRESS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_ADDRESS + " TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 当 DATABASE_VERSION 的值增加且设备上已存在旧版本数据库时触发
        // 例如：之前安装的版本是1，现在升级到版本2
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADDRESS);
        onCreate(db);
    }
}
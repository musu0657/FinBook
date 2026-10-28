package com.example.finbook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 账单数据库帮助类，负责建表以及账单数据的增删改查操作
 */
public class BillDatabaseHelper extends SQLiteOpenHelper {

    /** 数据库名称 */
    private static final String DB_NAME = "FinBook.db";
    /** 数据库版本号（提升到 2，用于新增 time 字段） */
    private static final int DB_VERSION = 2;

    /** 表名 */
    private static final String TABLE_BILL = "bill";
    /** 各列名称 */
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_AMOUNT = "amount";
    private static final String COLUMN_REMARK = "remark";
    private static final String COLUMN_TIME = "time";

    /** 建表 SQL 语句 */
    private static final String CREATE_TABLE_BILL =
            "CREATE TABLE " + TABLE_BILL + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TYPE + " TEXT, " +
                    COLUMN_CATEGORY + " TEXT, " +
                    COLUMN_AMOUNT + " REAL, " +
                    COLUMN_REMARK + " TEXT, " +
                    COLUMN_TIME + " TEXT)";

    /** 构造方法 */
    public BillDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /** 数据库首次创建时调用，用于建表 */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_BILL);
    }

    /** 数据库版本升级时调用 */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 简化处理：升级时删除旧表并重建（会清空旧数据；如需保留旧数据可改为 ALTER TABLE 增加列）
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BILL);
        onCreate(db);
    }

    /** 插入一条账单数据 */
    public void insertBill(Bill bill) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TYPE, bill.getType());
        values.put(COLUMN_CATEGORY, bill.getCategory());
        values.put(COLUMN_AMOUNT, bill.getAmount());
        values.put(COLUMN_REMARK, bill.getRemark());
        values.put(COLUMN_TIME, bill.getTime());
        db.insert(TABLE_BILL, null, values);
    }

    /** 根据账单 id 更新一条账单数据 */
    public void updateBill(Bill bill) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TYPE, bill.getType());
        values.put(COLUMN_CATEGORY, bill.getCategory());
        values.put(COLUMN_AMOUNT, bill.getAmount());
        values.put(COLUMN_REMARK, bill.getRemark());
        values.put(COLUMN_TIME, bill.getTime());
        db.update(TABLE_BILL, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(bill.getId())});
    }

    /** 根据 id 删除一条账单数据 */
    public void deleteBill(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_BILL, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    /** 查询所有账单数据并返回 List<Bill> */
    public List<Bill> getAllBills() {
        List<Bill> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        // 按 id 降序查询，最新插入的账单排在前面
        Cursor cursor = db.query(TABLE_BILL, null, null, null, null, null, COLUMN_ID + " DESC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Bill bill = new Bill();
                bill.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                bill.setType(cursor.getString(cursor.getColumnIndex(COLUMN_TYPE)));
                bill.setCategory(cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY)));
                bill.setAmount(cursor.getDouble(cursor.getColumnIndex(COLUMN_AMOUNT)));
                bill.setRemark(cursor.getString(cursor.getColumnIndex(COLUMN_REMARK)));
                bill.setTime(cursor.getString(cursor.getColumnIndex(COLUMN_TIME)));
                list.add(bill);
            }
            cursor.close();
        }
        return list;
    }

    /** 清空所有账单数据（方便测试） */
    public void clearAllBills() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_BILL, null, null);
    }
}

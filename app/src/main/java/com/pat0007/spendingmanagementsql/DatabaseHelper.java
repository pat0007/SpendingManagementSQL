package com.pat0007.spendingmanagementsql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "TransactionHistory.db";
    public static final String TABLE_NAME = "transactionsTable";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "TRANSACTION_DATE";
    public static final String COL_3 = "AMOUNT";
    public static final String COL_4 = "CATEGORY";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "TRANSACTION_DATE DATE,AMOUNT TEXT,CATEGORY TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String transactionDate, String amount, String category) {
        ContentValues contentValues = new ContentValues();
        SQLiteDatabase db = this.getWritableDatabase();

        contentValues.put(COL_2, transactionDate);
        contentValues.put(COL_3, amount);
        contentValues.put(COL_4, category);
        long isInsertSuccessful = db.insert(TABLE_NAME, null, contentValues);
        if (isInsertSuccessful == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return result;
    }

    public Cursor getSelectData(String query) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + query, null);
        return result;
    }
}

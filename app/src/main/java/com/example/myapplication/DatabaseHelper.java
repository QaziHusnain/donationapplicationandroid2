package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "donation.db";
    public static final String TABLE_NAME = "donations";
    public static final String COL_ID = "ID";
    public static final String COL_NAME = "NAME";
    public static final String COL_MOBILE = "MOBILE";
    public static final String COL_AMOUNT = "AMOUNT";
    public static final String TABLE_DONATION = "donations";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT, " +
                COL_MOBILE + " TEXT, " +
                COL_AMOUNT + " INTEGER)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean saveDonation(Donation donation) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_NAME, donation.getName());
        contentValues.put(COL_MOBILE, donation.getMobile());
        contentValues.put(COL_AMOUNT, donation.getAmount());


        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1;
    }

    public Cursor getAllDonations() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }
    public int getTotalDonation() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + COL_AMOUNT + ") FROM " + TABLE_DONATION, null);

        int totalDonation = 0;

        if (cursor.moveToFirst()) {
            totalDonation = cursor.getInt(0);
        }

        cursor.close();
        return totalDonation;
    }
}

package com.example.android.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.android.inventoryapp.data.ItemContract.ItemEntry;

public class ItemDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "inventory.db";
    private static final int DATABASE_VERSION = 1;

    public ItemDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ItemEntry.TABLE_NAME);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_ITEMS_TABLE = "CREATE TABLE " + ItemEntry.TABLE_NAME + " (" + ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + ItemEntry.COLUMN_ITEM_NAME + " TEXT NOT NULL, " + ItemEntry.COLUMN_ITEM_PRICE + " INTEGER NOT NULL, " + ItemEntry.COLUMN_ITEM_QUANTITY + " INTEGER NOT NULL, " + ItemEntry.COLUMN_ITEM_AVAILABILITY + " INTEGER NOT NULL DEFAULT 0, " + ItemEntry.COLUMN_ITEM_SUPPLIER + " TEXT NOT NULL, " + ItemEntry.COLUMN_ITEM_PHONE + " INTEGER );";

        Log.v("SQL_CREATE:", SQL_CREATE_ITEMS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_ITEMS_TABLE);
    }
}

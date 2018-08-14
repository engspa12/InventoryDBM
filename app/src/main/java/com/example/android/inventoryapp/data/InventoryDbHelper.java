package com.example.android.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;
/**
 * Created by DBM on 6/25/2017.
 */

public class InventoryDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "inventory.db";

    public static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + InventoryEntry.TABLE_NAME + " (" +
            InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            InventoryEntry.COLUMN_PRODUCT_BRAND + " TEXT NOT NULL," +
            InventoryEntry.COLUMN_PRODUCT_WARRANTY + " INTEGER NOT NULL," +
            InventoryEntry.COLUMN_PRODUCT_YEAR_MANUFACTURE + " TEXT NOT NULL," +
            InventoryEntry.COLUMN_PRODUCT_WEIGHT + " REAL NOT NULL," +
            InventoryEntry.COLUMN_PRODUCT_PRICE + " REAL NOT NULL," +
            InventoryEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL," +
            InventoryEntry.COLUMN_PRODUCT_STOCK + " INTEGER NOT NULL," +
            InventoryEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL," +
            InventoryEntry.COLUMN_PRODUCT_TYPE + " INTEGER NOT NULL," +
            //WE NEED TO CHANGE IT TO BLOB AFTERWARDS
            InventoryEntry.COLUMN_PRODUCT_IMAGE_URL + " TEXT NOT NULL)";

    public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + InventoryEntry.TABLE_NAME;

    public InventoryDbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}

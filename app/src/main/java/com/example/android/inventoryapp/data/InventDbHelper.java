package com.example.android.inventoryapp.data;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.android.inventoryapp.data.InventContract.InventEntry;
public class InventDbHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG=InventDbHelper.class.getSimpleName();
    private static final String DATABASE_NAME="shelter.db";
    private static final int DATABASE_VERSION=1;

    public InventDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    //name,price,quantity,imageid
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
    String SQL_CREATE_INVENT_TABLE="CREATE TABLE "+InventEntry.TABLE_NAME+" ( "
            +InventEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
            +InventEntry.INVENTAPP_NAME_COLUMN+" TEXT NOT NULL, "
            +InventEntry.INVENTAPP_PRICE_COLUMN+" INTEGER NOT NULL, "
            +InventEntry.INVENTAPP_QUANTITY_COLUMN+" INTEGER , "
            +InventEntry.INVENTAPP_IMAGEID_COLUMN+" INTEGER NOT NULL );";
           Log.d(LOG_TAG, "jabtak "+SQL_CREATE_INVENT_TABLE);
          sqLiteDatabase.execSQL(SQL_CREATE_INVENT_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}

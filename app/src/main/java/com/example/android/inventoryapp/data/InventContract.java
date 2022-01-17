package com.example.android.inventoryapp.data;

import android.net.Uri;
import android.content.ContentResolver;
import android.provider.BaseColumns;

public class InventContract {
    private InventContract(){}

    public static final String CONTENT_AUTHORITY="com.example.android.inventoryapp";
    public static final Uri BASE_CONTENT_URI=Uri.parse("content://"+CONTENT_AUTHORITY);
    public static final String INVENTS_PATH="inventoryapp";
    public static final class InventEntry implements BaseColumns{
    public static final Uri CONTENT_URI=Uri.withAppendedPath(BASE_CONTENT_URI,INVENTS_PATH);
    public static final String CONTENT_LIST_TYPE=ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+INVENTS_PATH;
    public static final String CONTENT_ITEM_TYPE=ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+INVENTS_PATH;
    //name,price,quantity,imageid
    public static final String TABLE_NAME="inventoryapp";
    public static final String _ID=BaseColumns._ID;
    public static final String INVENTAPP_NAME_COLUMN="name";
    public static final String INVENTAPP_PRICE_COLUMN="price";
    public static final String INVENTAPP_QUANTITY_COLUMN="quantity";
    public static final String INVENTAPP_IMAGEID_COLUMN="imageId";




    }
}

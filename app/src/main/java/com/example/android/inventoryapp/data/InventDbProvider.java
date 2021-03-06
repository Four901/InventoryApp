package com.example.android.inventoryapp.data;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.android.inventoryapp.R;
import com.example.android.inventoryapp.data.InventContract.InventEntry;

import java.net.URL;

public class InventDbProvider extends ContentProvider {
    public static final String LOG_TAG=InventDbProvider.class.getSimpleName();
    private static final int INVENTS=100;
    private static final int INVENT=101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // The content URI of the form "content://com.example.android.pets/pets" will map to the
        // integer code {@link #PETS}. This URI is used to provide access to MULTIPLE rows
        // of the pets table.
        sUriMatcher.addURI(InventContract.CONTENT_AUTHORITY, InventContract.INVENTS_PATH,INVENTS);

        // The content URI of the form "content://com.example.android.pets/pets/#" will map to the
        // integer code {@link #PET_ID}. This URI is used to provide access to ONE single row
        // of the pets table.
        //
        // In this case, the "#" wildcard is used where "#" can be substituted for an integer.
        // For example, "content://com.example.android.pets/pets/3" matches, but
        // "content://com.example.android.pets/pets" (without a number at the end) doesn't match.
        sUriMatcher.addURI(InventContract.CONTENT_AUTHORITY, InventContract.INVENTS_PATH + "/#",INVENT);
    }
    private InventDbHelper mDbHelper ;
    @Override
    public boolean onCreate() {
        mDbHelper = new InventDbHelper(getContext());

        return true;

    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
// Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;
        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTS:
                // For the PETS code, query the pets table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the pets table.
                cursor = database.query(InventEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case INVENT:
                // For the PET_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.pets/pets/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = InventEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(InventEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the cursor
        return cursor;
    }



    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        Log.d(LOG_TAG, "jabtak ins");
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTS:
                return insertInvent(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }
    private Uri insertInvent(Uri uri,ContentValues values)
    {
        Log.d(LOG_TAG, "jabtak");

        // Check that the name is not null
        String name = values.getAsString(InventEntry.INVENTAPP_NAME_COLUMN);
        if (name == null) {
            throw new IllegalArgumentException("Invent requires a name");
        }

        // Check that the gender is valid
        Integer price = values.getAsInteger(InventEntry.INVENTAPP_PRICE_COLUMN);
        if (price == null) {
            throw new IllegalArgumentException("Invent requires valid price");
        }

        // If the weight is provided, check that it's greater than or equal to 0 kg
        Integer quantity = values.getAsInteger(InventEntry.INVENTAPP_QUANTITY_COLUMN);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Invent requires some quantity");
        }
        Integer imageId=values.getAsInteger(InventEntry.INVENTAPP_IMAGEID_COLUMN);
        if(imageId==null)
        {
            imageId= R.drawable.ic_empty_shelter;
        }
        values.put(InventEntry.INVENTAPP_IMAGEID_COLUMN,imageId);
        // No need to check the breed, any value is valid (including null).

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new pet with the given values
        long id = database.insert(InventEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the pet content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTS:
                return updateInvent(uri, contentValues, selection, selectionArgs);
            case INVENT:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = InventEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateInvent(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }
    private int updateInvent(Uri uri,ContentValues values,String selection,String [] selectionArgs)
    {
        if (values.containsKey(InventEntry.INVENTAPP_NAME_COLUMN)) {
            String name = values.getAsString(InventEntry.INVENTAPP_NAME_COLUMN);
            if (name == null) {
                throw new IllegalArgumentException("Invent requires a name");
            }
        }

        // If the {@link PetEntry#COLUMN_PET_GENDER} key is present,
        // check that the gender value is valid.
        if (values.containsKey(InventEntry.INVENTAPP_PRICE_COLUMN)) {
            Integer price = values.getAsInteger(InventEntry.INVENTAPP_PRICE_COLUMN);
            if (price == null) {
                throw new IllegalArgumentException("Invent requires a price");
            }
        }

        // If the {@link PetEntry#COLUMN_PET_WEIGHT} key is present,
        // check that the weight value is valid.
        if (values.containsKey(InventEntry.INVENTAPP_QUANTITY_COLUMN)) {
            // Check that the weight is greater than or equal to 0 kg
            Integer quantity = values.getAsInteger(InventEntry.INVENTAPP_QUANTITY_COLUMN);
            if (quantity != null && quantity< 0) {
                throw new IllegalArgumentException("Quantity should be valid");
            }
        }
        if (values.containsKey(InventEntry.INVENTAPP_IMAGEID_COLUMN)) {
            // Check that the weight is greater than or equal to 0 kg
            Integer imageId = values.getAsInteger(InventEntry.INVENTAPP_IMAGEID_COLUMN);
            if (imageId== null) {
                imageId=R.drawable.ic_empty_shelter;
            }
        }
        else
        {
            values.put(InventEntry.INVENTAPP_IMAGEID_COLUMN,R.drawable.ic_empty_shelter);
        }
        // No need to check the breed, any value is valid (including null).

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(InventEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(InventEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case INVENT:
                // Delete a single row given by the ID in the URI
                selection = InventEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(InventEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTS:
                return InventEntry.CONTENT_LIST_TYPE;
            case INVENT:
                return InventEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}

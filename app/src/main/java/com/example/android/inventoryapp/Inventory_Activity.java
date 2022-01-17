package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.android.inventoryapp.data.InventDbProvider;
import com.example.android.inventoryapp.data.InventContract.InventEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class Inventory_Activity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /** Identifier for the pet data loader */
    private static final int INVENT_LOADER = 0;
   String LOG_TAG=Inventory_Activity.class.getSimpleName();
    /** Adapter for the ListView */
    InventCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_activity);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Inventory_Activity.this,InventoryeditActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the pet data
        ListView petListView = (ListView) findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        petListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of pet data in the Cursor.
        // There is no pet data yet (until the loader finishes) so pass in null for the Cursor.
        mCursorAdapter = new InventCursorAdapter(this, null);
        petListView.setAdapter(mCursorAdapter);


        // Setup the item click listener
        petListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(Inventory_Activity.this, InventShower_Activity.class);

                // Form the content URI that represents the specific pet that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link PetEntry#CONTENT_URI}.
                // For example, the URI would be "content://com.example.android.pets/pets/2"
                // if the pet with ID 2 was clicked on.
                Uri currentInventUri = ContentUris.withAppendedId(InventEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentInventUri);

                // Launch the {@link EditorActivity} to display the data for the current pet.
                startActivity(intent);
            }
        });

        // Kick off the loader
        getLoaderManager().initLoader(INVENT_LOADER, null, this);
    }

    /**
     * Helper method to insert hardcoded pet data into the database. For debugging purposes only.
     */
    private final void insertPet() {
        // Create a ContentValues object where column names are the keys,
        // and Toto's pet attributes are the values.

        ContentValues values = new ContentValues();
        values.put(InventEntry.INVENTAPP_NAME_COLUMN, "Toto");
        values.put(InventEntry.INVENTAPP_PRICE_COLUMN, 10);
        values.put(InventEntry.INVENTAPP_QUANTITY_COLUMN,5);
        values.put(InventEntry.INVENTAPP_IMAGEID_COLUMN, R.drawable.ic_empty_shelter);
        Log.d(LOG_TAG, "jabtak");
        // Insert a new row for Toto into the provider using the ContentResolver.
        // Use the {@link PetEntry#CONTENT_URI} to indicate that we want to insert
        // into the pets database table.
        // Receive the new content URI that will allow us to access Toto's data in the future.
        Uri newUri = getContentResolver().insert(InventEntry.CONTENT_URI, values);
    }

    /**
     * Helper method to delete all pets in the database.
     */
    private void deleteAllPets() {
        int rowsDeleted = getContentResolver().delete(InventEntry.CONTENT_URI, null, null);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_invent, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertPet();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllPets();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                InventEntry._ID,
                InventEntry.INVENTAPP_NAME_COLUMN,
                InventEntry.INVENTAPP_PRICE_COLUMN,
                InventEntry.INVENTAPP_QUANTITY_COLUMN,
                InventEntry.INVENTAPP_IMAGEID_COLUMN};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                InventEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update {@link PetCursorAdapter} with this new cursor containing updated pet data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }
    public void orderInvent(Cursor cursor)
    {
        int nameColumnIndex=cursor.getColumnIndex(InventEntry.INVENTAPP_NAME_COLUMN);
        int priceColumnIndex=cursor.getColumnIndex(InventEntry.INVENTAPP_PRICE_COLUMN);
        int quantityColumnIndex=cursor.getColumnIndex(InventEntry.INVENTAPP_QUANTITY_COLUMN);
        int imageIdColumnIndex=cursor.getColumnIndex(InventEntry.INVENTAPP_IMAGEID_COLUMN);
        int idColumnIndex=cursor.getColumnIndex(InventEntry._ID);

        String inventName=cursor.getString(nameColumnIndex);
        int inventPrice=cursor.getInt(priceColumnIndex);
        int inventQuantity=cursor.getInt(quantityColumnIndex);
        int inventImageId=cursor.getInt(imageIdColumnIndex);

        ContentValues values=new ContentValues();
        values.put(InventEntry.INVENTAPP_NAME_COLUMN,inventName);
        values.put(InventEntry.INVENTAPP_PRICE_COLUMN,inventPrice);
        values.put(InventEntry.INVENTAPP_QUANTITY_COLUMN,inventQuantity-1);
        values.put(InventEntry.INVENTAPP_IMAGEID_COLUMN,inventImageId);


        Uri inventUri=ContentUris.withAppendedId(InventEntry.CONTENT_URI,cursor.getInt(idColumnIndex));
       int rowsAffected= getContentResolver().update(inventUri,values,null,null);


    }
}
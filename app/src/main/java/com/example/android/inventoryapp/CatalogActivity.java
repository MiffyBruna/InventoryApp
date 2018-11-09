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
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.inventoryapp.data.ItemContract.ItemEntry;
import com.example.android.inventoryapp.data.ItemCursorAdapter;

;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int ITEM_LOADER = 0;
    ItemCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView itemListView = findViewById(R.id.list);
        View emptyView = findViewById(R.id.empty_view);
        itemListView.setEmptyView(emptyView);

        mCursorAdapter = new ItemCursorAdapter(this, null);
        itemListView.setAdapter(mCursorAdapter);

        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);

                Uri currentItemUri = ContentUris.withAppendedId(ItemEntry.CONTENT_URI, id);
                intent.setData(currentItemUri);
                startActivity(intent);
            }
        });
        getLoaderManager().initLoader(ITEM_LOADER, null, this);
    }

    private void insertItem() {

        ContentValues values = new ContentValues();
        values.put(ItemEntry.COLUMN_ITEM_NAME, "Panda Squishy");
        values.put(ItemEntry.COLUMN_ITEM_PRICE, 25.2);
        values.put(ItemEntry.COLUMN_ITEM_AVAILABILITY, ItemEntry.QUANTITY_INSTOCK);
        values.put(ItemEntry.COLUMN_ITEM_QUANTITY, 5);
        values.put(ItemEntry.COLUMN_ITEM_SUPPLIER, "CuteSquishy");
        values.put(ItemEntry.COLUMN_ITEM_PHONE, "2080545502");

        Uri newUri = getContentResolver().insert(ItemEntry.CONTENT_URI, values);
    }

    private void deleteAllItems() {
        int rowsDeleted = getContentResolver().delete(ItemEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from database");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                insertItem();
                return true;
            case R.id.action_delete_all_entries:
                deleteAllItems();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {ItemEntry._ID, ItemEntry.COLUMN_ITEM_NAME, ItemEntry.COLUMN_ITEM_PRICE, ItemEntry.COLUMN_ITEM_AVAILABILITY, ItemEntry.COLUMN_ITEM_QUANTITY, ItemEntry.COLUMN_ITEM_SUPPLIER, ItemEntry.COLUMN_ITEM_PHONE,};
        return new CursorLoader(this, ItemEntry.CONTENT_URI, projection, null, null, null);
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }


    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}

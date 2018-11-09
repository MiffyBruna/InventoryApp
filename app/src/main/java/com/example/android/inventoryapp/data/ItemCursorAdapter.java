package com.example.android.inventoryapp.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.R;

public class ItemCursorAdapter extends CursorAdapter {


    public ItemCursorAdapter(Context context, Cursor c) {
        /* flags */
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(final View view, final Context context, Cursor cursor) {


        TextView nameTextView = view.findViewById(R.id.name);
        TextView priceTextView = view.findViewById(R.id.price);
        TextView quantityTextView = view.findViewById(R.id.quantity);
        TextView supplierTextView = view.findViewById(R.id.supplier);
        TextView phoneTextView = view.findViewById(R.id.phone);
        TextView availabilityTextVw = view.findViewById(R.id.availability);

        int idColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY);
        int supplierColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_SUPPLIER);
        int phoneColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_PHONE);


        final int supplierPhone = cursor.getInt(phoneColumnIndex);
        final int quantity = cursor.getInt(quantityColumnIndex);
        final int id = cursor.getInt(idColumnIndex);

        String supplier = cursor.getString(supplierColumnIndex);
        String itemName = cursor.getString(nameColumnIndex);

        String itemPrice = cursor.getString(priceColumnIndex);
        int phone = cursor.getInt(phoneColumnIndex);

        if (TextUtils.isEmpty(itemName)) {
            itemName = context.getString(R.string.unknown);
        }

        if (quantity == 0) {
            availabilityTextVw.setText(R.string.quantity_nostock);
        } else {
            availabilityTextVw.setText(R.string.quantity_instock);
        }


        nameTextView.setText(itemName);
        priceTextView.setText(String.valueOf(itemPrice));
        quantityTextView.setText(String.valueOf(quantity));
        supplierTextView.setText(supplier);

        final Button sold = view.findViewById(R.id.sale_button);


        sold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 0) {
                    ContentValues values = new ContentValues();
                    Uri uri = ContentUris.withAppendedId(ItemContract.ItemEntry.CONTENT_URI, id);
                    values.put(ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY, quantity - 1);
                    view.getContext().getContentResolver().update(uri, values, null, null);
                } else {
                    Toast.makeText(context, R.string.quantity_error, Toast.LENGTH_SHORT).show();
                }
            }

        });

    }
}

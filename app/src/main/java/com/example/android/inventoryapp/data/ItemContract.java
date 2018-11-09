package com.example.android.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class ItemContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_ITEMS = "items";

    private ItemContract() {
    }

    public static final class ItemEntry implements BaseColumns {


        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ITEMS);

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;

        public static final String TABLE_NAME = "items";

        public static final String _ID = BaseColumns._ID;

        public static final String COLUMN_ITEM_NAME = "name";
        public static final String COLUMN_ITEM_PRICE = "price";
        public static final String COLUMN_ITEM_AVAILABILITY = "availability";
        public static final String COLUMN_ITEM_QUANTITY = "quantity";
        public static final String COLUMN_ITEM_SUPPLIER = "supplier";
        public static final String COLUMN_ITEM_PHONE = "phone";

        public static final int AVAILABILITY_UNKNOWN = 0;
        public static final int QUANTITY_INSTOCK = 1;
        public static final int QUANTITY_OUTSTOCK = 2;

        public static boolean verifyAvailability(int availability) {
            return availability == AVAILABILITY_UNKNOWN || availability == QUANTITY_INSTOCK || availability == QUANTITY_OUTSTOCK;
        }
    }
}

package com.example.android.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by DBM on 6/25/2017.
 */

public final class InventoryContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryapp";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_PRODUCTS = "products";

    private InventoryContract(){}

    public static final class InventoryEntry implements BaseColumns{

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_PRODUCTS);

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        public final static String TABLE_NAME = "products";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_PRODUCT_BRAND = "brand";
        public final static String COLUMN_PRODUCT_WARRANTY = "warranty";
        public final static String COLUMN_PRODUCT_DATE_MANUFACTURE = "date_of_manufacture";
        public final static String COLUMN_PRODUCT_WEIGHT = "weight";
        public final static String COLUMN_PRODUCT_PRICE = "price";
        public final static String COLUMN_PRODUCT_QUANTITY = "quantity";
        public final static String COLUMN_PRODUCT_STOCK = "in_stock";
        public final static String COLUMN_PRODUCT_NAME = "name";
        public final static String COLUMN_PRODUCT_TYPE = "type";
        public final static String COLUMN_PRODUCT_IMAGE_ID = "image_id";



        public final static int NO_STOCK_AVAILABLE = 0;
        public final static int IN_STOCK = 1;
        public final static int UNKNOWN_STOCK = 2;

        public final static int SPORTS_TYPE = 0;
        public final static int TECHNOLOGY_TYPE = 1;
        public final static int HOME_STUFF_TYPE = 2;
        public final static int CLOTHING_TYPE = 3;

        public static boolean validateType(int type){
            return (type == SPORTS_TYPE || type == TECHNOLOGY_TYPE  || type == HOME_STUFF_TYPE || type == CLOTHING_TYPE);
        }

        public static boolean validateStock(int stock){
            return (stock == IN_STOCK ||  stock == NO_STOCK_AVAILABLE || stock == UNKNOWN_STOCK );
        }
    }
}

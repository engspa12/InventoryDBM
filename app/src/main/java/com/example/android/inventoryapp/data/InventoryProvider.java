package com.example.android.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;

import java.util.ArrayList;

import static com.example.android.inventoryapp.data.InventoryContract.InventoryEntry.TABLE_NAME;

/**
 * Created by DBM on 6/25/2017.
 */

public class InventoryProvider extends ContentProvider {

    private boolean previousDeletion = false;

    private static final int PRODUCTS = 100;
    private static final int PRODUCT_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static{

        sUriMatcher.addURI(
                InventoryContract.CONTENT_AUTHORITY,InventoryContract.PATH_PRODUCTS,PRODUCTS);

        sUriMatcher.addURI(
                InventoryContract.CONTENT_AUTHORITY,InventoryContract.PATH_PRODUCTS + "/#",PRODUCT_ID);
    }

    private InventoryDbHelper dbHelper;


    @Override
    public boolean onCreate() {
        dbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase database = dbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                // For the PRODUCTS code, query the products table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the products table.
                // TODO: Perform database query on products table
                cursor=database.query(TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case PRODUCT_ID:
                // For the PRODUCT_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.products/products/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the products table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(),uri);

        return cursor;
    }



    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertProduct(Uri uri, ContentValues values) {

        // Check that the name is not null
        String name = values.getAsString(InventoryEntry.COLUMN_PRODUCT_NAME);

        if (name == null) {
            throw new IllegalArgumentException("Product requires a name");
        }
        // TODO: Insert a new product into the products database table with the given ContentValues

        Integer type = values.getAsInteger(InventoryEntry.COLUMN_PRODUCT_TYPE);

        if (type == null || !InventoryEntry.validateType(type)) {
            throw new IllegalArgumentException("Product requires valid type");
        }

        // If the weight is provided, check that it's greater than or equal to 0 kg
        Double weight = values.getAsDouble(InventoryEntry.COLUMN_PRODUCT_WEIGHT);

        if (weight != null && weight < 0) {
            throw new IllegalArgumentException("Product requires valid weight");
        }

        //If everything is correct, we proceed to write into the database
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        long newRowId = database.insert(TABLE_NAME,null,values);
        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it

        // if (newRowId == -1) {
        //    Log.e(LOG_TAG, "Failed to insert row for " + uri);
        //    return null;
        // }


        if (newRowId != -1){
            Toast.makeText(getContext(), "The product was added successfully",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getContext(),"The product had a problem when inserted",Toast.LENGTH_SHORT).show();
        }

        getContext().getContentResolver().notifyChange(uri,null);

        if(previousDeletion) {
            rearrangeIdColumn(InventoryEntry.CONTENT_URI);
        }

        return ContentUris.withAppendedId(uri, newRowId);
    }


    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case PRODUCT_ID:
                // For the PRODUCT_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateProduct(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // TODO: Update the selected products in the products database table with the given ContentValues
        // Check that the name is not null

        if(values.containsKey(InventoryEntry.COLUMN_PRODUCT_NAME)) {

            String name = values.getAsString(InventoryEntry.COLUMN_PRODUCT_NAME);

            if (name == null) {
                throw new IllegalArgumentException("Product requires a name");
            }

        }
        // TODO: Insert a new products into the products database table with the given ContentValues


        if(values.containsKey(InventoryEntry.COLUMN_PRODUCT_TYPE)) {
            Integer type = values.getAsInteger(InventoryEntry.COLUMN_PRODUCT_TYPE);

            if (type == null || !InventoryEntry.validateType(type)) {
                throw new IllegalArgumentException("Product requires valid type");
            }
        }

        if(values.containsKey(InventoryEntry.COLUMN_PRODUCT_WEIGHT)) {
            // If the weight is provided, check that it's greater than or equal to 0 kg
            Double weight = values.getAsDouble(InventoryEntry.COLUMN_PRODUCT_WEIGHT);

            if (weight != null && weight < 0) {
                throw new IllegalArgumentException("Product requires valid weight");
            }
        }

        if (values.size() == 0) {
            return 0;
        }
        //If everything is correct, we proceed to write into the database
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = db.update(TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0){
            Toast.makeText(getContext(),"Product was updated successfully",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getContext(),"Product had a problem when updated",Toast.LENGTH_SHORT).show();
        }


        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // TODO: Return the number of rows that were affected
        return rowsUpdated;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return InventoryEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return InventoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(TABLE_NAME, selection, selectionArgs);
                database.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + TABLE_NAME + "'");
                previousDeletion = false;
                break;
            case PRODUCT_ID:
                // Delete a single row given by the ID in the URI
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(TABLE_NAME, selection, selectionArgs);
                previousDeletion = true;
                rearrangeIdColumn(InventoryEntry.CONTENT_URI);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }


        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        if (rowsDeleted != 0) {
            Toast.makeText(getContext(), "Product deleted successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Product deletion failed", Toast.LENGTH_SHORT).show();
        }

        return rowsDeleted;
    }

    public void rearrangeIdColumn(Uri uri){
        String[] projection = {InventoryEntry._ID};
        Cursor cursor = query(uri,projection,null,null,null);
        ArrayList<String> helperList = new ArrayList<>();

        int count=0;
        if(cursor.moveToFirst()){
            String currentId = cursor.getString(cursor.getColumnIndexOrThrow(InventoryEntry._ID));
            helperList.add(currentId);
            while(cursor.moveToNext()){
                count++;
                currentId = cursor.getString(cursor.getColumnIndexOrThrow(InventoryEntry._ID));
                helperList.add(currentId);
            }
            count++;

            cursor.close();

            for(int i=1;i<=count;i++){
                Uri arrangeUri = ContentUris.withAppendedId(uri,Long.parseLong(helperList.get(i-1)));
                ContentValues values = new ContentValues();
                values.put(InventoryEntry._ID,i);
                update(arrangeUri,values,null,null);
            }

        }

    }
}

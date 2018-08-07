package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    @BindView(R.id.fab_product) FloatingActionButton fab;
    @BindView(R.id.list) ListView listView;
    @BindView(R.id.empty_view) TextView emptyView;


    private InventoryCursorAdapter adapter;

    private String[] projection = {InventoryEntry.COLUMN_PRODUCT_BRAND,InventoryEntry.COLUMN_PRODUCT_WARRANTY, InventoryEntry.COLUMN_PRODUCT_DATE_MANUFACTURE,
    InventoryEntry.COLUMN_PRODUCT_WEIGHT, InventoryEntry.COLUMN_PRODUCT_PRICE, InventoryEntry.COLUMN_PRODUCT_QUANTITY, InventoryEntry.COLUMN_PRODUCT_STOCK,
            InventoryEntry.COLUMN_PRODUCT_STOCK,InventoryEntry.COLUMN_PRODUCT_NAME,InventoryEntry.COLUMN_PRODUCT_TYPE,InventoryEntry.COLUMN_PRODUCT_IMAGE_ID};

    private Uri modifyUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overall_layout);
        ButterKnife.bind(this);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,EditorActivity.class);
                startActivity(intent);
            }
        });

        listView.setEmptyView(emptyView);
        //emptyView.setVisibility(View.GONE);

        adapter = new InventoryCursorAdapter(this,null);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            //We don't use the parameter id, instead we use view.getId() to know if the SALE button was clicked or not
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                long viewId = view.getId();
                System.out.println("the position is: " + position);
                System.out.println("the id is: " + id);
                modifyUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI,position+1);

                if(viewId == R.id.sale_button){
                    reduceQuantityItem();
                }
                else{
                    Intent intent = new Intent(MainActivity.this,EditorActivity.class);
                    Uri wantedUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI,id);
                    intent.setData(wantedUri);
                    startActivity(intent);
                }
            }
        });

        getSupportLoaderManager().initLoader(1, null, this);

    }

    @Override
    protected void onStart(){
        super.onStart();
    }

    public void reduceQuantityItem()
    {
        System.out.println("the sale button was pressed");
        Cursor cursor = getContentResolver().query(modifyUri,projection,null,null,null);
        System.out.println("the cursor position is: " + cursor.getPosition());
        System.out.println("the cursor count is: " + cursor.getCount());

        int quantity = 0;
        if(cursor.moveToFirst()) {
            quantity = cursor.getInt(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_PRODUCT_QUANTITY));
        }
        cursor.close();

        if(quantity > 0){
            quantity--;
        }
        else{
            Toast.makeText(getBaseContext(),"The min quantity allowed is 0",Toast.LENGTH_SHORT).show();

            return;
        }
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY,quantity);
        getContentResolver().update(modifyUri,values,null,null);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(getBaseContext(), InventoryEntry.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

            adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertDummyProduct();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteEntries();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void insertDummyProduct(){

        ContentValues values = new ContentValues();

        values.put(InventoryEntry.COLUMN_PRODUCT_BRAND,"Product Brand");
        values.put(InventoryEntry.COLUMN_PRODUCT_WARRANTY,360);
        values.put(InventoryEntry.COLUMN_PRODUCT_DATE_MANUFACTURE,"2017");
        values.put(InventoryEntry.COLUMN_PRODUCT_WEIGHT,1);
        values.put(InventoryEntry.COLUMN_PRODUCT_PRICE,1.00);
        values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY,1);
        values.put(InventoryEntry.COLUMN_PRODUCT_STOCK,1);
        values.put(InventoryEntry.COLUMN_PRODUCT_NAME,"Product Name");
        values.put(InventoryEntry.COLUMN_PRODUCT_TYPE,3);
        values.put(InventoryEntry.COLUMN_PRODUCT_IMAGE_ID,R.drawable.clothing);

        getContentResolver().insert(InventoryEntry.CONTENT_URI,values);

    }

    public void deleteEntries(){
        getContentResolver().delete(InventoryEntry.CONTENT_URI, null, null);

    }
}

package com.example.android.inventoryapp;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements InventoryAdapter.ItemClickListener {


    @BindView(R.id.fab_product) FloatingActionButton fab;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.empty_view) TextView emptyView;

    private AppDatabase mDb;

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String BRAND = "Product Brand";
    private static final int WARRANTY = 360;
    private static final String MANUFACTURE_YEAR = "2017";
    private static final double WEIGHT = 1.56;
    private static final double PRICE = 14.99;
    private static final int QUANTITY = 5;
    private static final int IN_STOCK = 1;
    private static final String NAME = "Product Name";
    private static final int TYPE_PRODUCT = 3;


    private static final String downloadImageUrl = "http://www.bsmc.net.au/wp-content/uploads/No-image-available.jpg";

    private InventoryAdapter adapter;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overall_layout);
        ButterKnife.bind(this);

        mDb = AppDatabase.getInstance(getApplicationContext());

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }

            }
        };


       mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "OnComplete : " + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Failed : ", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                   }
                });


       fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,EditorActivity.class);
                startActivity(intent);
                }
        });


       LinearLayoutManager layoutManager = new LinearLayoutManager(this);
       recyclerView.setLayoutManager(layoutManager);
       emptyView.setVisibility(View.GONE);

       recyclerView.setHasFixedSize(true);

       adapter = new InventoryAdapter(this,this);

       recyclerView.setAdapter(adapter);

        DividerItemDecoration itemDecorator = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider));

       recyclerView.addItemDecoration(itemDecorator);

       new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
           @Override
           public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
               return false;
           }

           @Override
           public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        int position = viewHolder.getAdapterPosition();
                        List<ProductItem> productsList = adapter.getProducts();
                        mDb.productItemDao().deleteProduct(productsList.get(position));
                    }
                });
           }
       }).attachToRecyclerView(recyclerView);

       /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

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
        });*/

        //getSupportLoaderManager().initLoader(1, null, this);
        retrieveProducts();

    }

    @Override
    protected void onStart(){
        super.onStart();
       mAuth.addAuthStateListener(mAuthListener);
    }


    private void retrieveProducts() {
        final LiveData<List<ProductItem>> list = mDb.productItemDao().loadAllProducts();
        list.observe(this, new Observer<List<ProductItem>>() {
            @Override
            public void onChanged(@Nullable List<ProductItem> productItems) {
                Log.d(TAG,"Receiving database update from LiveData");
                adapter.setProducts(productItems);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void reduceQuantityItem() {

/*        System.out.println("the sale button was pressed");
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
        getContentResolver().update(modifyUri,values,null,null);*/

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

        final ProductItem productItem = new ProductItem(BRAND,WARRANTY,MANUFACTURE_YEAR,WEIGHT,
                PRICE,QUANTITY,IN_STOCK,NAME,TYPE_PRODUCT,downloadImageUrl);

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.productItemDao().insertProduct(productItem);
            }
        });

        Toast.makeText(this,"Product added successfully",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onItemClickListener(int itemId, int clickedItemIndex, boolean isSaleButton) {
        if(isSaleButton){
            //reduceQuantityItem();
            Toast.makeText(this, "The sale button was pressed, " + "itemId: " + itemId + " - " + "position: " + clickedItemIndex,Toast.LENGTH_SHORT).show();
        }
        else{
            Intent intent = new Intent(MainActivity.this,EditorActivity.class);
            intent.putExtra(EditorActivity.EXTRA_PRODUCT_ID,itemId);
            startActivity(intent);
            //Toast.makeText(this, "Another view was pressed, " + "itemId: " + itemId + " - " + "position: " + clickedItemIndex,Toast.LENGTH_SHORT).show();
        }
    }



    public void deleteEntries(){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.productItemDao().deleteAllProducts();
            }
        });
    }

}

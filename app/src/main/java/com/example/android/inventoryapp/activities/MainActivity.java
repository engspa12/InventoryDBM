package com.example.android.inventoryapp.activities;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.AppDatabase;
import com.example.android.inventoryapp.AppExecutors;
import com.example.android.inventoryapp.adapter.InventoryAdapter;
import com.example.android.inventoryapp.ProductItem;
import com.example.android.inventoryapp.R;
import com.example.android.inventoryapp.viewmodel.MainViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements InventoryAdapter.ItemClickListener {

    @BindView(R.id.fab_product)
    FloatingActionButton fab;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.empty_view)
    TextView emptyView;

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

    private static final String noImageAvailableUrl = "https://www.bsmc.net.au/wp-content/uploads/No-image-available.jpg";

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

        //Anonymous Sign in
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
                        final ProductItem item = productsList.get(position);
                        String urlImageStorageLocation = item.getUrlImageLocation();
                        if(urlImageStorageLocation != null) {
                            //Delete image in FirebaseStorage
                            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                            StorageReference storageRef = firebaseStorage.getReference();
                            StorageReference deleteFileRef = storageRef.child(urlImageStorageLocation);

                            deleteFileRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // File deleted successfully
                                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            mDb.productItemDao().deleteProduct(item);
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Uh-oh, an error occurred!
                                    Toast.makeText(MainActivity.this, getString(R.string.error_delete_storage_message), Toast.LENGTH_SHORT).show();
                                }
                            });

                        } else{
                            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                @Override
                                public void run() {
                                    mDb.productItemDao().deleteProduct(item);
                                }
                            });
                        }
                    }
                });


           }
       }).attachToRecyclerView(recyclerView);

        setupViewModel();

    }

    @Override
    protected void onStart(){
        super.onStart();
       mAuth.addAuthStateListener(mAuthListener);
    }

    //Setup ViewModel using MainViewModel class
    private void setupViewModel() {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getListProductItems().observe(this, new Observer<List<ProductItem>>() {
            @Override
            public void onChanged(@Nullable List<ProductItem> productItems) {
                adapter.setProducts(productItems);
                if(productItems.size() == 0){
                    emptyView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else{
                    emptyView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
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


    //Reduce quantity by 1
    public void reduceQuantityItem(final int position) {

        final LiveData<List<ProductItem>> listProducts = mDb.productItemDao().loadAllProducts();
        listProducts.observe(this, new Observer<List<ProductItem>>() {
            @Override
            public void onChanged(@Nullable List<ProductItem> productItems) {
                listProducts.removeObserver(this);
                final ProductItem saleProduct = productItems.get(position);
                int quantity = saleProduct.getQuantity();
                final int productId = saleProduct.getId();
                if(quantity > 0){
                    quantity--;
                    saleProduct.setQuantity(quantity);
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            saleProduct.setId(productId);
                            mDb.productItemDao().updateProduct(saleProduct);
                        }

                    });
                }
                else{
                    Toast.makeText(getBaseContext(), R.string.min_value_message,Toast.LENGTH_SHORT).show();
                }

            }
        });


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
        }
        return super.onOptionsItemSelected(item);
    }

    //Insert a product to test the database
    public void insertDummyProduct(){

        final ProductItem productItem = new ProductItem(BRAND,WARRANTY,MANUFACTURE_YEAR,WEIGHT,
                PRICE,QUANTITY,IN_STOCK,NAME,TYPE_PRODUCT, null,null);

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.productItemDao().insertProduct(productItem);
            }
        });

        Toast.makeText(this, R.string.product_added_successfully_message,Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onItemClickListener(int itemId, int clickedItemIndex, boolean isSaleButton) {
        if(isSaleButton){
            reduceQuantityItem(clickedItemIndex);
        }
        else{
            Intent intent = new Intent(MainActivity.this,EditorActivity.class);
            intent.putExtra(EditorActivity.EXTRA_PRODUCT_ID,itemId);
            startActivity(intent);
        }
    }

}

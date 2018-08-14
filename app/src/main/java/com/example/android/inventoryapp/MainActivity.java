package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    @BindView(R.id.fab_product) FloatingActionButton fab;
    @BindView(R.id.list) ListView listView;
    @BindView(R.id.empty_view) TextView emptyView;

    private static final String TAG = MainActivity.class.getSimpleName();

    //private static final int RC_PHOTO_PICKER = 2;

    //private Uri selectedImageUri;

    private static final String downloadImageUrl = "https://cdn4.iconfinder.com/data/icons/seo-accessibility-usability-2-2/256/Usability_Evaluation-512.png";

    private InventoryCursorAdapter adapter;

    private String[] projection = {InventoryEntry.COLUMN_PRODUCT_BRAND,InventoryEntry.COLUMN_PRODUCT_WARRANTY, InventoryEntry.COLUMN_PRODUCT_YEAR_MANUFACTURE,
    InventoryEntry.COLUMN_PRODUCT_WEIGHT, InventoryEntry.COLUMN_PRODUCT_PRICE, InventoryEntry.COLUMN_PRODUCT_QUANTITY, InventoryEntry.COLUMN_PRODUCT_STOCK,
            InventoryEntry.COLUMN_PRODUCT_STOCK,InventoryEntry.COLUMN_PRODUCT_NAME,InventoryEntry.COLUMN_PRODUCT_TYPE,InventoryEntry.COLUMN_PRODUCT_IMAGE_URL};

    private Uri modifyUri;

    //private FirebaseDatabase firebaseDatabase;
    //private DatabaseReference productsDatabaseReference;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //private FirebaseStorage firebaseStorage;
    //private StorageReference productsStorageReference;

    //private StorageReference photoRef;

    private ChildEventListener mChildEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overall_layout);
        ButterKnife.bind(this);

        //firebaseDatabase = FirebaseDatabase.getInstance();
        //productsDatabaseReference = firebaseDatabase.getReference().child("products");

        //firebaseStorage = FirebaseStorage.getInstance();
        //productsStorageReference = firebaseStorage.getReference().child("inventory_photos");

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

        /*fab.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    // TODO: Fire an intent to show an image picker
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/jpeg");
                    intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                    startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
                }
        });*/


       fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,EditorActivity.class);
                startActivity(intent);
                }
        });

       /*fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProductItem productItem = new ProductItem("New Brand",256,"2007",1.56,76.12,8,1,"Product Name",3,downloadImageUrl);
                Map<String, Object> postValues = productItem.toMap();

                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/Dorian2003Mgx" , postValues);

                productsDatabaseReference.updateChildren(childUpdates);
                }
        });*/

        listView.setEmptyView(emptyView);

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
       mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
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

   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            selectedImageUri = data.getData();
            //saveInStorage();
        }

    }*/

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
        values.put(InventoryEntry.COLUMN_PRODUCT_YEAR_MANUFACTURE,"2017");
        values.put(InventoryEntry.COLUMN_PRODUCT_WEIGHT,1.56);
        values.put(InventoryEntry.COLUMN_PRODUCT_PRICE,14.99);
        values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY,5);
        values.put(InventoryEntry.COLUMN_PRODUCT_STOCK,1);
        values.put(InventoryEntry.COLUMN_PRODUCT_NAME,"Product Name");
        values.put(InventoryEntry.COLUMN_PRODUCT_TYPE,3);
        values.put(InventoryEntry.COLUMN_PRODUCT_IMAGE_URL,downloadImageUrl);

        //ProductItem productItem = new ProductItem("Product Brand",360,"2017",1.56,14.99,5,1,"Product Name",3,downloadImageUrl);
        //productsDatabaseReference.push().setValue(productItem);

        getContentResolver().insert(InventoryEntry.CONTENT_URI,values);

    }



    public void deleteEntries(){
        getContentResolver().delete(InventoryEntry.CONTENT_URI, null, null);

    }

/*    private void saveInStorage(){
        photoRef = productsStorageReference.child(selectedImageUri.getLastPathSegment());

        UploadTask uploadTask = photoRef.putFile(selectedImageUri);
        uploadTask.addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(MainActivity.this,"The file was uploaded successfully",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,"Error - The file could not be uploaded",Toast.LENGTH_SHORT).show();
            }
        });

        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                // Continue with the task to get the download URL
                return photoRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri imageDownloadUri = task.getResult();
                    downloadImageUrl = imageDownloadUri.toString();
                } else {
                    // Handle failures
                    // ...
                    Toast.makeText(MainActivity.this,"The size of the image must be below 3 MB",Toast.LENGTH_LONG).show();
                }
            }
        });
    }*/
}

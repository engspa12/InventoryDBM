package com.example.android.inventoryapp.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import com.example.android.inventoryapp.AppDatabase;
import com.example.android.inventoryapp.ProductItem;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private static final String TAG = MainViewModel.class.getSimpleName();

    private LiveData<List<ProductItem>> listProductItems;

    public MainViewModel(@NonNull Application application){
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        listProductItems = database.productItemDao().loadAllProducts();
    }

    public LiveData<List<ProductItem>> getListProductItems() {
        return listProductItems;
    }
}

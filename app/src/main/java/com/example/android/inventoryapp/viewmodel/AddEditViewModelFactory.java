package com.example.android.inventoryapp.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.example.android.inventoryapp.AppDatabase;

public class AddEditViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppDatabase mDb;
    private final int mProductItemId;

    public AddEditViewModelFactory(AppDatabase database, int id){
        mDb = database;
        mProductItemId = id;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AddEditViewModel(mDb,mProductItemId);
    }


}

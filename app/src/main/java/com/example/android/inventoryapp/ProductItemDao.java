package com.example.android.inventoryapp;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface ProductItemDao {

    @Query("SELECT * FROM products ORDER BY id")
    List<ProductItem> loadAllProducts();

    @Insert
    void insertProduct(ProductItem productItem);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateProduct(ProductItem productItem);

    @Delete
    void deleteProduct(ProductItem productItem);

}

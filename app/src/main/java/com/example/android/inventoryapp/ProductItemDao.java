package com.example.android.inventoryapp;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ProductItemDao {

    @Query("SELECT * FROM products ORDER BY id")
    LiveData<List<ProductItem>> loadAllProducts();

    @Insert
    void insertProduct(ProductItem productItem);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateProduct(ProductItem productItem);

    @Delete
    void deleteProduct(ProductItem productItem);

    @Query("DELETE FROM products")
    void deleteAllProducts();

    @Query("SELECT * FROM products WHERE id = :id")
    LiveData<ProductItem> loadProductById(int id);
}

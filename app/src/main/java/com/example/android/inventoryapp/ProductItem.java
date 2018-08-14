package com.example.android.inventoryapp;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

@Entity(tableName = "products")
public class ProductItem {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String brand;
    private int warranty;
    @ColumnInfo(name = "manufacture_year")
    private String manufactureYear;
    private double weight;
    private double price;
    private int quantity;
    @ColumnInfo(name = "in_stock")
    private int inStock;
    private String name;
    private int type;
    @ColumnInfo(name = "url_image")
    private String urlImage;


    @Ignore
    public ProductItem(String brand, int warranty, String manufactureYear, double weight, double price, int quantity, int inStock
            , String name, int type, String urlImage){
        this.brand = brand;
        this.warranty = warranty;
        this.manufactureYear = manufactureYear;
        this.weight = weight;
        this.price = price;
        this.quantity = quantity;
        this.inStock = inStock;
        this.name = name;
        this.type = type;
        this.urlImage = urlImage;
    }

    public ProductItem(int id, String brand, int warranty, String manufactureYear, double weight, double price, int quantity, int inStock
            , String name, int type, String urlImage){
        this.id = id;
        this.brand = brand;
        this.warranty = warranty;
        this.manufactureYear = manufactureYear;
        this.weight = weight;
        this.price = price;
        this.quantity = quantity;
        this.inStock = inStock;
        this.name = name;
        this.type = type;
        this.urlImage = urlImage;
    }

/*    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("brand", brand);
        result.put("warranty", warranty);
        result.put("yearManufacture", yearManufacture);
        result.put("weight", weight);
        result.put("price", price);
        result.put("quantity", quantity);
        result.put("inStock", inStock);
        result.put("name", name);
        result.put("type", type);
        result.put("urlImage", urlImage);

        return result;
    }*/

    public int getId(){return id;}

    public void setId(int id){this.id = id;}

    public String getBrand(){
        return brand;
    }

    public int getWarranty(){
        return warranty;
    }

    public String getManufactureYear(){
        return manufactureYear;
    }

    public double getWeight(){
        return weight;
    }

    public double getPrice(){
        return price;
    }

    public int getQuantity(){
        return quantity;
    }

    public int getInStock(){
        return inStock;
    }

   public String getProductName(){
        return name;
   }

   public int getProductType(){
        return type;
   }

   public String getUrlImage(){
        return urlImage;
   }


}

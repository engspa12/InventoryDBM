package com.example.android.inventoryapp;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

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
    private String urlImageLocation;


    @Ignore
    public ProductItem(String brand, int warranty, String manufactureYear, double weight, double price, int quantity, int inStock
            , String name, int type, String urlImage, String urlImageLocation){
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
        this.urlImageLocation = urlImageLocation;
    }

    public ProductItem(int id, String brand, int warranty, String manufactureYear, double weight, double price, int quantity, int inStock
            , String name, int type, String urlImage, String urlImageLocation){
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
        this.urlImageLocation = urlImageLocation;
    }


    public int getId(){return id;}

    public void setId(int id){this.id = id;}

    public String getBrand(){
        return brand;
    }

    public void setBrand(String brand){this.brand = brand;}

    public int getWarranty(){
        return warranty;
    }

    public void setWarranty(int warranty){this.warranty = warranty;}

    public String getManufactureYear(){
        return manufactureYear;
    }

    public void setManufactureYear(String manufactureYear){this.manufactureYear = manufactureYear;}

    public double getWeight(){
        return weight;
    }

    public void setWeight(double weight){this.weight = weight;}

    public double getPrice(){
        return price;
    }

    public void setPrice(double price){this.price = price;}

    public int getQuantity(){
        return quantity;
    }

    public void setQuantity(int quantity){this.quantity = quantity;}

    public int getInStock(){
        return inStock;
    }

    public void setInStock(int inStock){this.inStock = inStock;}

   public String getName(){
        return name;
   }

   public void setName(String name){this.name = name;}

   public int getType(){
        return type;
   }

    public void setType(int type){this.type = type;}

   public String getUrlImage(){
        return urlImage;
   }

    public void setUrlImage(String urlImage){this.urlImage = urlImage;}

    public String getUrlImageLocation(){return urlImageLocation;}

    public void setUrlImageLocation(String urlImageLocation){this.urlImageLocation = urlImageLocation;}


}

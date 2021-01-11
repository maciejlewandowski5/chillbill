package com.example.chillbill.model;

import java.io.Serializable;

public class Product implements Serializable {
    private String name;
    private float price;
    private float pricePerName;
    private Category category;
    private float quantity;
    private float discount;

    public Product() {
    }

    public void setPricePerName(float pricePerName) {
        this.pricePerName = pricePerName;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    // for firestor object conversions only
    public Product(String name, float price, float pricePerName, Category category, float quantity, float discount) {
        this.name = name;
        this.price = price;
        this.pricePerName = pricePerName;
        this.category = category;
        this.quantity = quantity;
        this.discount = discount;
    }

    public Product(String name, float pricePerName, float quantity, Category category) {
        this.name = name;
        this.price = pricePerName * quantity;
        this.quantity = quantity;
        this.category = category;
        this.discount=0;
        this.pricePerName = pricePerName;
    }

    public String getName() {
        return name;
    }

    public float getPrice() {
        return price;
    }

    public Category getCategory() {
        return category;
    }

    public String getCategoryString(){
        String results="";

        if(category==Category.BLUE){
            results = "blue";
        }else if(category==Category.GREEN){
            results = "green";
        }else if(category==Category.ORANGE){
            results = "orange";
        }else if(category==Category.PURPLE){
            results = "purple";
        }else{
            results = "yellow";
        };
        return results;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }
}


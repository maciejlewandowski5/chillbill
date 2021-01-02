package com.example.chillbill.model;

import java.io.Serializable;

public class Product implements Serializable {
    private String name;
    private float price;
    private Category category;
    private float quantity;

    public Product(String name, float price,float quantity, Category category) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
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
}


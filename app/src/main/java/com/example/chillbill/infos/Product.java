package com.example.chillbill.infos;

import java.io.Serializable;

public class Product implements Serializable {
    private String name;
    private float price;
    private Category category;

    public Product(String name, float price, Category category) {
        this.name = name;
        this.price = price;
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
}


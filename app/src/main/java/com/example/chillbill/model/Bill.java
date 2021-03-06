package com.example.chillbill.model;


import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Bill implements Serializable {
    @DocumentId
    private String id;
    @ServerTimestamp
    private Date timeStamp;
    private String shopName;
    private float totalAmount;
    private Date date;
    private ArrayList<Double> categoryPercentage;
    private String savingsJar;
    private ArrayList<Product> productList;
    private boolean totalAmountMatchesSumProductPrices;


    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public Bill() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public void setTotalAmount(float totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setCategoryPercentage(ArrayList<Double> categoryPercentage) {
        this.categoryPercentage = categoryPercentage;
    }

    public void setSavingsJar(String savingsJar) {
        this.savingsJar = savingsJar;
    }

    public void setProductList(ArrayList<Product> productList) {
        this.productList = productList;
    }

    public void setTotalAmountMatchesSumProductPrices(boolean totalAmountMatchesSumProductPrices) {
        this.totalAmountMatchesSumProductPrices = totalAmountMatchesSumProductPrices;
    }

    // for firestore conversions only
    public Bill(String shopName, float totalAmount, Date date, ArrayList<Double> categoryPercentage, String savingsJar, ArrayList<Product> productList, boolean totalAmountMatchesSumProductPrices) {
        this.shopName = shopName;
        this.totalAmount = totalAmount;
        this.date = date;
        this.categoryPercentage = categoryPercentage;
        this.savingsJar = savingsJar;
        this.productList = productList;
        this.totalAmountMatchesSumProductPrices = totalAmountMatchesSumProductPrices;
    }

    public Bill(String shopName, float totalAmount, Date date) {
        this.shopName = shopName;
        this.totalAmount = totalAmount;
        this.date = date;
        categoryPercentage = new ArrayList<>();
        categoryPercentage.add(20d);
        categoryPercentage.add(20d);
        categoryPercentage.add(20d);
        categoryPercentage.add(20d);
        categoryPercentage.add(20d);

        productList = new ArrayList<Product>();
        savingsJar = "##noJar";
        totalAmountMatchesSumProductPrices = false;

    }

    public String getShopName() {
        return shopName;
    }

    public float getTotalAmount() {
        return totalAmount;
    }

    public Date getDate() {
        return date;
    }

    public ArrayList<Double> getCategoryPercentage() {
        return categoryPercentage;
    }

    public String getSavingsJar() {
        return savingsJar;
    }

    public ArrayList<Product> getProductList() {
        return productList;
    }

    public boolean isTotalAmountMatchesSumProductPrices() {
        return totalAmountMatchesSumProductPrices;
    }

    public void addProduct(Product product) {

        productList.add(product);
        float price = 0;
        for (Product product1 : productList) {
            price += product1.getPrice();
        }
        if (price == this.totalAmount) {
            totalAmountMatchesSumProductPrices = true;
        }
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append("Shop name:  " + shopName+ "\n");
        result.append("Date:  " + date.toString() + "\n");
        result.append("Sum:   " + totalAmount + "\n");
        result.append("Products:   " + "\n");
        for(Product product :productList){
            result.append("     " + product.getName() + "        " + product.getPrice() + "\n");
        }
        return  result.toString();
    }

    public void updateCategories() {
        ArrayList<Double> categoriesPercentage = new ArrayList<Double>() {
            {
                add(0.0); //purple
                add(0.0); //yellow
                add(0.0); //green
                add(0.0); //orange
                add(0.0); //blue
            }
        };
        double total = 0.0;

        for (int i = 0; i < productList.size(); i++) {
            Category category = productList.get(i).getCategory();
            float currentPrice = productList.get(i).getPrice();
            total += currentPrice;

            if (category== Category.PURPLE) {
                categoriesPercentage.set(0, currentPrice + categoriesPercentage.get(0));
            } else if (category== Category.YELLOW) {
                categoriesPercentage.set(1, currentPrice + categoriesPercentage.get(1));
            } else if (category== Category.GREEN) {
                categoriesPercentage.set(2, currentPrice + categoriesPercentage.get(2));
            } else if (category== Category.ORANGE) {
                categoriesPercentage.set(3, currentPrice + categoriesPercentage.get(3));
            } else if (category== Category.BLUE) {
                categoriesPercentage.set(4, currentPrice + categoriesPercentage.get(4));
            }
        }

        for (int i = 0; i < 5; i++) {
            categoriesPercentage.set(i, categoriesPercentage.get(i) * 100 / total);
            System.out.println("QWERTYUIOP");
            System.out.println(categoriesPercentage.get(i));
        }

        this.categoryPercentage = categoriesPercentage;
    }
}

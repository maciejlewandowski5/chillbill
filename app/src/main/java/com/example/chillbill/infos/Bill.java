package com.example.chillbill.infos;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Bill implements Serializable {
    private String shopName;
    private float totalAmount;
    private Date date;
    private float[] categoryPercentage;
    private String savingsJar;
    private ArrayList<Product> productList;
    private boolean totalAmountMatchesSumProductPrices;

    public Bill(String shopName, float totalAmount, Date date) {
        this.shopName = shopName;
        this.totalAmount = totalAmount;
        this.date = date;
        categoryPercentage = new float[]{20, 20, 20, 20, 20};
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

    public float[] getCategoryPercentage() {
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
}

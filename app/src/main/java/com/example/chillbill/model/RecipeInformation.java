package com.example.chillbill.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class RecipeInformation implements Serializable {
    private String title;
    private String description;
    private URL imageURL;
    private URL recipeURL;
    private ArrayList<String> products;

    public RecipeInformation(String title, String description, URL imageURL, URL recipeURL) {
        this.title = title;
        this.description = description;
        this.imageURL = imageURL;
        this.recipeURL = recipeURL;
        products = new ArrayList<>();
    }

    public static RecipeInformation instantiate(JSONObject jsonObject) {
        RecipeInformation recipeInformation = null;
        String title = "";
        String description = "";
        String recipeURL = "";
        String imageURL = "";
        JSONArray jsonArray = new JSONArray();
        try {
            title = jsonObject.getString("name");
            description = jsonObject.getString("description");
            recipeURL = jsonObject.getString("link");
            imageURL = jsonObject.getString("image");
            jsonArray = jsonObject.getJSONArray("ingredients");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            recipeInformation = new RecipeInformation(title, description, new URL(imageURL), new URL(recipeURL));
            for (int j = 0; j < jsonArray.length(); j++) {
                try {
                    recipeInformation.addProduct(jsonArray.getString(j));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (MalformedURLException e) {
            try {
                recipeInformation = new RecipeInformation(title, description, new URL("https:" + imageURL), new URL("https://www.mojegotowanie.pl/search?q=" + title));
                for (int j = 0; j < jsonArray.length(); j++) {
                    try {
                        recipeInformation.addProduct(jsonArray.getString(j));
                    } catch (JSONException o) {
                        o.printStackTrace();
                    }
                }
            } catch (MalformedURLException malformedURLException) {
                malformedURLException.printStackTrace();
            }
        }

        return recipeInformation;
    }

    public void addProduct(String product) {
        products.add(product);
    }

    public ArrayList<String> getProducts() {
        return products;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public URL getImageURL() {
        return imageURL;
    }

    public URL getRecipeURL() {
        return recipeURL;
    }

    public String getShortDesc() {
        if (description.length() <= 150) {
            return description;
        }
        return description.substring(150);
    }
}

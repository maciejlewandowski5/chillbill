package com.example.chillbill;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.chillbill.model.RecipeInformation;

import java.util.ArrayList;

public class Recipe extends AppCompatActivity {
    RecipeInformation recipeInformation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recepe);

        LinearLayout linearLayout = findViewById(R.id.list_contaner);
        Intent intent = getIntent();

        String ARG_RECEP_PARAM_OUT = "RECEPINFO";
        recipeInformation = (RecipeInformation) intent.getSerializableExtra(ARG_RECEP_PARAM_OUT);

        if (recipeInformation != null) {
            Fragment foodItem =  FoodItem.newInstance(recipeInformation.getTitle(), recipeInformation.getImageURL());
            androidx.fragment.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment5, foodItem);
            transaction.commit();


            transaction = getSupportFragmentManager().beginTransaction();
            ArrayList<String> products;
            products = recipeInformation.getProducts();

            int i = 0;
            for (String product : products) {
                Fragment ingredient = ShopItemList.newInstance(product, i % 2 == 0);
                transaction.add(linearLayout.getId(), ingredient);
                i++;
            }
            transaction.commit();

            TextView recipeText = findViewById(R.id.recepeText);

            recipeText.setText(recipeInformation.getShortDesc());
        }
    }

    public void openURL(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(recipeInformation.getRecipeURL().toString()));
        startActivity(browserIntent);
    }
}
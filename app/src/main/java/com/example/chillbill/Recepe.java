package com.example.chillbill;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Recepe extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recepe);

        LinearLayout linearLayout = findViewById(R.id.list_contaner);

        Fragment foodItem = new FoodItem().newInstance("Sałatka z Avocado",R.drawable.food_item_background_image);

        androidx.fragment.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment5,foodItem);
        transaction.commit();


        transaction = getSupportFragmentManager().beginTransaction();



        for(int i=0;i<10;i++) {
            Fragment ingredient = new ShopItemList().newInstance("Pomidory 35kg" + i,i%2==0);

            transaction.add(linearLayout.getId(),ingredient);
        }
        transaction.commit();

        TextView recepeText = findViewById(R.id.recepeText);

        recepeText.setText("Przykładowy tekst recepturyPrzykładowy " +
                "" +
                "Przykładowy tekst receptury" +
                "Przykładowy tekst receptury" +
                "Przykładowy tekst receptury" +
                "Przykładowy tekst receptury" +
                "Przykładowy tekst receptury" +
                "Przykładowy tekst receptury" +
                "tekst recepturyPrzykładowy tekst receptury");
    }
}
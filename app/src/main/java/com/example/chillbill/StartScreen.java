package com.example.chillbill;

import androidx.appcompat.app.AppCompatActivity;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.View;
import android.widget.ImageView;

import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

public class StartScreen extends AppCompatActivity {

    CarouselView carouselView;

    int[] sampleImages = {R.drawable.image_1, R.drawable.image_2, R.drawable.image_3};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);

        HistoryItem first = HistoryItem.newInstance("LIDL",621.50f,20,10,20,40,10);
        HistoryItem second = HistoryItem.newInstance("Biedronka",128.12f,10,20,30,30,10);
        HistoryItem third = HistoryItem.newInstance("Empik",12.50f,5,25,35,20,15);

        FitItem fitItem = FitItem.newInstance("Aktywnosc fizyczna","Lorem impsum dolores . . .",R.drawable.image_2);
        FitItem fitItem2 = FitItem.newInstance("Aktywnosc fizyczna","Lorem impsum dolores . . .",R.drawable.image_2);
        FitItem fitItem3 = FitItem.newInstance("Aktywnosc fizyczna","Lorem impsum dolores . . .",R.drawable.image_2);

        FoodItem foodItem1 = FoodItem.newInstance("Rzepak",R.drawable.food_item_background_image);
        FoodItem foodItem2 = FoodItem.newInstance("Szczaw",R.drawable.food_item_background_image);
        FoodItem foodItem3 = FoodItem.newInstance("Pomidorowa",R.drawable.food_item_background_image);
        FoodItem foodItem4 = FoodItem.newInstance("Salatka z Avocado",R.drawable.food_item_background_image);
        FoodItem foodItem5 = FoodItem.newInstance("Zupa paprykowa",R.drawable.food_item_background_image);
        FoodItem foodItem6 = FoodItem.newInstance("Cytrusy",R.drawable.food_item_background_image);
        FoodItem foodItem7 = FoodItem.newInstance("Kiszonki",R.drawable.food_item_background_image);


        androidx.fragment.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.fragment2,first);
        transaction.replace(R.id.fragment3,second);
        transaction.replace(R.id.fragment4,third);

        transaction.replace(R.id.fragment,fitItem);
        transaction.replace(R.id.fragment1,fitItem2);
        transaction.replace(R.id.fragment12,fitItem3);

        transaction.replace(R.id.fragment6,foodItem1);
        transaction.replace(R.id.fragment7,foodItem2);
        transaction.replace(R.id.fragment8,foodItem3);
        transaction.replace(R.id.fragment9,foodItem4);
        transaction.replace(R.id.fragment10,foodItem5);
        transaction.replace(R.id.fragment13,foodItem6);
        transaction.replace(R.id.fragment11,foodItem7);
        transaction.commit();





    }
    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            imageView.setImageResource(sampleImages[position]);
        }
    };

}
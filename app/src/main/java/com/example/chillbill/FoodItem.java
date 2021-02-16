package com.example.chillbill;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.chillbill.model.RecipeInformation;
import com.squareup.picasso.Picasso;


import java.io.Serializable;
import java.net.URL;


public class FoodItem extends Fragment {

    private static final String SERIALIZABLE = "serializable";

    private String title;
    private URL imageId;
    ImageView background;

    public FoodItem() {
        // Required empty public constructor
    }


    public static FoodItem newInstance(Serializable ...serializable) {
        FoodItem fragment = new FoodItem();
        Bundle args = new Bundle();
        args.putSerializable(SERIALIZABLE,serializable[0]);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            RecipeInformation recipeInformation = (RecipeInformation) getArguments().getSerializable(SERIALIZABLE);
            title = recipeInformation.getTitle();
            imageId = recipeInformation.getImageURL();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_food_item, container, false);

        background = rootView.findViewById(R.id.food_image_background);

        if(imageId != null) {
            Picasso.get().load(imageId.toString()).into(background);
        }
        TextView titleView = rootView.findViewById(R.id.title_food);
        titleView.setText(title);

        return rootView;
    }

    public ImageView getBackground(){
        return background;
    }


}
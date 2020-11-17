package com.example.chillbill;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FoodItem#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FoodItem extends Fragment {
    private String title;
    private int imageId;


    public FoodItem() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static FoodItem newInstance(String titleFood, int imageIdFood) {
        FoodItem fragment = new FoodItem();
        Bundle args = new Bundle();
        args.putString("titleFood", titleFood);
        args.putInt("imageIdFood", imageIdFood);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString("titleFood");
            imageId = getArguments().getInt("imageIdFood");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_food_item, container, false);

        ImageView background = rootView.findViewById(R.id.food_image_background);
        background.setImageResource(imageId);

        TextView titleView = rootView.findViewById(R.id.title_food);

        titleView.setText(title);

        return rootView;
    }
}
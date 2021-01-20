package com.example.chillbill;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.squareup.picasso.Picasso;


import java.net.URL;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FoodItem#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FoodItem extends Fragment {
    private String title;
    private URL imageId;
    ImageView background;

    public FoodItem() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static FoodItem newInstance(String titleFood, URL imageIdFood) {
        FoodItem fragment = new FoodItem();
        Bundle args = new Bundle();
        args.putString("titleFood", titleFood);
        args.putSerializable("imageIdFood", imageIdFood);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString("titleFood");
            imageId = (URL)getArguments().getSerializable("imageIdFood");
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
package com.example.chillbill;


import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;


import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryItem#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryItem extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    ProgressBar purple;
    ProgressBar yellow;
    ProgressBar green;
    ProgressBar orange;
    ProgressBar blue;
    float purpleFloat;
    float yellowFloat;
    float greenFloat;
    float orangeFloat;
    float blueFloat;
    String name;
    float price;
    Date date;
    String category;


    public HistoryItem() {
        // Required empty public constructor
    }

    public static HistoryItem newInstance(String name,float price,float purpleCategory, float yellowCategory, float greenCategory, float orangeCategory, float blueCategory) {
        HistoryItem fragment = new HistoryItem();
        Bundle args = new Bundle();



        // Shoud be in percentage where 100% is purpleFloat + ... + blueFloat
        args.putString("name",name);
        args.putFloat("price",price);
        args.putFloat("purpleFloat", purpleCategory);
        args.putFloat("yellowFloat", yellowCategory);
        args.putFloat("greenFloat", greenCategory);
        args.putFloat("orangeFloat", orangeCategory);
        args.putFloat("blueFloat", blueCategory);


        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        float sum = 0;

        float barWidthInDp = 338.f;
        if (getArguments() != null) {
            name = getArguments().getString("name");
            price = getArguments().getFloat("price");
            purpleFloat = barWidthInDp*getArguments().getFloat("purpleFloat")/100.0f;
            yellowFloat = barWidthInDp*getArguments().getFloat("yellowFloat")/100.0f;
            greenFloat = barWidthInDp*getArguments().getFloat("greenFloat")/100.0f;
            orangeFloat = barWidthInDp*getArguments().getFloat("orangeFloat")/100.0f;
            blueFloat = barWidthInDp*getArguments().getFloat("blueFloat")/100.0f;
            sum = purpleFloat + yellowFloat + greenFloat + orangeFloat + blueFloat;
        }


    }

    // 268dp is total width of all progress bars
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View RootView = inflater.inflate(R.layout.fragment_history_item, container, false);
        // Inflate the layout for this fragment
        purple = (ProgressBar) RootView.findViewById(R.id.progressBar5);
        yellow = (ProgressBar) RootView.findViewById(R.id.progressBar4);
        green = (ProgressBar) RootView.findViewById(R.id.progressBar2);
        orange = (ProgressBar) RootView.findViewById(R.id.progressBar3);
        blue = (ProgressBar) RootView.findViewById(R.id.progressBar7);


        float heightDp = 5f;
        float heightPx = DptoPx(heightDp);
        ConstraintLayout constraintLayout = RootView.findViewById(R.id.constrainViewHistoryFragment);

        /// Purple
        purple.setLayoutParams(new ConstraintLayout.LayoutParams((int) DptoPx(purpleFloat),(int)heightPx));
        ConstraintSet purpleConstrainSet = new ConstraintSet();
        purpleConstrainSet.clone(constraintLayout);
        purpleConstrainSet.connect(purple.getId(),ConstraintSet.BOTTOM,ConstraintSet.PARENT_ID,ConstraintSet.BOTTOM);
        purpleConstrainSet.connect(purple.getId(),ConstraintSet.START,ConstraintSet.PARENT_ID,ConstraintSet.START);
        purpleConstrainSet.applyTo(constraintLayout);

        /// Yellow
        yellow.setLayoutParams(new ConstraintLayout.LayoutParams((int) DptoPx(yellowFloat), (int) heightPx));
        ConstraintSet yellowConstraintSet = new ConstraintSet();
        yellowConstraintSet.clone(constraintLayout);
        yellowConstraintSet.connect(yellow.getId(),ConstraintSet.BOTTOM,ConstraintSet.PARENT_ID,ConstraintSet.BOTTOM);
        yellowConstraintSet.connect(yellow.getId(),ConstraintSet.START,purple.getId(),ConstraintSet.END);
        yellowConstraintSet.applyTo(constraintLayout);

        /// Green
        green.setLayoutParams(new ConstraintLayout.LayoutParams((int) DptoPx(greenFloat), (int) heightPx));
        ConstraintSet greenConstraintSet = new ConstraintSet();
        greenConstraintSet.clone(constraintLayout);
        greenConstraintSet.connect(green.getId(),ConstraintSet.BOTTOM,ConstraintSet.PARENT_ID,ConstraintSet.BOTTOM);
        greenConstraintSet.connect(green.getId(),ConstraintSet.START,yellow.getId(),ConstraintSet.END);
        greenConstraintSet.applyTo(constraintLayout);

        /// Orange
        orange.setLayoutParams(new ConstraintLayout.LayoutParams((int) DptoPx(orangeFloat), (int) heightPx));
        ConstraintSet orangeConstraintSet = new ConstraintSet();
        orangeConstraintSet.clone(constraintLayout);
        orangeConstraintSet.connect(orange.getId(),ConstraintSet.BOTTOM,ConstraintSet.PARENT_ID,ConstraintSet.BOTTOM);
        orangeConstraintSet.connect(orange.getId(),ConstraintSet.START,green.getId(),ConstraintSet.END);
        orangeConstraintSet.applyTo(constraintLayout);

        /// Blue
        blue.setLayoutParams(new ConstraintLayout.LayoutParams((int) DptoPx(blueFloat), (int) heightPx));
        ConstraintSet blueConstraintSet = new ConstraintSet();
        blueConstraintSet.clone(constraintLayout);
        blueConstraintSet.connect(blue.getId(),ConstraintSet.BOTTOM,ConstraintSet.PARENT_ID,ConstraintSet.BOTTOM);
        blueConstraintSet.connect(blue.getId(),ConstraintSet.START,orange.getId(),ConstraintSet.END);
        blueConstraintSet.applyTo(constraintLayout);

        TextView priceTextView = RootView.findViewById(R.id.price);
        TextView titleTextView = RootView.findViewById(R.id.shop_name);


        priceTextView.setText(String.format("%.2f", price));
        titleTextView.setText(name);


        return RootView;


    }

    int DptoPx(float dp){
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}
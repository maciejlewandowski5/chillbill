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

import com.example.chillbill.model.Bill;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryItemExtended#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryItemExtended extends Fragment {

    // TODO: Rename parameter arguments, choose names that match


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

    public static Fragment newInstance(Serializable ...serializable) {
        HistoryItemExtended fragment = new HistoryItemExtended();
        Bundle args = new Bundle();

        args.putSerializable("bill",serializable[0]);
        fragment.setArguments(args);
        return fragment;
    }

    public HistoryItemExtended() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        float sum = 0;

        float barWidthInDp = 338.f;
        if (getArguments() != null) {
            Bill bill = (Bill)getArguments().getSerializable("bill");
            name = bill.getShopName();
            price = bill.getTotalAmount();
            purpleFloat = (float) barWidthInDp*(bill.getCategoryPercentage().get(0).floatValue())/100.0f;
            yellowFloat = (float) barWidthInDp*(bill.getCategoryPercentage().get(1).floatValue())/100.0f;
            greenFloat = (float) barWidthInDp*(bill.getCategoryPercentage().get(2).floatValue())/100.0f;
            orangeFloat = (float) barWidthInDp*(bill.getCategoryPercentage().get(3).floatValue())/100.0f;
            blueFloat = (float) barWidthInDp*(bill.getCategoryPercentage().get(4).floatValue())/100.0f;
            sum = purpleFloat + yellowFloat + greenFloat + orangeFloat + blueFloat;


            date = bill.getDate();

            if(date == null){
                date= new Date();

            }
            category = bill.getSavingsJar();
            sum = purpleFloat + yellowFloat + greenFloat + orangeFloat + blueFloat;

            if (purpleFloat == 0) {
                purpleFloat = 1;
            }
            if (yellowFloat == 0) {
                yellowFloat = 1;
            }
            if (orangeFloat == 0) {
                orangeFloat = 1;
            }
            if (greenFloat == 0) {
                greenFloat = 1;
            }
            if (blueFloat == 0) {
                blueFloat = 1;
            }



        }


    }

    // 268dp is total width of all progress bars
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View RootView = inflater.inflate(R.layout.fragment_history_item__extended, container, false);
        // Inflate the layout for this fragment
        purple = (ProgressBar) RootView.findViewById(R.id.progressBar115);
        yellow = (ProgressBar) RootView.findViewById(R.id.progressBar114);
        green = (ProgressBar) RootView.findViewById(R.id.progressBar113);
        orange = (ProgressBar) RootView.findViewById(R.id.progressBar112);
        blue = (ProgressBar) RootView.findViewById(R.id.progressBar111);


        float heightDp = 5f;
        float heightPx = DptoPx(heightDp);
        ConstraintLayout constraintLayout = RootView.findViewById(R.id.history_item_extended_container);

        /// Purple
        purple.setLayoutParams(new ConstraintLayout.LayoutParams((int) DptoPx(purpleFloat), (int) heightPx));
        ConstraintSet purpleConstrainSet = new ConstraintSet();
        purpleConstrainSet.clone(constraintLayout);
        purpleConstrainSet.connect(purple.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        purpleConstrainSet.connect(purple.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        purpleConstrainSet.applyTo(constraintLayout);

        /// Yellow
        yellow.setLayoutParams(new ConstraintLayout.LayoutParams((int) DptoPx(yellowFloat), (int) heightPx));
        ConstraintSet yellowConstraintSet = new ConstraintSet();
        yellowConstraintSet.clone(constraintLayout);
        yellowConstraintSet.connect(yellow.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        yellowConstraintSet.connect(yellow.getId(), ConstraintSet.START, purple.getId(), ConstraintSet.END);
        yellowConstraintSet.applyTo(constraintLayout);

        /// Green
        green.setLayoutParams(new ConstraintLayout.LayoutParams((int) DptoPx(greenFloat), (int) heightPx));
        ConstraintSet greenConstraintSet = new ConstraintSet();
        greenConstraintSet.clone(constraintLayout);
        greenConstraintSet.connect(green.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        greenConstraintSet.connect(green.getId(), ConstraintSet.START, yellow.getId(), ConstraintSet.END);
        greenConstraintSet.applyTo(constraintLayout);

        /// Orange
        orange.setLayoutParams(new ConstraintLayout.LayoutParams((int) DptoPx(orangeFloat), (int) heightPx));
        ConstraintSet orangeConstraintSet = new ConstraintSet();
        orangeConstraintSet.clone(constraintLayout);
        orangeConstraintSet.connect(orange.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        orangeConstraintSet.connect(orange.getId(), ConstraintSet.START, green.getId(), ConstraintSet.END);
        orangeConstraintSet.applyTo(constraintLayout);

        /// Blue
        blue.setLayoutParams(new ConstraintLayout.LayoutParams((int) DptoPx(blueFloat), (int) heightPx));
        ConstraintSet blueConstraintSet = new ConstraintSet();
        blueConstraintSet.clone(constraintLayout);
        blueConstraintSet.connect(blue.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        blueConstraintSet.connect(blue.getId(), ConstraintSet.START, orange.getId(), ConstraintSet.END);
        blueConstraintSet.applyTo(constraintLayout);

        TextView priceTextView = RootView.findViewById(R.id.price);
        TextView titleTextView = RootView.findViewById(R.id.shop_name);
        TextView categoryTextView = RootView.findViewById(R.id.category_history_item);
        TextView dateTextView = RootView.findViewById(R.id.date_history_item);


        priceTextView.setText(String.format("%.2f", price));
        titleTextView.setText(name);

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        categoryTextView.setText(category);
        if(date==null){
            date = new Date();
        }
        dateTextView.setText(formatter.format(date));


        return RootView;


    }


    int DptoPx(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }


}
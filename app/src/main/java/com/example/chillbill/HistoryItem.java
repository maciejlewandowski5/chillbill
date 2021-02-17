package com.example.chillbill;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import com.example.chillbill.helpers.Utils;
import com.example.chillbill.model.Bill;

import java.io.Serializable;
import java.util.ArrayList;


public class HistoryItem extends Fragment {

//338
    private static final float BAR_WIDTH_DP = 338.f;
    private static final float BAR_HEIGHT_DP = 5.f;
    protected static final String SERIALIZABLE = "billHistItem";

    private String name;
    private float price;
    private ArrayList<Double> categoryWidth;

    ArrayList<ProgressBar> progressBars;


    public HistoryItem() {
        // Required empty public constructor
    }

    public static HistoryItem newInstance(Serializable... bill) {
        HistoryItem fragment = new HistoryItem();
        Bundle args = new Bundle();
        args.putSerializable(SERIALIZABLE, bill[0]);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        categoryWidth = new ArrayList<>();
        if (getArguments() != null) {
            Bill bill = (Bill) getArguments().getSerializable(SERIALIZABLE);
            name = bill.getShopName();
            price = bill.getTotalAmount();
            for (double percent : bill.getCategoryPercentage()) {
                categoryWidth.add(BAR_WIDTH_DP * (percent) / 100.0f*2);
            }
        }
        trimLength();

        progressBars = new ArrayList<>();
    }



    // 268dp is total width of all progress bars
    @SuppressLint("DefaultLocale") // For displaying price
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View RootView = inflater.inflate(R.layout.fragment_history_item, container, false);

        // Initialize views
        initializeProgressBars(RootView);

        TextView priceTextView = RootView.findViewById(R.id.price);
        TextView titleTextView = RootView.findViewById(R.id.shop_name);


        // Set values to views
        ConstraintLayout constraintLayout = RootView.findViewById(R.id.constrainViewHistoryFragment);

        setProgressBars(RootView);

        priceTextView.setText(String.format("%.2f", price));
        titleTextView.setText(name);


        return RootView;
    }



    @Override
    public void onResume() {
        super.onResume();
    }
}
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

import com.example.chillbill.helpers.HistoryItemHelper;
import com.example.chillbill.helpers.Utils;
import com.example.chillbill.model.Bill;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;

public class HistoryItem extends Fragment {

    HistoryItemHelper historyItemHelper;

    public HistoryItem() {
        // Required empty public constructor
    }

    public static HistoryItem newInstance(Serializable... bill) {
        HistoryItem fragment = new HistoryItem();
        Bundle args = new Bundle();
        args.putSerializable(HistoryItemHelper.SERIALIZABLE, bill[0]);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        historyItemHelper = new HistoryItemHelper();
        if (getArguments() != null) {
            Bill bill = (Bill) getArguments().getSerializable(HistoryItemHelper.SERIALIZABLE);
            historyItemHelper.setName(bill.getShopName());
            historyItemHelper.setPrice(bill.getTotalAmount());
            for (double percent : bill.getCategoryPercentage()) {
                historyItemHelper.getCategoryWidth().add(HistoryItemHelper.BAR_WIDTH_DP * (percent) / 100.0f*2);
            }
        }
        historyItemHelper.trimLength();
    }

    public ArrayList<ProgressBar> initializeProgressBars(View rootView){
        ArrayList<ProgressBar> progressBars = new ArrayList<>();
        progressBars.add(rootView.findViewById(R.id.progressBar5));
        progressBars.add(rootView.findViewById(R.id.progressBar4));
        progressBars.add(rootView.findViewById(R.id.progressBar2));
        progressBars.add(rootView.findViewById(R.id.progressBar3));
        progressBars.add(rootView.findViewById(R.id.progressBar7));
        return progressBars;
    }

    // 268dp is total width of all progress bars
    @SuppressLint("DefaultLocale") // For displaying price
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history_item, container, false);

        // Initialize views
        historyItemHelper.setProgressBars(initializeProgressBars(rootView));

        TextView priceTextView = rootView.findViewById(R.id.price);
        TextView titleTextView = rootView.findViewById(R.id.shop_name);
        ConstraintLayout constraintLayout = rootView.findViewById(R.id.constrainViewHistoryFragment);

        // Set values to views
        historyItemHelper.modelProgressBars(rootView,constraintLayout);
        priceTextView.setText(String.format(Locale.getDefault(),"%.2f", historyItemHelper.getPrice()));
        titleTextView.setText(historyItemHelper.getName());

        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
    }
}
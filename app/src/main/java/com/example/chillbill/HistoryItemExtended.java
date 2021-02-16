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

import com.example.chillbill.helpers.Utils;
import com.example.chillbill.model.Bill;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class HistoryItemExtended extends Fragment {


    private static final float BAR_WIDTH_DP = 338.f;
    private static final float BAR_HEIGHT_DP = 5.f;
    private static final String SERIALIZABLE = "billHistItem";
    private static final String NO_JAR = "##noJar";

    private String name;
    private float price;
    private Date date;
    private String jar;
    private ArrayList<Double> categoryWidth;

    ArrayList<ProgressBar> progressBars;

    public static HistoryItemExtended newInstance(Serializable... bill) {
        HistoryItemExtended fragment = new HistoryItemExtended();
        Bundle args = new Bundle();
        args.putSerializable(SERIALIZABLE, bill[0]);
        fragment.setArguments(args);
        return fragment;
    }

    public HistoryItemExtended() {
        // Required empty public constructor
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
            date = bill.getDate();
            if (date == null) {
                date = new Date();
            }
            jar = bill.getSavingsJar();
        }
        trimLength();
        progressBars = new ArrayList<>();
    }

    private void trimLength() {
        for (int i = 0; i < categoryWidth.size(); i++) {
            if (categoryWidth.get(i) == 0) {
                categoryWidth.set(i, 1.0d);
            }
        }
    }

    // 268dp is total width of all progress bars
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View RootView = inflater.inflate(R.layout.fragment_history_item__extended, container, false);
        // Initialize views
        progressBars.add(RootView.findViewById(R.id.progressBar115));
        progressBars.add(RootView.findViewById(R.id.progressBar114));
        progressBars.add(RootView.findViewById(R.id.progressBar113));
        progressBars.add(RootView.findViewById(R.id.progressBar112));
        progressBars.add(RootView.findViewById(R.id.progressBar111));

        TextView priceTextView = RootView.findViewById(R.id.price);
        TextView titleTextView = RootView.findViewById(R.id.shop_name);


        // Set values to views
        ConstraintLayout constraintLayout = RootView.findViewById(R.id.history_item_extended_container);
        if (progressBars.size() > 0 && progressBars.size() == categoryWidth.size()) {
            attachConstraints(progressBars.get(0), constraintLayout, categoryWidth.get(0).floatValue());
            for (int i = 1; i < progressBars.size(); i++) {
                attachConstraints(progressBars.get(i), progressBars.get(i - 1), constraintLayout, categoryWidth.get(i).floatValue());
            }
        }

        priceTextView.setText(String.format("%.2f", price));
        titleTextView.setText(name);

        TextView categoryTextView = RootView.findViewById(R.id.category_history_item);
        TextView dateTextView = RootView.findViewById(R.id.date_history_item);

        if (jar == null) {
            jar = NO_JAR;
        }
        if (jar.equals(NO_JAR)) {
            jar = getString(R.string.Expenses);
        }
        categoryTextView.setText(jar);

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        if (date == null) {
            date = new Date();
        }
        dateTextView.setText(formatter.format(date));

        return RootView;
    }

    private ConstraintSet prepareConstraintSet(ProgressBar progressBar, ConstraintLayout constraintLayout, float widthPx) {
        progressBar.setLayoutParams(new ConstraintLayout.LayoutParams((int) widthPx, Utils.dpToPx(BAR_HEIGHT_DP, getContext())));
        ConstraintSet constrainSet = new ConstraintSet();
        constrainSet.clone(constraintLayout);
        constrainSet.connect(progressBar.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        return constrainSet;
    }

    private void attachConstraints(ProgressBar progressBar, ConstraintLayout constraintLayout, float widthPx) {
        ConstraintSet constraintSet = prepareConstraintSet(progressBar, constraintLayout, widthPx);
        constraintSet.connect(progressBar.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        constraintSet.applyTo(constraintLayout);
    }

    private void attachConstraints(ProgressBar progressBar, ProgressBar parentProgressBar, ConstraintLayout constraintLayout, float widthPx) {
        ConstraintSet constraintSet = prepareConstraintSet(progressBar, constraintLayout, widthPx);
        constraintSet.connect(progressBar.getId(), ConstraintSet.START, parentProgressBar.getId(), ConstraintSet.END);
        constraintSet.applyTo(constraintLayout);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
package com.example.chillbill;

import android.annotation.SuppressLint;
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

import com.example.chillbill.helpers.HistoryItemHelper;
import com.example.chillbill.helpers.Utils;
import com.example.chillbill.model.Bill;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.example.chillbill.helpers.HistoryItemHelper.SERIALIZABLE;


public class HistoryItemExtended extends Fragment {

    HistoryItemHelper historyItemHelper;
    private static final String NO_JAR = "##noJar";

    private Date date;
    private String jar;


    public static HistoryItemExtended newInstance(Serializable... bill) {
        HistoryItemExtended fragment = new HistoryItemExtended();
        Bundle args = new Bundle();
        args.putSerializable(HistoryItemHelper.SERIALIZABLE, bill[0]);
        fragment.setArguments(args);
        return fragment;
    }

    public HistoryItemExtended() {
        // Required empty public constructor
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
            date = bill.getDate();
            if (date == null) {
                date = new Date();
            }
            jar = bill.getSavingsJar();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_history_item__extended, container, false);
        // Initialize views
        historyItemHelper.setProgressBars(initializeProgressBars(rootView));

        TextView priceTextView = rootView.findViewById(R.id.price);
        TextView titleTextView = rootView.findViewById(R.id.shop_name);
        ConstraintLayout constraintLayout = rootView.findViewById(R.id.history_item_extended_container);

        // Set values to views
        historyItemHelper.modelProgressBars(rootView,constraintLayout);
        priceTextView.setText(String.format(Locale.getDefault(),"%.2f", historyItemHelper.getPrice()));
        titleTextView.setText(historyItemHelper.getName());

        TextView categoryTextView = rootView.findViewById(R.id.category_history_item);
        TextView dateTextView = rootView.findViewById(R.id.date_history_item);

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

        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
    }
}
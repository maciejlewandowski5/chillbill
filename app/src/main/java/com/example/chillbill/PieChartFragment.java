package com.example.chillbill;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.chillbill.helpers.FirestoreHelper;
import com.example.chillbill.helpers.Utils;
import com.example.chillbill.model.Bill;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;


public class PieChartFragment extends Fragment {

    private PieChart pieChart;

    private int[] colors;
    private float[] dataSet;

    FirestoreHelper firestoreHelper;

    public PieChartFragment() {
        // Required empty public constructor
    }


    public static PieChartFragment newInstance() {
        return new PieChartFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PieChartFragment that = this;
        firestoreHelper = new FirestoreHelper(document -> {
            Bill bill = document.toObject(Bill.class);
            for (int i = 0; i < (bill != null ? bill.getCategoryPercentage().size() : 0); i++) {
                dataSet[i] += (float) (bill.getCategoryPercentage().get(i) / 100) * bill.getTotalAmount();
            }
        }, this::setupPieChart, e -> Log.w("History", "Error getting documents.", e), () -> {
            dataSet = new float[5];
            Utils.toastError(that.getContext());
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pie_chart, container, false);

        // Initialise
        pieChart = view.findViewById(R.id.pie_chart);
        colors = Utils.getPrimaryColors(this);
        LocalDate endLoc = LocalDate.now();
        Date start = Utils.getFirstDayOfTheMonth(Utils.localDateToDate(endLoc.minusMonths(1)));

        // Set
        firestoreHelper.loadBills(start, Utils.localDateToDate(endLoc));
        return view;
    }


    private void setupPieChart() {

        //populate list of entries
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        for (float v : dataSet) {
            pieEntries.add(new PieEntry(v));
        }

        //configure chart
        PieDataSet dataSet = new PieDataSet(pieEntries, "");
        dataSet.setColors(colors);
        PieData pieData = new PieData(dataSet);
        pieData.setDrawValues(false);
        pieChart.setData(pieData);
        pieChart.setDrawEntryLabels(false);
        pieChart.setContentDescription("");
        pieChart.setHoleRadius(Utils.dpToPx(39, requireActivity().getApplicationContext()));
        pieChart.getDescription().setEnabled(false);
        pieChart.setTouchEnabled(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.refreshDrawableState();
        pieChart.invalidate();
        pieChart.spin(1400, 0, 90, Easing.EaseInOutQuad);
        pieChart.animateY(700, Easing.EaseInOutQuad);
    }

}
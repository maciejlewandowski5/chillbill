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
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;


public class BarChartFragment extends Fragment {

    private static final String TAG = "ChillBill_BarCharFragment";

    BarChart barChart;

    private int[] mainColors;
    private int[] secondaryColors;

    float[] dataSetMain;
    float[] dataSetSecondary;

    FirestoreHelper firestoreHelperMain;
    FirestoreHelper firestoreHelperSecondary;

    Date start;
    Date end;

    public BarChartFragment() {
        // Required empty public constructor
    }

    public static BarChartFragment newInstance() {
        return new BarChartFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BarChartFragment that = this;

        // For first data set
        firestoreHelperMain = new FirestoreHelper(bill -> {
            for (int i = 0; i < Objects.requireNonNull(bill).getCategoryPercentage().size(); i++) {
                dataSetMain[i] += (float) (bill.getCategoryPercentage().get(i) / 100) * bill.getTotalAmount();
            }
        }, () -> firestoreHelperSecondary.loadBills(start, end), e -> {
            Log.w(TAG, "Error getting documents.", e);
            Utils.toastError(that.getContext());
        }, () -> dataSetMain = new float[5]);

        // For second data set
        firestoreHelperSecondary = new FirestoreHelper(bill -> {
            for (int i = 0; i < Objects.requireNonNull(bill).getCategoryPercentage().size(); i++) {
                dataSetSecondary[i] += (float) (bill.getCategoryPercentage().get(i) / 100) * bill.getTotalAmount();
            }
        }, () -> {
            if (dataSetSecondary != null && dataSetMain != null) {
                setupBarChart();
            }
        }, e -> {
            Log.w(TAG, "Error getting documents.", e);
            Utils.toastError(that.getContext());
        }, () -> dataSetSecondary = new float[5]);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bar_chart, container, false);

        // Initialize
        barChart = view.findViewById(R.id.barChart);
        mainColors = Utils.getPrimaryColors(this);
        secondaryColors = Utils.getSecondaryColors(this);

        // Set
        end = new Date();

        Calendar cal = Calendar.getInstance();
        cal.setTime(Utils.getFirstDayOfTheMonth(end));
        cal.add(Calendar.DATE, -1);
        start = Utils.getFirstDayOfTheMonth(cal.getTime());

        firestoreHelperMain.loadBills(Utils.getFirstDayOfTheMonth(end), end);
        firestoreHelperSecondary.loadBills(start, end);

        return view;
    }

    private void setupBarChart() {
        ArrayList<BarEntry> currentEntries = new ArrayList<>();
        ArrayList<BarEntry> oldEntries = new ArrayList<>();
        for (int i = 0; i < dataSetMain.length; i++) {
            currentEntries.add(new BarEntry(i, dataSetMain[i]));
            oldEntries.add(new BarEntry(i, dataSetSecondary[i]));
        }

        BarDataSet set1 = new BarDataSet(currentEntries, "");
        set1.setColors(mainColors);
        BarDataSet set2 = new BarDataSet(oldEntries, "");
        set2.setColors(secondaryColors);

        BarData data = new BarData(set2, set1);
        data.setBarWidth(0.35f);
        barChart.setData(data);
        barChart.groupBars(0, 0.15f, 0f);
        barChart.getXAxis().setEnabled(false);
        barChart.getAxisLeft().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.setTouchEnabled(false);
        barChart.refreshDrawableState();
        barChart.invalidate();
        barChart.animateY(700, Easing.EaseInOutQuad);
        barChart.animateX(700, Easing.EaseInOutQuad);

    }
}
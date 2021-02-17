package com.example.chillbill;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chillbill.helpers.FirestoreHelper;
import com.example.chillbill.helpers.Utils;
import com.example.chillbill.model.Bill;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class BarChartFragment extends Fragment {

    BarChart barChart;

    private int[] mainColors;
    private int[] secondaryColors;

    float[] dataSetMain;
    float[] dataSetSecondary;

    FirestoreHelper firestoreHelper;

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
        firestoreHelper = new FirestoreHelper(new FirestoreHelper.OnGetDocument() {
            @Override
            public void onGetDocument(DocumentSnapshot document) {
                Bill bill = document.toObject(Bill.class);
                for (int i = 0; i < bill.getCategoryPercentage().size(); i++) {
                    dataSetMain[i] += (float) (bill.getCategoryPercentage().get(i)/100) * bill.getTotalAmount();
                }
            }
        }, new FirestoreHelper.OnDocumentsProcessingFinished() {
            @Override
            public void onDocumentsProcessingFinished() {
                if(dataSetSecondary != null && dataSetMain != null) {
                    setupBarChart();
                }
            }
        }, new FirestoreHelper.OnError() {
            @Override
            public void onError(Exception e) {
                Log.w("History", "Error getting documents.", e);
                Utils.toastError(that.getContext());
            }
        }, new FirestoreHelper.OnTaskSuccessful() {
            @Override
            public void OnTaskSuccessful() {
                dataSetMain = new float[5];
            }
        });
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
        Date end = new Date();
        Date start = Utils.getFirstDayOfTheMonth(end);
        firestoreHelper.loadBills(start,end);

        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        cal.add(Calendar.DATE, -1);
        Date startSecondary = Utils.getFirstDayOfTheMonth(cal.getTime());
        firestoreHelper.loadBills(startSecondary,end);

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
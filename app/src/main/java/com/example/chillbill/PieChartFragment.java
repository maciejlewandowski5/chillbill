package com.example.chillbill;

import android.content.Context;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chillbill.helpers.FirestoreHelper;
import com.example.chillbill.helpers.Utils;
import com.example.chillbill.model.Bill;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;


public class PieChartFragment extends Fragment {

    private PieChart pieChart;

    int[] colors;
    float[] dataSet;

    public PieChartFragment() {
        // Required empty public constructor
    }


    public static PieChartFragment newInstance() {
        PieChartFragment fragment = new PieChartFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pie_chart, container, false);
        pieChart = view.findViewById(R.id.pie_chart);
        colors = new int[]{
                ContextCompat.getColor(getActivity(), R.color.purple),
                ContextCompat.getColor(getActivity(), R.color.yellow),
                ContextCompat.getColor(getActivity(), R.color.green),
                ContextCompat.getColor(getActivity(), R.color.orange),
                ContextCompat.getColor(getActivity(), R.color.blue)
        };

        LocalDate endloc = LocalDate.now();
        Date start = Utils.getFirstDayOfTheMonth(convertToDateViaInstant(endloc.minusMonths(1)));
        FirestoreHelper.getBillsInRange(start, convertToDateViaInstant(endloc)).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                dataSet = new float[5];
                QuerySnapshot qs = task.getResult();
                for (QueryDocumentSnapshot ds : qs) {
                    Bill bill = ds.toObject(Bill.class);
                    for (int i = 0; i < bill.getCategoryPercentage().size(); i++) {
                        dataSet[i] += (float) (bill.getCategoryPercentage().get(i)/100) * bill.getTotalAmount();
                    }
                }
                setupPieChart();
            }
        });

        return view;
    }

    public Date convertToDateViaInstant(LocalDate dateToConvert) {
        return java.util.Date.from(dateToConvert.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }
    private void setupPieChart() {
        //Sample data
        //populate list of entries
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        for (int i = 0; i < dataSet.length; i++) {
            pieEntries.add(new PieEntry(dataSet[i]));
        }
        //configure chart
        PieDataSet dataSet = new PieDataSet(pieEntries, "");
        dataSet.setColors(colors);
        PieData pieData = new PieData(dataSet);
        pieData.setDrawValues(false);
        pieChart.setData(pieData);
        pieChart.setDrawEntryLabels(false);
        pieChart.setContentDescription("");
        pieChart.setHoleRadius(dpToPx(39, requireActivity().getApplicationContext()));
        pieChart.getDescription().setEnabled(false);
        pieChart.setTouchEnabled(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.refreshDrawableState();
        pieChart.invalidate();
        pieChart.spin(1400,0,90,Easing.EaseInOutQuad);
        pieChart.animateY(700, Easing.EaseInOutQuad);

    }
    public static int dpToPx(float dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}
package com.example.chillbill;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chillbill.model.Bill;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BarChartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BarChartFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private int[] mainColors;
    private int[] secondaryColors;
    BarChart barChart;
    float[] datasetMain;
    float[] datasetSecondary;

    public BarChartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BarChartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BarChartFragment newInstance(String param1, String param2) {
        BarChartFragment fragment = new BarChartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bar_chart, container, false);

        barChart = view.findViewById(R.id.barChart);
        mainColors = new int[]{
                ContextCompat.getColor(getActivity(), R.color.purple),
                ContextCompat.getColor(getActivity(), R.color.yellow),
                ContextCompat.getColor(getActivity(), R.color.green),
                ContextCompat.getColor(getActivity(), R.color.orange),
                ContextCompat.getColor(getActivity(), R.color.blue)
        };
        secondaryColors = new int[]{
                ContextCompat.getColor(getActivity(), R.color.dark_purple),
                ContextCompat.getColor(getActivity(), R.color.dark_yellow),
                ContextCompat.getColor(getActivity(), R.color.dark_green),
                ContextCompat.getColor(getActivity(), R.color.dark_orange),
                ContextCompat.getColor(getActivity(), R.color.dark_blue)
        };

        Date end = new Date();
        Date start = Utils.getFirstDayOfTheMonth(end);
        Utils.getBillsInRange(start, end).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                datasetMain = new float[5];
                QuerySnapshot qs = task.getResult();
                for (QueryDocumentSnapshot ds : qs) {
                    Bill bill = ds.toObject(Bill.class);
                    for (int i = 0; i < bill.getCategoryPercentage().size(); i++) {
                        datasetMain[i] += (float) (bill.getCategoryPercentage().get(i)/100) * bill.getTotalAmount();
                    }
                }
                if(datasetSecondary != null) {
                    setupBarChart();
                }
            }
        });

        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        cal.add(Calendar.DATE, -1);
        Date startSecondary = Utils.getFirstDayOfTheMonth(cal.getTime());
        Utils.getBillsInRange(startSecondary, start).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                datasetSecondary = new float[5];
                QuerySnapshot qs = task.getResult();
                for (QueryDocumentSnapshot ds : qs) {
                    Bill bill = ds.toObject(Bill.class);
                    for (int i = 0; i < bill.getCategoryPercentage().size(); i++) {
                        datasetSecondary[i] += (float) (bill.getCategoryPercentage().get(i)/100) * bill.getTotalAmount();
                    }
                }
                if(datasetMain != null) {
                    setupBarChart();
                }
            }
        });



        return view;
    }

    private void setupBarChart() {
        ArrayList<BarEntry> currentEntries = new ArrayList<>();
        ArrayList<BarEntry> oldEntries = new ArrayList<>();
        for (int i = 0; i < datasetMain.length; i++) {
            currentEntries.add(new BarEntry(i, datasetMain[i]));
            oldEntries.add(new BarEntry(i, datasetSecondary[i]));
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
    }
}
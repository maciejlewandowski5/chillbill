package com.example.chillbill;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.chillbill.model.Bill;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PieChartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PieChartFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private PieChart pieChart;

    int[] colors;
    float[] dataset;


    public PieChartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PieChartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PieChartFragment newInstance(String param1, String param2) {
        PieChartFragment fragment = new PieChartFragment();
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
        View view = inflater.inflate(R.layout.fragment_pie_chart, container, false);
        pieChart = view.findViewById(R.id.pie_chart);
        colors = new int[]{
                ContextCompat.getColor(getActivity(), R.color.purple),
                ContextCompat.getColor(getActivity(), R.color.yellow),
                ContextCompat.getColor(getActivity(), R.color.green),
                ContextCompat.getColor(getActivity(), R.color.orange),
                ContextCompat.getColor(getActivity(), R.color.blue)
        };

        Date end = new Date();
        Date start = Utils.getFirstDayOfTheMonth(end);
        Utils.getBillsInRange(start, end).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                dataset = new float[5];
                QuerySnapshot qs = task.getResult();
                for (QueryDocumentSnapshot ds : qs) {
                    Bill bill = ds.toObject(Bill.class);
                    for (int i = 0; i < bill.getCategoryPercentage().size(); i++) {
                        dataset[i] += (float) (bill.getCategoryPercentage().get(i)/100) * bill.getTotalAmount();
                    }
                }
                setupPieChart();
            }
        });

        return view;
    }


    private void setupPieChart() {
        //Sample data
        //populate list of entries
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        for (int i = 0; i < dataset.length; i++) {
            pieEntries.add(new PieEntry(dataset[i]));
        }
        //configure chart
        PieDataSet dataSet = new PieDataSet(pieEntries, "");
        dataSet.setColors(colors);
        PieData pieData = new PieData(dataSet);
        pieData.setDrawValues(true);
        pieChart.setData(pieData);
        pieChart.setDrawEntryLabels(false);
        pieChart.setContentDescription("");
        pieChart.setHoleRadius(75);
        pieChart.getDescription().setEnabled(false);
        pieChart.setTouchEnabled(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.refreshDrawableState();
        pieChart.invalidate();

    }
}
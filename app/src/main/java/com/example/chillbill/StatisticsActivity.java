package com.example.chillbill;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;

public class StatisticsActivity extends AppCompatActivity {

    class DemoData {
        float[] expenses;
        float[] oldExpenses;
        String[] labels;
        int[] colors;
        int[] oldColors;

        public DemoData() {
            expenses = new float[]{55.21f, 231.8f, 121.11f, 23f, 77.77f};
            oldExpenses = new float[]{108f, 23.99f, 15f, 23f, 66.6f};
            labels = new String[] {getString(R.string.yellow_category),
                    getString(R.string.blue_category),
                    getString(R.string.green_category),
                    getString(R.string.orange_category),
                    getString(R.string.purple_category)};
            colors = new int[] {getColor(R.color.yellow),
                    getColor(R.color.blue),
                    getColor(R.color.green),
                    getColor(R.color.orange),
                    getColor(R.color.purple)};
            oldColors = new int[] {
                    getColor(R.color.light_yellow),
                    getColor(R.color.light_blue),
                    getColor(R.color.light_green),
                    getColor(R.color.light_orange),
                    getColor(R.color.light_purple)
            };
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        PieChart pieChart = (PieChart) findViewById(R.id.pie_chart);
        setupPieChart(pieChart);

        BarChart barChart = (BarChart)findViewById(R.id.month_by_month_bar_chart);
        setupBarChart(barChart);

    }

    private void setupPieChart(PieChart pieChart) {
        //Sample data
        DemoData demoData = new DemoData();
        //populate list of entries
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        for (int i = 0; i < demoData.expenses.length; i++) {
            pieEntries.add(new PieEntry(demoData.expenses[i], demoData.labels[i]));
        }
        //configure chart
        PieDataSet dataSet = new PieDataSet(pieEntries, "");
        dataSet.setColors(demoData.colors);
        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        pieChart.setData(data);
        pieChart.setDrawEntryLabels(false);
        pieChart.setContentDescription("");
        pieChart.setHoleRadius(75);
        pieChart.getDescription().setEnabled(false);
        pieChart.setTouchEnabled(false);
        //configure legend
        //TODO Create legend outside chart instead
        Legend legend = pieChart.getLegend();
        legend.setForm((Legend.LegendForm.CIRCLE));
        legend.setTextSize(12);
        legend.setFormSize(20);
        legend.setFormToTextSpace(10);
        legend.setYEntrySpace(5);
        legend.setWordWrapEnabled(true);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
    }

    private void setupBarChart(BarChart barChart) {
        DemoData demoData = new DemoData();
        ArrayList<BarEntry> currentEntries = new ArrayList<>();
        ArrayList<BarEntry> oldEntries = new ArrayList<>();
        for(int i = 0; i < demoData.expenses.length; i++) {
            currentEntries.add(new BarEntry(i, demoData.expenses[i]));
            oldEntries.add(new BarEntry(i, demoData.oldExpenses[i]));
        }

        BarDataSet set1 = new BarDataSet(currentEntries, "");
        set1.setColors(demoData.colors);
        BarDataSet set2 = new BarDataSet(oldEntries, "");
        set2.setColors(demoData.oldColors);

        BarData data = new BarData(set1, set2);
        data.setBarWidth(0.35f);
        barChart.setData(data);
        barChart.groupBars(0, 0.15f, 0f);
        barChart.getXAxis().setEnabled(false);
        barChart.getAxisLeft().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.setTouchEnabled(false);
    }
}
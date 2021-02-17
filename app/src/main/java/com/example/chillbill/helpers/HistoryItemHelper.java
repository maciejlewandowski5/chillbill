package com.example.chillbill.helpers;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.example.chillbill.R;

import java.util.ArrayList;

public class HistoryItemHelper {

    public static final float BAR_WIDTH_DP = 338.f;
    public static final float BAR_HEIGHT_DP = 5.f;
    public static final String SERIALIZABLE = "billHistItem";

    public String name;
    public float price;
    public ArrayList<Double> categoryWidth;

    ArrayList<ProgressBar> progressBars;



    public HistoryItemHelper() {
    }



    public String getName() {
        return name;
    }

    public float getPrice() {
        return price;
    }

    public ArrayList<Double> getCategoryWidth() {
        return categoryWidth;
    }

    public ArrayList<ProgressBar> getProgressBars() {
        return progressBars;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setCategoryWidth(ArrayList<Double> categoryWidth) {
        this.categoryWidth = categoryWidth;
    }

    public void setProgressBars(ArrayList<ProgressBar> progressBars) {
        this.progressBars = progressBars;
    }

    public HistoryItemHelper(String name, float price, ArrayList<Double> categoryWidth, ArrayList<ProgressBar> progressBars) {
        this.name = name;
        this.price = price;
        this.categoryWidth = categoryWidth;
        this.progressBars = progressBars;
    }

    public void trimLength() {
        for (int i = 0; i < categoryWidth.size(); i++) {
            if (categoryWidth.get(i) == 0) {
                categoryWidth.set(i, 1.0d);
            }
        }
    }

    public void initializeProgressBars(View rootView){
        // Initialize views
        progressBars.add(rootView.findViewById(R.id.progressBar5));
        progressBars.add(rootView.findViewById(R.id.progressBar4));
        progressBars.add(rootView.findViewById(R.id.progressBar2));
        progressBars.add(rootView.findViewById(R.id.progressBar3));
        progressBars.add(rootView.findViewById(R.id.progressBar7));

    }

    public ConstraintSet prepareConstraintSet(ProgressBar progressBar, ConstraintLayout constraintLayout, float widthPx, Context context) {
        progressBar.setLayoutParams(new ConstraintLayout.LayoutParams((int) widthPx, Utils.dpToPx(BAR_HEIGHT_DP, context)));
        ConstraintSet constrainSet = new ConstraintSet();
        constrainSet.clone(constraintLayout);
        constrainSet.connect(progressBar.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        return constrainSet;
    }

    public void attachConstraints(ProgressBar progressBar, ConstraintLayout constraintLayout, float widthPx,Context context) {
        ConstraintSet constraintSet = prepareConstraintSet(progressBar, constraintLayout, widthPx,context);
        constraintSet.connect(progressBar.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        constraintSet.applyTo(constraintLayout);
    }

    public void attachConstraints(ProgressBar progressBar, ProgressBar parentProgressBar, ConstraintLayout constraintLayout, float widthPx,Context context) {
        ConstraintSet constraintSet = prepareConstraintSet(progressBar, constraintLayout, widthPx,context);
        constraintSet.connect(progressBar.getId(), ConstraintSet.START, parentProgressBar.getId(), ConstraintSet.END);
        constraintSet.applyTo(constraintLayout);
    }

    public void setProgressBars(View root){
        ConstraintLayout constraintLayout = root.findViewById(R.id.constrainViewHistoryFragment);

        attachConstraints(progressBars.get(0), constraintLayout, categoryWidth.get(0).floatValue(),root.getContext());
        for (int i = 1; i < progressBars.size(); i++) {
            attachConstraints(progressBars.get(i), progressBars.get(i - 1), constraintLayout, categoryWidth.get(i).floatValue(),root.getContext());
        }
    }

}

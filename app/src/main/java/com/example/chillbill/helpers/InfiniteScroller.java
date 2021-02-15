package com.example.chillbill.helpers;

import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.chillbill.R;

import java.io.Serializable;
import java.util.ArrayList;

public class InfiniteScroller<T extends Serializable> {

    LinearLayout container;
    //Height of one element in dp;
    int height;
    SpecificOnClickListener onClickListener;
    AbstractFactory factory;
    AppCompatActivity app;

    public InfiniteScroller(LinearLayout container, int height, SpecificOnClickListener onClickListener, AbstractFactory factory, AppCompatActivity app) {
        this.container = container;
        this.height = height;
        this.onClickListener = onClickListener;
        this.factory = factory;
        this.app = app;
    }

    public void clear(){
        container.removeAllViews();
    }

    public  <T extends Serializable> void populate(ArrayList<T> items){


        int i = 0;
        for (T item : items) {
            FragmentTransaction transaction = app.getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right,android.R.anim.fade_in,android.R.anim.fade_out);
            ConstraintLayout constraintLayout = new ConstraintLayout(app);
            Fragment fragment = factory.newInstance(item,i);

            constraintLayout.setId(View.generateViewId());
            container.addView(constraintLayout);
            Button button = new Button(app);
            button.setId(View.generateViewId());
            button.setBackgroundColor(Color.TRANSPARENT);
            button.setLayoutParams(constraintLayout.getLayoutParams());


            button.setHeight(Utils.dpToPx(height, app));

            int finalI = i;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onClick(v,item, finalI);
                }
            });

            constraintLayout.addView(button);

            transaction.add(constraintLayout.getId(), fragment, String.valueOf(item.hashCode()));
            i++;
            transaction.commit();
        }

    }



    public interface SpecificOnClickListener {

        void onClick(View view, Serializable object,int index);

    }


}

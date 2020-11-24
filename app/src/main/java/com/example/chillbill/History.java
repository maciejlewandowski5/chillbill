package com.example.chillbill;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.util.Date;

public class History extends AppCompatActivity {
    LinearLayout linearLayout;
    ScrollView scrollView;
    int prevScrollPositon;
    int loadedElements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        linearLayout = findViewById(R.id.linear_list_for_history_extended_item_object);
        scrollView = findViewById(R.id.history_scroll_view);
        // scrollView.getChildAt(0).getHeight();
        prevScrollPositon = 0;
        loadedElements = 0;

        HistoryItemExtended tmp = HistoryItemExtended.newInstance("Biedronka",
                252.15f, 15, 5, 15,
                5, 60, new Date(), "Podstawowe wydatki");

        androidx.fragment.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();


        for (int i = 0; i < 10; i++) {
            HistoryItemExtended fragment = HistoryItemExtended.newInstance("Biedronka" + i,
                    252.15f, 15 + i, 5, 15,
                    5, 60 - i, new Date(), "Podstawowe wydatki");

            transaction.add(linearLayout.getId(), fragment);
            loadedElements++;

        }
        transaction.commit();

        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                androidx.fragment.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                int scrollY = scrollView.getScrollY();
                int fragmentScrolled = (int) Math.ceil((float) scrollY / (linearLayout.getChildAt(0).getHeight() + DptoPx(16)));
                float percentScrolled = (float) scrollY / (linearLayout.getHeight());


                if (prevScrollPositon < scrollY) {

                    if ((float)fragmentScrolled/loadedElements >= 0.5) {
                        for (int i = 0; i < 5; i++) {
                            HistoryItemExtended fragment = HistoryItemExtended.newInstance("Biedronka" + i,
                                    252.15f, 15 + i, 5, 15,
                                    5, 60 - i, new Date(), "Podstawowe wydatki");

                            transaction.add(linearLayout.getId(), fragment);
                            loadedElements++;

                        }
                    }
                    transaction.commit();
                }
                prevScrollPositon = scrollY;
            }
        });

    }

    public void addNextHistoryItem(View view) {

        androidx.fragment.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //for (int i = 0; i < 10; i++) {
        HistoryItemExtended fragment = HistoryItemExtended.newInstance("Lidl",
                252.15f, 15, 5, 15,
                5, 60, new Date(), "Podstawowe wydatki");

        transaction.add(linearLayout.getId(), fragment);

        //}
        transaction.commit();

    }

    int DptoPx(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

}

package com.example.chillbill;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.os.Bundle;
import android.widget.LinearLayout;

import java.util.Date;

public class History extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        LinearLayout constraintLayout = findViewById(R.id.linear_list_for_history_extended_item_object);

        HistoryItemExtended tmp = HistoryItemExtended.newInstance("Biedronka",
                252.15f, 15, 5, 15,
                5, 60, new Date(), "Podstawowe wydatki");

        androidx.fragment.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();


        for (int i = 0; i < 10; i++) {
            HistoryItemExtended fragment = HistoryItemExtended.newInstance("Biedronka",
                    252.15f, 15+i, 5, 15,
                    5, 60-i, new Date(), "Podstawowe wydatki");

            transaction.add(constraintLayout.getId(), fragment);

             }
            transaction.commit();

        }
    }
package com.example.chillbill;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.widget.LinearLayout;

import java.util.Date;

public class BillPage extends AppCompatActivity {

    private LinearLayout  linearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_page);

        linearLayout = findViewById(R.id.container_for_table_item_bill_list);
        LinearLayout linlay = findViewById(R.id.linearLayout7);

        linlay.removeAllViews();
        androidx.fragment.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment billfrag = new HistoryItemExtended().newInstance("Żabka",876.12f,5,15,20,20,40,new Date(),"Podstawowe wydatki");
        transaction.add(linlay.getId(),billfrag);
        transaction.commit();

         transaction = getSupportFragmentManager().beginTransaction();

        for(int i=0;i<25;i++) {
            Fragment fragment = new ListElment().newInstance("Brokuły"+i, 7.58f+i, i % 2 != 0, "orange");
            transaction.add(linearLayout.getId(), fragment);
        }
        transaction.commit();
    }
}
package com.example.chillbill;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.chillbill.helpers.FirestoreHelper;
import com.example.chillbill.helpers.InfiniteScroller;
import com.example.chillbill.model.Bill;
import com.example.chillbill.model.Category;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


public class History extends AppCompatActivity implements FilterButtons.OnClickListener {


    private final String ARG_HIST_PARAM_OUT = "HISTINFO";

    TextView textView;
    LinearLayout linearLayout;
    ScrollView scrollView;

    InfiniteScroller<Bill> infiniteScroller;
    ArrayList<Bill>[] billsInfo;
    FilterButtons filterButtons;

    FirestoreHelper firestoreHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Fireabase
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        firestoreHelper = new FirestoreHelper(firebaseAuth, db, document -> {
            Bill bill = document.toObject(Bill.class);
            billsInfo[0].add(bill);
        }, () -> infiniteScroller.populate(billsInfo[0]),
                e -> Log.w("History", "Error getting documents.", e)
                , () -> billsInfo[0] = new ArrayList<>());



        // Initialize views
        linearLayout = findViewById(R.id.linear_list_for_history_extended_item_object);
        scrollView = findViewById(R.id.history_scroll_view);
        textView = findViewById(R.id.textInputEditText);

        billsInfo = new ArrayList[]{new ArrayList<Bill>()};

        History that = this;
        filterButtons = FilterButtons.newInstance();
        filterButtons.setSorter(new FilterButtons.Sorter() {
            @Override
            public void sortByDate() {

                billsInfo[0].sort((o1, o2) -> o2.getDate().compareTo(o1.getDate()));
                linearLayout.removeAllViews();
                infiniteScroller.populate(billsInfo[0]);
            }

            @Override
            public void sortAndDisplay(Category category) {
                billsInfo[0].sort((o1, o2) -> {
                    int toCmp;
                    if (category == Category.PURPLE) {
                        toCmp = 0;
                    } else if (category == Category.YELLOW) {
                        toCmp = 1;
                    } else if (category == Category.GREEN) {
                        toCmp = 2;
                    } else if (category == Category.ORANGE) {
                        toCmp = 3;
                    } else if (category == Category.BLUE) {
                        toCmp = 4;
                    } else {
                        return -1; // for category = null
                    }
                    double rhs = o2.getCategoryPercentage().get(toCmp);
                    double lhs = o1.getCategoryPercentage().get(toCmp);
                    return Double.compare(rhs, lhs);
                });
                linearLayout.removeAllViews();
                infiniteScroller.populate(billsInfo[0]);
            }
        });

        infiniteScroller = new InfiniteScroller<>(linearLayout, 72 + 16, (view, object, index) -> {
            view.setTransitionName("he");
            Intent intent = new Intent(that, BillPage.class);
            intent.putExtra(ARG_HIST_PARAM_OUT, object);
            startActivity(intent);
        }, HistoryItemExtended::newInstance, this);


        // Initialize fragments
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setReorderingAllowed(true);
        transaction.replace(R.id.fragment, filterButtons);

        transaction.commit();

    }


    @Override
    protected void onResume() {
        super.onResume();
        linearLayout.removeAllViews();
        firestoreHelper.loadHistoryItems();
        filterButtons.callOnResume();
    }

    public void search(View view) {
        ArrayList<Bill> sorted = new ArrayList<>();
        for (Bill bill : billsInfo[0]) {
            if (bill.getShopName().contains(textView.getText())) {
                sorted.add(bill);
            }
        }
        linearLayout.removeAllViews();
        infiniteScroller.populate(sorted);
    }


    @Override
    public void sortYellow(View view) {
        filterButtons.sortColorHistory(view, R.color.yellow, Category.YELLOW, 2);
    }

    @Override
    public void sortPurple(View view) {
        filterButtons.sortColorHistory(view, R.color.purple, Category.PURPLE, 1);
    }

    @Override
    public void sortGreen(View view) {
        filterButtons.sortColorHistory(view, R.color.green, Category.GREEN, 3);
    }

    @Override
    public void sortOrange(View view) {
        filterButtons.sortColorHistory(view, R.color.orange, Category.ORANGE, 0);
    }

    @Override
    public void sortBlue(View view) {
        filterButtons.sortColorHistory(view, R.color.blue, Category.BLUE, 4);
    }
}

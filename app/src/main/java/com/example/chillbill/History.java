package com.example.chillbill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.chillbill.helpers.FirestoreHelper;
import com.example.chillbill.helpers.InfiniteScroller;
import com.example.chillbill.model.Bill;

import com.example.chillbill.model.Category;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;


//TODO: Implemet sorting by biggest usage of bill in category and by date and by
//      jar type. Should be done by get requests to cloud.
public class History extends AppCompatActivity implements FilterButtons.OnClickListener {
    LinearLayout linearLayout;
    ScrollView scrollView;
    int prevScrollPositon;
    int loadedElements;
    private final String ARG_HIST_PARAM_OUT = "HISTINFO";
    private FirebaseAuth firebaseAuth;
    FirebaseFirestore db;


    TextView textView;


    int[] colors;
    InfiniteScroller<Bill> infiniteScroller;
    FirestoreHelper firestoreHelper;
     ArrayList<Bill>[] billsInfo;
     FilterButtons filterButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);


        // Initialize views
        linearLayout = findViewById(R.id.linear_list_for_history_extended_item_object);
        scrollView = findViewById(R.id.history_scroll_view);
        textView = findViewById(R.id.textInputEditText);

        // scrollView.getChildAt(0).getHeight();
        prevScrollPositon = 0;
        loadedElements = 0;

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        History that = this;

        filterButtons = FilterButtons.newInstance();
        filterButtons.setSorter(new FilterButtons.Sorter() {
            @Override
            public void sortByDate() {

                billsInfo[0].sort(new Comparator<Bill>() {
                    @Override
                    public int compare(Bill o1, Bill o2) {
                        return o2.getDate().compareTo(o1.getDate());
                    }
                });
                linearLayout.removeAllViews();
                infiniteScroller.populate(billsInfo[0]);
            }

            @Override
            public void sortAndDisplay(Category category) {
                billsInfo[0].sort(new Comparator<Bill>() {
                    @Override
                    public int compare(Bill o1, Bill o2) {
                        int toCmp = 0;
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
                        return lhs > rhs ? -1 : (lhs < rhs) ? 1 : 0;
                    }
                });
                linearLayout.removeAllViews();
                infiniteScroller.populate(billsInfo[0]);
            }
        });
        filterButtons.setOnClickListener(this);


        infiniteScroller = new InfiniteScroller<>(linearLayout, 72 + 16, new InfiniteScroller.SpecificOnClickListener() {
            @Override
            public void onClick(View view, Serializable object, int index) {
                Intent intent = new Intent(that, BillPage.class);
                intent.putExtra(ARG_HIST_PARAM_OUT, object);
                that.startActivity(intent);
            }
        }, HistoryItemExtended::newInstance, this);

        billsInfo = new ArrayList[]{new ArrayList<Bill>()};
        firestoreHelper = new FirestoreHelper(firebaseAuth, db, new FirestoreHelper.OnGetDocument() {
            @Override
            public void onGetDocument(QueryDocumentSnapshot document) {
                Bill bill = document.toObject(Bill.class);
                billsInfo[0].add(bill);
            }
        }, new FirestoreHelper.OnDocumentsProcessingFinished() {
            @Override
            public void onDocumentsProcessingFinished() {
                infiniteScroller.populate(billsInfo[0]);
            }
        }, new FirestoreHelper.OnError() {
            @Override
            public void onError(Exception e) {
                Log.w("History", "Error getting documents.", e);

            }
        }, new FirestoreHelper.OnTaskSuccessful() {
            @Override
            public void OnTaskSuccessful() {

                billsInfo[0] = new ArrayList<>();
            }
        });


        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setReorderingAllowed(true);
        transaction.replace(R.id.fragment,filterButtons);

        transaction.commit();


        //TODO: Fix infinity scroller
        // loadAllHistoryItemExtended();
    }


    @Override
    protected void onResume() {
        super.onResume();
        linearLayout.removeAllViews();
        firestoreHelper.loadHistoryItems();
        filterButtons.refreshFilters();
        filterButtons.setAllToWhite();
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

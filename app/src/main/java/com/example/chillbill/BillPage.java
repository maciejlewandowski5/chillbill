package com.example.chillbill;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.chillbill.helpers.FirestoreHelper;
import com.example.chillbill.helpers.InfiniteScroller;
import com.example.chillbill.helpers.Utils;
import com.example.chillbill.model.Bill;
import com.example.chillbill.model.Category;
import com.example.chillbill.model.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;


public class BillPage extends AppCompatActivity implements FilterButtons.OnClickListener {

    private static final String TAG = "ChillBill_BillPage";
    private static final String ARG_PROD_PARAM_OUT = "PRODINFO";
    private static final String BILL_FOR_PROD = "BILL_FOR_PROD";

    private FirestoreHelper firestoreHelper;
    private LinearLayout linearLayout;
    private InfiniteScroller<Product> infiniteScroller;
    private FilterButtons filterButtons;

    private Bill bill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_page);
        Intent intent = getIntent();


        // Initialize views
        linearLayout = findViewById(R.id.container_for_table_item_bill_list);
        LinearLayout linlay = findViewById(R.id.linearLayout7);


        // Get parameters
        String ARG_HIST_PARAM_OUT = "HISTINFO";
        bill = (Bill) intent.getSerializableExtra(ARG_HIST_PARAM_OUT);


        BillPage that = this;
        infiniteScroller = new InfiniteScroller<>(linearLayout, 24 + 8 + 24, (View v, Serializable s, int i) -> {
            Intent intent1 = new Intent(that, ProductPropertiesEditor.class);
            intent1.putExtra(BILL_FOR_PROD, bill);
            intent1.putExtra(ARG_PROD_PARAM_OUT, i);
            that.startActivity(intent1);
        }, ListElement::newInstance, this);


        // Firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firestoreHelper = new FirestoreHelper(firebaseAuth, db, (DocumentSnapshot document) -> {
            bill = document.toObject(Bill.class);
            infiniteScroller.populate(bill.getProductList());
        }, () -> {
            // Not used, -> not implemented
        }, e -> {
            Log.w(TAG, "Error getting documents.", e.getCause());
            Utils.toastError(that);
        }, () -> {
          // Not used, -> not implemented
        });


        filterButtons = FilterButtons.newInstance();
        filterButtons.setSorter(new FilterButtons.Sorter() {
            @Override
            public void sortByDate() {
                linearLayout.removeAllViews();
                infiniteScroller.populate(bill.getProductList());
            }

            @Override
            public void sortAndDisplay(Category category) {
                linearLayout.removeAllViews();
                populateContainer(category);
            }
        });



        // Initial transactions
        linlay.removeAllViews();
        androidx.fragment.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment billfrag = HistoryItemExtended.newInstance(bill);
        transaction.add(linlay.getId(), billfrag);
        transaction.commit();


        FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();

        transaction2.replace(R.id.fragment2, filterButtons);
        transaction2.commit();

    }


    private void populateContainer(Category category) {
        ArrayList<Product> tmp = new ArrayList<>();
        bill.getProductList().forEach((Product product) -> {
            if (product.getCategory() == category) {
                tmp.add(product);
            }
        });
        infiniteScroller.populate(tmp);
    }


    private void refreshBill() {
        firestoreHelper.loadBill(bill.getId());
    }

    @Override
    protected void onResume() {
        super.onResume();
        String ARG_HIST_PARAM_OUT = "HISTINFO";
        Intent intent = getIntent();
        bill = (Bill) intent.getSerializableExtra(ARG_HIST_PARAM_OUT);
        refreshBill();
        filterButtons.callOnResume();
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

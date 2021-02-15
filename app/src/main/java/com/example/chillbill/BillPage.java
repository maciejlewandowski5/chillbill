package com.example.chillbill;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.chillbill.helpers.InfiniteScroller;
import com.example.chillbill.helpers.Utils;
import com.example.chillbill.model.Bill;
import com.example.chillbill.model.Category;
import com.example.chillbill.model.Product;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;


public class BillPage extends AppCompatActivity implements  FilterButtons.OnClickListener {

    private final String ARG_PROD_PARAM_OUT = "PRODINFO";
    private final String BILL_FOR_PROD = "BILL_FOR_PROD";
    private LinearLayout linearLayout;
    Bill bill;

    FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private InfiniteScroller<Product> infiniteScroller;
    FilterButtons filterButtons;

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
        infiniteScroller = new InfiniteScroller<>(linearLayout, 24 + 8 + 24, new InfiniteScroller.SpecificOnClickListener() {
            @Override
            public void onClick(View v, Serializable s,int i) {
                Intent intent = new Intent(that, ProductPropertiesEditor.class);
                intent.putExtra(BILL_FOR_PROD, bill);
                intent.putExtra(ARG_PROD_PARAM_OUT,i);
                that.startActivity(intent);
            }
        }, ListElment::newInstance, this);


        // Firebase
        db = FirebaseFirestore.getInstance();
        // googleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN);
        firebaseAuth = FirebaseAuth.getInstance();


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
        filterButtons.setOnClickListener(this);

        linlay.removeAllViews();
        androidx.fragment.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment billfrag = HistoryItemExtended.newInstance(bill);

        transaction.add(linlay.getId(), billfrag);
        transaction.commit();


        FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();

        transaction2.replace(R.id.fragment2,filterButtons);
        transaction2.commit();

    }


    public void populateContainer(Category category) {
        ArrayList<Product> tmp = new ArrayList();
        bill.getProductList().forEach(product -> {
            if (product.getCategory() == category) {
                tmp.add(product);
            }
        });
        infiniteScroller.populate(tmp);
    }



    public void refreshBill() {

        BillPage that = this;
        db.collection("Users").document(firebaseAuth.getCurrentUser().getUid()).collection("Bills").document(bill.getId())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                bill = documentSnapshot.toObject(Bill.class);
                infiniteScroller.populate(bill.getProductList());
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        String ARG_HIST_PARAM_OUT = "HISTINFO";
        Intent intent = getIntent();
        bill = (Bill) intent.getSerializableExtra(ARG_HIST_PARAM_OUT);
        refreshBill();
        filterButtons.refreshFilters();
        filterButtons.setAllToWhite();
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

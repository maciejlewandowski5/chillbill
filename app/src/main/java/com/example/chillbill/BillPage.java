package com.example.chillbill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.chillbill.model.Bill;
import com.example.chillbill.model.Category;
import com.example.chillbill.model.Product;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Date;

import static com.example.chillbill.StartScreen.dpToPx;

public class BillPage extends AppCompatActivity {

    private final String ARG_PROD_PARAM_OUT = "PRODINFO";
    private LinearLayout linearLayout;
    Bill bill;
    Button[] filterButtons;
    int[] colors;
    boolean[] activeFilters; //TODO: smarter filter, create way to show few categories at the same time
    FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_page);
        Intent intent = getIntent();

        activeFilters = new boolean[]{false, false, false, false, false};

        filterButtons = new Button[]{findViewById(R.id.orange_category_button), findViewById(R.id.purple_category_button),
                findViewById(R.id.yellow_category_button),
                findViewById(R.id.green_categoty_button),
                findViewById(R.id.blue_category_button)};

        colors = new int[]{ResourcesCompat.getColor(getResources(), R.color.orange, null),
                ResourcesCompat.getColor(getResources(), R.color.purple, null),
                ResourcesCompat.getColor(getResources(), R.color.yellow, null),
                ResourcesCompat.getColor(getResources(), R.color.green, null),
                ResourcesCompat.getColor(getResources(), R.color.blue, null)};

        String ARG_HIST_PARAM_OUT = "HISTINFO";
        bill = (Bill) intent.getSerializableExtra(ARG_HIST_PARAM_OUT);


        db = FirebaseFirestore.getInstance();
       // googleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN);
        firebaseAuth = FirebaseAuth.getInstance();


        linearLayout = findViewById(R.id.container_for_table_item_bill_list);
        LinearLayout linlay = findViewById(R.id.linearLayout7);

        linlay.removeAllViews();
        androidx.fragment.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment billfrag = HistoryItemExtended.newInstance(bill.getShopName(), bill.getTotalAmount(),
                bill.getCategoryPercentage().get(0).floatValue(), bill.getCategoryPercentage().get(1).floatValue(),
                bill.getCategoryPercentage().get(2).floatValue(), bill.getCategoryPercentage().get(3).floatValue(), bill.getCategoryPercentage().get(4).floatValue()
                ,new Date(), bill.getSavingsJar());
        transaction.add(linlay.getId(), billfrag);
        transaction.commit();

        displayProducts(bill.getProductList());
    }

    public void displayProducts(ArrayList<Product> products) {
        int i = 0;
        linearLayout.removeAllViews();
        androidx.fragment.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        for (Product product : products) {
            ConstraintLayout constraintLayout = new ConstraintLayout(this);
            Fragment fragment = ListElment.newInstance(product.getName(), product.getPrice(), product.getQuantity(), i % 2 != 0, product.getCategoryString(product));
            constraintLayout.setId(View.generateViewId());
            linearLayout.addView(constraintLayout);
            Button button = new Button(this);
            button.setId(View.generateViewId());
            button.setBackgroundColor(Color.TRANSPARENT);
            button.setLayoutParams(constraintLayout.getLayoutParams());
            button.setHeight(dpToPx(24 + 8 + 24, this));

            BillPage that = this;

            int finalI = i;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(that, ProductPropertiesEditor.class);
                    intent.putExtra(ARG_PROD_PARAM_OUT, product);
                    intent.putExtra("ARG_DATE_OUT", bill.getDate());
                    intent.putExtra("ARG_SHOPNAME_OUT", bill.getShopName());
                    intent.putExtra("ARG_PRODUCT_INDEX_OUT", finalI);
                    intent.putExtra("BILL_FOR_PROD",bill);
                    that.startActivity(intent);
                }

            });
            constraintLayout.addView(button);
            transaction.add(constraintLayout.getId(), fragment, product.getName() + product.getPrice() + product.getQuantity());
            i++;
        }
        transaction.commit();
    }

    public void displayProducts(ArrayList<Product> products, Category category) {
        int i = 0;
        androidx.fragment.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        for (Product product : products) {
            if (product.getCategory() == category) {
                ConstraintLayout constraintLayout = new ConstraintLayout(this);
                Fragment fragment = ListElment.newInstance(product.getName(), product.getPrice(), product.getQuantity(), i % 2 != 0, product.getCategoryString(product));
                constraintLayout.setId(View.generateViewId());

                linearLayout.addView(constraintLayout);
                Button button = new Button(this);
                button.setId(View.generateViewId());
                button.setBackgroundColor(Color.TRANSPARENT);
                button.setLayoutParams(constraintLayout.getLayoutParams());
                button.setHeight(dpToPx(24 + 8 + 24, this));

                int finalI = i;
                BillPage that = this;
                button.setOnClickListener(v -> {
                    Intent intent = new Intent(that, ProductPropertiesEditor.class);
                    intent.putExtra("ARG_PRODUCT_INDEX_OUT", finalI);
                    intent.putExtra("BILL_FOR_PROD",bill);
                    that.startActivity(intent);
                });
                constraintLayout.addView(button);

                transaction.add(constraintLayout.getId(), fragment, product.getName() + product.getPrice() + product.getQuantity());


            }
            i++;
        }
        transaction.commit();
    }

    public void sortOrange(View view) {
        if (!activeFilters[0]) {
            linearLayout.removeAllViews();
            displayProducts(bill.getProductList(), Category.ORANGE);
            Button button = (Button) view;
            button.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.orange, null));
            button.setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
            setColorsToWhite(0);
            activeFilters[0] = true;
        } else {
            linearLayout.removeAllViews();
            displayProducts(bill.getProductList());
            Button button = (Button) view;
            button.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
            button.setTextColor(ResourcesCompat.getColor(getResources(), R.color.orange, null));
            setColorsToWhite(0);
            activeFilters[0] = false;
        }
    }

    public void sortPurple(View view) {
        if (!activeFilters[1]) {
            linearLayout.removeAllViews();
            displayProducts(bill.getProductList(), Category.PURPLE);
            Button button = (Button) view;
            button.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.purple, null));
            button.setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
            setColorsToWhite(1);
            activeFilters[1] = true;
        } else {
            linearLayout.removeAllViews();
            displayProducts(bill.getProductList());
            Button button = (Button) view;
            button.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
            button.setTextColor(ResourcesCompat.getColor(getResources(), R.color.orange, null));
            setColorsToWhite(0);
            activeFilters[1] = false;
        }
    }

    public void sortYellow(View view) {
        if (!activeFilters[2]) {
            linearLayout.removeAllViews();
            displayProducts(bill.getProductList(), Category.YELLOW);
            Button button = (Button) view;
            button.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.yellow, null));
            button.setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
            setColorsToWhite(2);
            activeFilters[2] = true;
        } else {
            linearLayout.removeAllViews();
            displayProducts(bill.getProductList());
            Button button = (Button) view;
            button.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
            button.setTextColor(ResourcesCompat.getColor(getResources(), R.color.orange, null));
            setColorsToWhite(0);
            activeFilters[2] = false;
        }
    }

    public void sortGreen(View view) {
        if (!activeFilters[3]) {
            linearLayout.removeAllViews();
            displayProducts(bill.getProductList(), Category.GREEN);
            Button button = (Button) view;
            button.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.green, null));
            button.setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
            setColorsToWhite(3);
            activeFilters[3] = true;
        } else {
            linearLayout.removeAllViews();
            displayProducts(bill.getProductList());
            Button button = (Button) view;
            button.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
            button.setTextColor(ResourcesCompat.getColor(getResources(), R.color.orange, null));
            setColorsToWhite(0);
            activeFilters[3] = false;
        }
    }

    public void sortBlue(View view) {
        if (!activeFilters[4]) {
            linearLayout.removeAllViews();
            displayProducts(bill.getProductList(), Category.BLUE);
            Button button = (Button) view;
            button.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.blue, null));
            button.setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
            setColorsToWhite(4);
            activeFilters[4] = true;
        } else {
            linearLayout.removeAllViews();
            displayProducts(bill.getProductList());
            Button button = (Button) view;
            button.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
            button.setTextColor(ResourcesCompat.getColor(getResources(), R.color.orange, null));
            setColorsToWhite(0);
            activeFilters[4] = false;
        }
    }

    public void setColorsToWhite(int buttonNumber) {
        for (int i = 0; i < filterButtons.length; i++) {
            if (i != buttonNumber) {
                filterButtons[i].setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
                filterButtons[i].setTextColor(colors[i]);
            }
        }
    }

    public void refreshBill(){
        db.collection("Users").document(firebaseAuth.getCurrentUser().getUid()).collection("Bills").document(bill.getId())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                bill = documentSnapshot.toObject(Bill.class);
                displayProducts(bill.getProductList());
            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();
        String ARG_HIST_PARAM_OUT = "HISTINFO";
        Intent intent  = getIntent();
        bill = (Bill) intent.getSerializableExtra(ARG_HIST_PARAM_OUT);
        refreshBill();
    }
}

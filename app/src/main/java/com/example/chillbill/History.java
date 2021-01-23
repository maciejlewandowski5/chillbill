package com.example.chillbill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.chillbill.model.Bill;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


//TODO: Implemet sorting by biggest usage of bill in category and by date and by
//      jar type. Should be done by get requests to cloud.
public class History extends AppCompatActivity {
    LinearLayout linearLayout;
    ScrollView scrollView;
    int prevScrollPositon;
    int loadedElements;
    private final String ARG_HIST_PARAM_OUT = "HISTINFO";
    private FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    final int[] historyItemsLoaded = {0};
    private DocumentSnapshot lastVisible;
    ArrayList<Bill> bills;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        linearLayout = findViewById(R.id.linear_list_for_history_extended_item_object);
        scrollView = findViewById(R.id.history_scroll_view);
        // scrollView.getChildAt(0).getHeight();
        prevScrollPositon = 0;
        loadedElements = 0;

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        bills = new ArrayList<>();
        History that = this;



        //Search recepes listner
        textView = findViewById(R.id.textInputEditText);

        //TODO: Fix inifinty scroller
       // loadAllHistoryItemExtended();
      /*  db.collection("Users").document(firebaseAuth.getCurrentUser().getUid()).collection("Bills").orderBy("date", Query.Direction.ASCENDING).limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                lastVisible = document;
                            }

                            //Load starting last ten bills
                            loadHistoryItemExtended(historyItemsLoaded[0]);
                            scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                                @Override
                                public void onScrollChanged() {
                                    int scrollY = scrollView.getScrollY();
                                    //int fragmentScrolled = (int) Math.ceil((float) scrollY / (linearLayout.getChildAt(0).getHeight() + dptoPx(16)));
                                    int fragmentScrolled = (int) Math.ceil((float) scrollY / (dptoPx(72) + dptoPx(16)));
                                    float percentScrolled = (float) scrollY / (linearLayout.getHeight());


                                    if (prevScrollPositon < scrollY) {

                                        if ((float) fragmentScrolled / loadedElements >= 0.5) {
                                            loadHistoryItemExtended(historyItemsLoaded[0]);
                                            prevScrollPositon = scrollY;
                                        }
                                    }

                                }
                            });

                        } else {
                            Log.w("History", "Error getting documents.", task.getException());
                        }
                    }
                });

*/
    }

    public ArrayList<Bill> loadAllHistoryItemExtended() {
        //TODO:: Add get request
        bills = new ArrayList<>();


        db.collection("Users").document(firebaseAuth.getCurrentUser().getUid()).collection("Bills").orderBy("date", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Bill bill = document.toObject(Bill.class);
                                bills.add(bill);
                            }
                            displayHistoryItemsExtended(bills);

                        } else {
                            Log.w("History", "Error getting documents.", task.getException());
                        }
                    }
                });

        return bills;
    }
    public ArrayList<Bill> loadHistoryItemExtended(int alreadyLoadedElementsNumber) {
        //TODO:: Add get request
        bills = new ArrayList<>();


            db.collection("Users").document(firebaseAuth.getCurrentUser().getUid()).collection("Bills").orderBy("date", Query.Direction.ASCENDING).startAt(lastVisible).limit(5)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Bill bill = document.toObject(Bill.class);
                                    bills.add(bill);
                                    System.out.println("ITEM WAS LOADED");
                                    lastVisible = document;
                                }
                                displayHistoryItemsExtended(bills);
                                historyItemsLoaded[0] += 5;

                            } else {
                                Log.w("History", "Error getting documents.", task.getException());
                            }
                        }
                    });

        return bills;
    }

    public void displayHistoryItemsExtended(ArrayList<Bill> bills) {
        androidx.fragment.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        for (Bill bill : bills) {
            ConstraintLayout constraintLayout = new ConstraintLayout(this);
            HistoryItemExtended fragment = HistoryItemExtended.newInstance(bill.getShopName(),
                    bill.getTotalAmount(), bill.getCategoryPercentage().get(0).floatValue(), bill.getCategoryPercentage().get(1).floatValue(), bill.getCategoryPercentage().get(2).floatValue(),
                    bill.getCategoryPercentage().get(3).floatValue(), bill.getCategoryPercentage().get(4).floatValue(), bill.getDate(), bill.getSavingsJar());

            constraintLayout.setId(View.generateViewId());
            linearLayout.addView(constraintLayout);
            Button button = new Button(this);
            button.setId(View.generateViewId());
            button.setBackgroundColor(Color.TRANSPARENT);
            button.setLayoutParams(constraintLayout.getLayoutParams());
            // Hegiht of extended history item is 72dp + 16dp for margin
            button.setHeight(dptoPx(72 + 16));

            History that = this;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(that, BillPage.class);
                    intent.putExtra(ARG_HIST_PARAM_OUT, bill);
                    that.startActivity(intent);
                }

            });

            constraintLayout.addView(button);
            transaction.add(constraintLayout.getId(), fragment);
            loadedElements++;

        }

        transaction.commit();
    }


    int dptoPx(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    @Override
    protected void onResume() {
        super.onResume();
        linearLayout.removeAllViews();
        loadAllHistoryItemExtended();
    }

    public void search(View view) {
        ArrayList<Bill> sorted = new ArrayList<>();
        for(Bill bill: bills){
            if(bill.getShopName().contains(textView.getText())){
                sorted.add(bill);
            }
        }
        linearLayout.removeAllViews();
        displayHistoryItemsExtended(sorted);
    }
}

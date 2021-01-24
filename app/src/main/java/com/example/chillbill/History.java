package com.example.chillbill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

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

import com.example.chillbill.model.Category;
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
import java.util.Collections;
import java.util.Comparator;


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
    private boolean[] activeFilters;
    Button[] filterButtons;
    int[] colors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

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


        db.collection("Users").document(firebaseAuth.getCurrentUser().getUid()).collection("Bills").orderBy("date", Query.Direction.DESCENDING)
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
        activeFilters = new boolean[]{false, false, false, false, false};
        setColorsToWhite(0);
        filterButtons[0].setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
        filterButtons[0].setTextColor(colors[0]);
    }

    public void search(View view) {
        ArrayList<Bill> sorted = new ArrayList<>();
        for (Bill bill : bills) {
            if (bill.getShopName().contains(textView.getText())) {
                sorted.add(bill);
            }
        }
        linearLayout.removeAllViews();
        displayHistoryItemsExtended(sorted);
    }

    public void setColorsToWhite(int buttonNumber) {
        for (int i = 0; i < filterButtons.length; i++) {
            if (i != buttonNumber) {
                filterButtons[i].setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
                filterButtons[i].setTextColor(colors[i]);
            }
        }
    }


    public void sortByDate() {

        bills.sort(new Comparator<Bill>() {
            @Override
            public int compare(Bill o1, Bill o2) {
                return o2.getDate().compareTo(o1.getDate());
            }
        });
        linearLayout.removeAllViews();
        displayHistoryItemsExtended(bills);
    }

    public void sortLogic(View view, Category category) {

    }

    public void sortAndDisplay(Category category) {
        bills.sort(new Comparator<Bill>() {
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
                    return -1; // for categopry = null
                }
                double rhs = o2.getCategoryPercentage().get(toCmp);
                double lhs = o1.getCategoryPercentage().get(toCmp);
                return lhs > rhs ? -1 : (lhs < rhs) ? 1 : 0;
            }
        });
        linearLayout.removeAllViews();
        displayHistoryItemsExtended(bills);
    }

    public void sortYellowHistory(View view) {
        if (!activeFilters[2]) {
            linearLayout.removeAllViews();
            sortAndDisplay(Category.YELLOW);
            Button button = (Button) view;
            button.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.yellow, null));
            button.setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
            setColorsToWhite(2);
            activeFilters[2] = true;
        } else {
            linearLayout.removeAllViews();
            sortByDate();
            Button button = (Button) view;
            button.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
            button.setTextColor(ResourcesCompat.getColor(getResources(), R.color.yellow, null));
            setColorsToWhite(2);
            activeFilters[2] = false;
        }
    }

    public void sortPurpleHistory(View view) {
        if (!activeFilters[1]) {
            linearLayout.removeAllViews();
            sortAndDisplay(Category.PURPLE);
            Button button = (Button) view;
            button.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.purple, null));
            button.setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
            setColorsToWhite(1);
            activeFilters[1] = true;
        } else {
            linearLayout.removeAllViews();
            sortByDate();
            Button button = (Button) view;
            button.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
            button.setTextColor(ResourcesCompat.getColor(getResources(), R.color.purple, null));
            setColorsToWhite(1);
            activeFilters[1] = false;
        }
    }

    public void sortGreenHistory(View view) {
        if (!activeFilters[3]) {
            linearLayout.removeAllViews();
            sortAndDisplay(Category.GREEN);
            Button button = (Button) view;
            button.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.green, null));
            button.setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
            setColorsToWhite(3);
            activeFilters[3] = true;
        } else {
            linearLayout.removeAllViews();
            sortByDate();
            Button button = (Button) view;
            button.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
            button.setTextColor(ResourcesCompat.getColor(getResources(), R.color.green, null));
            setColorsToWhite(3);
            activeFilters[3] = false;
        }
    }

    public void sortOrangeHistory(View view) {
        if (!activeFilters[0]) {
            linearLayout.removeAllViews();
            sortAndDisplay(Category.ORANGE);
            Button button = (Button) view;
            button.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.orange, null));
            button.setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
            setColorsToWhite(0);
            activeFilters[0] = true;
        } else {
            linearLayout.removeAllViews();
            sortByDate();
            Button button = (Button) view;
            button.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
            button.setTextColor(ResourcesCompat.getColor(getResources(), R.color.orange, null));
            setColorsToWhite(0);
            activeFilters[0] = false;
        }
    }

    public void sortBlueHistory(View view) {
        if (!activeFilters[4]) {
            linearLayout.removeAllViews();
            sortAndDisplay(Category.BLUE);
            Button button = (Button) view;
            button.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.blue, null));
            button.setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
            setColorsToWhite(4);
            activeFilters[4] = true;
        } else {
            linearLayout.removeAllViews();
            sortByDate();
            Button button = (Button) view;
            button.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
            button.setTextColor(ResourcesCompat.getColor(getResources(), R.color.blue, null));
            setColorsToWhite(4);
            activeFilters[4] = false;
        }
    }
}

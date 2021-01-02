package com.example.chillbill;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.example.chillbill.model.Bill;
import com.example.chillbill.model.Category;
import com.example.chillbill.model.Product;

import java.util.ArrayList;
import java.util.Date;

//TODO: Implemet sorting by biggest usage of bill in category and by date and by
//      jar type. Should be done by get requests to cloud.
public class History extends AppCompatActivity {
    LinearLayout linearLayout;
    ScrollView scrollView;
    int prevScrollPositon;
    int loadedElements;
    private final String ARG_HIST_PARAM_OUT = "HISTINFO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        linearLayout = findViewById(R.id.linear_list_for_history_extended_item_object);
        scrollView = findViewById(R.id.history_scroll_view);
        // scrollView.getChildAt(0).getHeight();
        prevScrollPositon = 0;
        loadedElements = 0;

        final int[] historyItemsLoaded = {0};

        //Load starting last ten bills
        ArrayList<Bill> bills = loadHistoryItemExtended(historyItemsLoaded[0]);
        displayHistoryItemsExtended(bills);
        historyItemsLoaded[0] +=5;
        displayHistoryItemsExtended(bills);
        historyItemsLoaded[0] +=5;


        History that = this;
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scrollY = scrollView.getScrollY();
                int fragmentScrolled = (int) Math.ceil((float) scrollY / (linearLayout.getChildAt(0).getHeight() + dptoPx(16)));
                float percentScrolled = (float) scrollY / (linearLayout.getHeight());


                if (prevScrollPositon < scrollY) {

                    if ((float) fragmentScrolled / loadedElements >= 0.5) {
                        ArrayList<Bill> bills = loadHistoryItemExtended(historyItemsLoaded[0]);
                        displayHistoryItemsExtended(bills);
                        //loaded was 5 elments, if more should be changed for diffretn number;
                        historyItemsLoaded[0] += 5;
                        prevScrollPositon = scrollY;
                    }
                }

            }
        });

    }

    public ArrayList<Bill> loadHistoryItemExtended(int alreadyLoadedElementsNumber){
        //TODO:: Add get request
                ArrayList<Bill> bills = new ArrayList<>();
                for (int i=alreadyLoadedElementsNumber; i<alreadyLoadedElementsNumber+5;i++){


                    Bill bill = new Bill("Lidl", (float) (98.51*i/10f),new Date());
                    for(int j=0;j<12;j++){
                        Category category;
                        if(j%5==0){
                            category = Category.BLUE;
                        }
                        else if(j%5==1){
                            category = Category.PURPLE;
                        }
                        else if(j%5==2){
                            category = Category.ORANGE;
                        }
                        else if(j%5==3){
                            category = Category.GREEN;
                        }
                        else{
                            category = Category.YELLOW;
                        }
                        Product product = new Product("Product nr" + j,(j+i)*1.57f, (float) Math.sqrt((i-j)*(i-j)),category);
                        bill.addProduct(product);
                    }
                    bills.add(bill);
                }
                return bills;
    }

    public void displayHistoryItemsExtended(ArrayList<Bill> bills){
        androidx.fragment.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                for(Bill bill : bills){
        ConstraintLayout constraintLayout = new ConstraintLayout(this);
        HistoryItemExtended fragment = HistoryItemExtended.newInstance(bill.getShopName(),
                bill.getTotalAmount(), bill.getCategoryPercentage()[0], bill.getCategoryPercentage()[1],bill.getCategoryPercentage()[2],
                bill.getCategoryPercentage()[3],bill.getCategoryPercentage()[4], bill.getDate(), bill.getSavingsJar());

        constraintLayout.setId(View.generateViewId());
        linearLayout.addView(constraintLayout);
        Button button = new Button(this);
        button.setId(View.generateViewId());
        button.setBackgroundColor(Color.TRANSPARENT);
        button.setLayoutParams(constraintLayout.getLayoutParams());
        // Hegiht of extended history item is 72dp + 16dp for margin
        button.setHeight(dptoPx(72+16));

        History that =this;
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

}

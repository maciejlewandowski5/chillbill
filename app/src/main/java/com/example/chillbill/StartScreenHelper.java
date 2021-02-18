package com.example.chillbill;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.chillbill.BillPage;
import com.example.chillbill.FoodItem;
import com.example.chillbill.HistoryItem;
import com.example.chillbill.Recipe;
import com.example.chillbill.StartScreen;
import com.example.chillbill.helpers.AccountHelper;
import com.example.chillbill.helpers.FirestoreHelper;
import com.example.chillbill.helpers.ImageHelper;
import com.example.chillbill.helpers.InfiniteScroller;
import com.example.chillbill.helpers.ListenableInput;
import com.example.chillbill.helpers.Utils;
import com.example.chillbill.helpers.VolleyJsonHelper;
import com.example.chillbill.helpers.VolleyStringHelper;
import com.example.chillbill.model.Bill;
import com.example.chillbill.model.RecipeInformation;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;



public class StartScreenHelper {

    static StartScreen startActivity;

    public static void Initialize(){
        // Infinite scrollers
        StartScreen that = startActivity;
        startActivity.billScroller = new InfiniteScroller<>(historyContainer, 12 + 51, (v, s, i) -> {
            Intent intent = new Intent(that, BillPage.class);
            intent.putExtra(startActivity.ARG_HIST_PARAM_OUT, s);
            startActivity.startActivity(intent);
        }, HistoryItem::newInstance, startActivity);

        startActivity.recipeInformationInfiniteScroller = new InfiniteScroller<>(recipesContainer, 88, (v, s, i) -> {
            Intent intent = new Intent(that, Recipe.class);
            intent.putExtra(startActivity.ARG_HIST_PARAM_OUT, s);
            that.startActivity(intent);
        }, FoodItem::newInstance, startActivity);


        // Firestore helpers
        startActivity.newBillManager = new FirestoreHelper(new FirestoreHelper.FirestoreHelperListener() {
            @Override
            public void onError(Exception e) {
                Log.w(tag, "Error getting documents.", e);
                Utils.toastError(that);
            }

            @Override
            public void onGetDocument(Bill bill) {
                Intent intent = new Intent(that, BillPage.class);
                intent.putExtra(startActivity.ARG_HIST_PARAM_OUT, bill);
                that.startActivity(intent);
            }

            @Override
            public void onDocumentsProcessingFinished() {
                startActivity.setProgressBarInvisible();
            }
        });


        startActivity.firestoreHelper = new FirestoreHelper(new FirestoreHelper.FirestoreHelperListener() {
            @Override
            public void onError(Exception e) {
                Log.w(tag, "Error getting documents.", e);
                Utils.toastError(that);
            }

            @Override
            public void onDocumentsProcessingFinished() {
                startActivity.billScroller.populate(startActivity.billsInfo[0]);
            }

            @Override
            public void onGetDocument(Bill bill) {
                startActivity.billsInfo[0].add(bill);
            }

            @Override
            public void onTaskSuccessful() {
                startActivity.billsInfo[0] = new ArrayList<>();
            }
        });


        // Volley helpers
        startActivity.jasonReq = new VolleyJsonHelper(response -> {
            for (int i = 0; i < response.length(); i++) {
                try {
                    RecipeInformation recipeInformation = RecipeInformation.instantiate(new JSONObject(response.getString(i)));
                    startActivity.recipesInformation.add(recipeInformation);
                } catch (JSONException e) {
                    Log.w(tag, "Error getting documents.", e.getCause());
                    Utils.toastError(that);
                }
            }
            recipeInformationInfiniteScroller.populate(recipesInformation);
        }, error -> {
            recipesContainer.removeAllViews();
            Log.w(TAG, "Error getting documents.", error.getCause());
            Utils.toastError(that);
        });

        stringReq = new VolleyStringHelper(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                newBillManager.loadBill(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.toastError(that);
                setProgressBarInvisible();
            }
        }, this);

        // Text views
        textView.setOnTypingEndsListener(new ListenableInput.OnTypingEndsListener() {
            @Override
            public boolean onTypingEnds(TextView v, int actionId, KeyEvent event) {
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                recipesContainer.removeAllViews();
                progressBar = new ProgressBar(that, null, android.R.attr.progressBarStyleLarge);
                recipesContainer.addView(progressBar);
                recipesInformation = new ArrayList<>();

                jasonReq.getRecipesInfo(v.getText().toString(), that);
                return false;
            }
        });

        // ImageHelper

        imageHelper = new ImageHelper(new ImageHelper.CameraStared() {
            @Override
            public void cameraStared() {
                setProgressBarVisible();
            }
        }, new ImageHelper.GalleryStarted() {
            @Override
            public void galleryStarted() {
                setProgressBarVisible();
            }
        }, this);

        accountHelper = new AccountHelper(this);

    }
}

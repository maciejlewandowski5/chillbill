package com.example.chillbill;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentTransaction;

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

import java.io.FileNotFoundException;
import java.util.ArrayList;


public class StartScreen extends AppCompatActivity {


    private static final String TAG = "ChillBill_StartScreen";
    private static final int NUMBER_OF_LATEST_HIST_ITEMS = 3;
    private static final int CAMERA_REQUEST = 1888;
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final String ARG_HIST_PARAM_OUT = "HISTINFO";
    private static final int BILL_HIST_ITEM_HEIGHT = 12 + 51;
    private static final int RECIPE_ITEM_HEIGHT = 88;

    private LinearLayout recipesContainer;
    private ProgressBar progressBar;
    private Button addBill;
    private PopupMenu menu;

    InfiniteScroller<Bill> billScroller;
    InfiniteScroller<RecipeInformation> recipeInformationInfiniteScroller;
    VolleyJsonHelper jasonReq;
    VolleyStringHelper stringReq;
    FirestoreHelper firestoreHelper;
    FirestoreHelper newBillManager;
    ImageHelper imageHelper;
    AccountHelper accountHelper;


    private ArrayList<RecipeInformation> recipesInformation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);

        // Initialize variables
        recipesInformation = new ArrayList<>();
        final ArrayList<Bill>[] billsInfo = new ArrayList[]{new ArrayList<>()};

        // Initialize views
        recipesContainer = findViewById(R.id.recipes_container);
        LinearLayout historyContainer = findViewById(R.id.linearLayout10);
        progressBar = findViewById(R.id.progress_bar);
        addBill = findViewById(R.id.add_bill);
        ListenableInput textView = new ListenableInput(findViewById(R.id.textInputEditText));
        FragmentContainerView pieChart = findViewById(R.id.pieChartFragmentStart);
        menu = new PopupMenu(this, addBill);
        pieChart.removeAllViews();


        // Infinite scrollers
        StartScreen that = this;
        billScroller = new InfiniteScroller<>(historyContainer, BILL_HIST_ITEM_HEIGHT, (v, s, i) -> {
            Intent intent = new Intent(that, BillPage.class);
            intent.putExtra(ARG_HIST_PARAM_OUT, s);
            startActivity(intent);
        }, HistoryItem::newInstance, this);

        recipeInformationInfiniteScroller = new InfiniteScroller<>(recipesContainer, RECIPE_ITEM_HEIGHT, (v, s, i) -> {
            Intent intent = new Intent(that, Recipe.class);
            intent.putExtra(ARG_HIST_PARAM_OUT, s);
            that.startActivity(intent);
        }, FoodItem::newInstance, this);


        // FireStore helpers
        newBillManager = new FirestoreHelper(new FirestoreHelper.FirestoreHelperListener() {
            @Override
            public void onError(Exception e) {
                Log.w(TAG, "Error getting documents.", e);
                Utils.toastError(that);
            }

            @Override
            public void onGetDocument(Bill bill) {
                Intent intent = new Intent(that, BillPage.class);
                intent.putExtra(ARG_HIST_PARAM_OUT, bill);
                that.startActivity(intent);
            }

            @Override
            public void onDocumentsProcessingFinished() {
                setProgressBarInvisible();
            }
        });


        firestoreHelper = new FirestoreHelper(new FirestoreHelper.FirestoreHelperListener() {
            @Override
            public void onError(Exception e) {
                Log.w(TAG, "Error getting documents.", e);
                Utils.toastError(that);
            }

            @Override
            public void onDocumentsProcessingFinished() {
                billScroller.populate(billsInfo[0]);
            }

            @Override
            public void onGetDocument(Bill bill) {
                billsInfo[0].add(bill);
            }

            @Override
            public void onTaskSuccessful() {
                billsInfo[0] = new ArrayList<>();
            }
        });


        // Volley helpers
        jasonReq = new VolleyJsonHelper(response -> {
            for (int i = 0; i < response.length(); i++) {
                try {
                    RecipeInformation recipeInformation = RecipeInformation.instantiate(new JSONObject(response.getString(i)));
                    recipesInformation.add(recipeInformation);
                } catch (JSONException e) {
                    Log.w(TAG, "Error getting documents.", e.getCause());
                    Utils.toastError(that);
                }
            }
            recipeInformationInfiniteScroller.populate(recipesInformation);
        }, error -> {
            recipesContainer.removeAllViews();
            Log.w(TAG, "Error getting documents.", error.getCause());
            Utils.toastError(that);
        });

        stringReq = new VolleyStringHelper(response -> newBillManager.loadBill(response),
                error -> {
                    Utils.toastError(that);
                    setProgressBarInvisible();
                }, this);

        // Text views
        textView.setOnTypingEndsListener((v, actionId, event) -> {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            recipesContainer.removeAllViews();
            progressBar = new ProgressBar(that, null, android.R.attr.progressBarStyleLarge);
            recipesContainer.addView(progressBar);
            recipesInformation = new ArrayList<>();

            jasonReq.getRecipesInfo(v.getText().toString(), that);
            return false;
        });

        // ImageHelper

        imageHelper = new ImageHelper(this::setProgressBarVisible, this::setProgressBarVisible, this);

        accountHelper = new AccountHelper(this);

        // Populate menu
        menu.getMenu().add(R.string.add_manually);
        menu.getMenu().add(R.string.take_from_galery);
        menu.getMenu().add(R.string.take_picture);

        // Initial configuration
        setProgressBarInvisible();
        Utils.checkCameraPermission(this, CAMERA_REQUEST_CODE);
        jasonReq.getRecipesInfo("naleśniki", this);

        // Perform transactions
        PieChartFragment pieChartFragment = PieChartFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(
                android.R.anim.fade_in,
                android.R.anim.fade_out,
                android.R.anim.bounce_interpolator,
                android.R.anim.bounce_interpolator);
        transaction.add(R.id.pieChartFragmentStart, pieChartFragment);
        transaction.commit();


    }

    public void setProgressBarInvisible() {
        progressBar.setVisibility(View.INVISIBLE);
        addBill.setVisibility(View.VISIBLE);
    }

    public void setProgressBarVisible() {
        progressBar.setVisibility(View.VISIBLE);
        addBill.setVisibility(View.INVISIBLE);
    }


    @Override
    protected void onResume() {
        super.onResume();
        firestoreHelper.loadBills(NUMBER_OF_LATEST_HIST_ITEMS);
    }


    public void dispatchTakePictureIntent(View view) {
        menu.setOnMenuItemClickListener(item -> {
            if (item.getTitle().toString().equals(getResources().getString(R.string.add_manually))) {
                // TODO: Create module for manual bill adding;
                setProgressBarVisible();
                Utils.toastMessage(getString(R.string.future_feature), this);
                setProgressBarInvisible();
            } else if (item.getTitle().toString().equals(getResources().getString(R.string.take_from_galery))) {
                imageHelper.startGallery();
            } else {
                imageHelper.startCamera();
            }
            return false;
        });
        menu.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Utils.toastMessage(getString(R.string.camera_permission_granted), this);
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Utils.toastMessage(getString(R.string.camera_permissons_denied), this);
                addBill.setClickable(true);
                setProgressBarInvisible();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        StartScreen that = this;
        if (resultCode == RESULT_OK) {
            Uri file = Uri.EMPTY;
            if (requestCode == 0) { // taking picture with camera
                try {
                    file = Uri.fromFile(imageHelper.getCurrentPhoto());
                } catch (FileNotFoundException e) {
                    Utils.toastError(that);
                    setProgressBarInvisible();
                    return;
                }
            } else if (requestCode == 1) { // selecting from gallery
                file = data.getData();
            }
            imageHelper.sendToStorage(file, stringReq);
        }
    }


    public void startHistory(View view) {
        Intent intent = new Intent(this, History.class);
        startActivity(intent);
    }

    public void startChartsPage(View view) {
        Intent intent = new Intent(this, StatisticsActivity.class);
        startActivity(intent);
    }


    public void logOut(View view) {
        PopupMenu menu = new PopupMenu(this, view);
        menu.getMenu().add(R.string.log_out);
        menu.setOnMenuItemClickListener(item -> {
            if (item.getTitle().toString().equals(getResources().getString(R.string.log_out))) {
                accountHelper.signOut(TAG);
            }
            return false;
        });
        menu.show();
    }


}
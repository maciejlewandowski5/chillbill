package com.example.chillbill;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import androidx.fragment.app.FragmentContainerView;
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
import com.google.android.material.textfield.TextInputEditText;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;


public class StartScreenHelper {

    static final String TAG = "ChillBill_StartScreen";
    static final int NUMBER_OF_LATEST_HIST_ITEMS = 3;
    static final int CAMERA_REQUEST = 1888;
    static final int CAMERA_REQUEST_CODE = 100;
    static final int CAMERA_PERMISSION_CODE = 100;
    private static final String ARG_HIST_PARAM_OUT = "HISTINFO";
    private static final int BILL_HIST_ITEM_HEIGHT = 12 + 51;
    private static final int RECIPE_ITEM_HEIGHT = 88;

    private LinearLayout recipesContainer;
    private LinearLayout historyContainer;
    private ProgressBar progressBar;
    private Button addBill;
    private PopupMenu menu;
    private ListenableInput textView;
    private FragmentContainerView pieChart;

    private InfiniteScroller<Bill> billScroller;
    private InfiniteScroller<RecipeInformation> recipeInformationInfiniteScroller;
    private VolleyJsonHelper jasonReq;
    private VolleyStringHelper stringReq;
    private FirestoreHelper firestoreHelper;
    private FirestoreHelper newBillManager;
    private ImageHelper imageHelper;
    private AccountHelper accountHelper;
    private final StartScreen startScreen;

    private ArrayList<Bill>[] billsInfo;
    private ArrayList<RecipeInformation> recipesInformation;

    public StartScreenHelper(StartScreen startScreen) {
        this.startScreen = startScreen;
    }

    private View findViewById(int viewId) {
        return startScreen.findViewById(viewId);
    }

    private void startActivity(Intent intent) {
        startScreen.startActivity(intent);
    }

    public void initialize() {
        initializeVariables();
        initializeViews();
        initializeInfinityScroller();
        initializeFirestoreHelpers();
        initializeVolleyHelpers();
        initializeSmartTextView();
        initializeImageHelper();
        initializeAccountHelper();
        populateMenu();

    }

    private void populateMenu(){
        menu.getMenu().add(R.string.add_manually);
        menu.getMenu().add(R.string.take_from_galery);
        menu.getMenu().add(R.string.take_picture);
    }

    private void initializeImageHelper(){
        accountHelper = new AccountHelper(startScreen);
    }

    private void initializeAccountHelper(){
        imageHelper = new ImageHelper(this::setProgressBarInvisible, this::setProgressBarInvisible, startScreen);
    }

    private void initializeVariables() {
        recipesInformation = new ArrayList<>();
        billsInfo = new ArrayList[]{new ArrayList<>()};
    }

    private void initializeViews() {
        recipesContainer = (LinearLayout) findViewById(R.id.recipes_container);
        historyContainer = (LinearLayout) findViewById(R.id.linearLayout10);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        addBill = (Button) findViewById(R.id.add_bill);
        textView = new ListenableInput((TextInputEditText) findViewById(R.id.textInputEditText));
        pieChart = (FragmentContainerView) findViewById(R.id.pieChartFragmentStart);
        menu = new PopupMenu(startScreen, addBill);
        pieChart.removeAllViews();
    }

    private void initializeInfinityScroller() {
        StartScreen that = startScreen;
        billScroller = new InfiniteScroller<>(historyContainer, BILL_HIST_ITEM_HEIGHT, (v, s, i) -> {
            Intent intent = new Intent(that, BillPage.class);
            intent.putExtra(ARG_HIST_PARAM_OUT, s);
            startActivity(intent);
        }, HistoryItem::newInstance, startScreen);

        recipeInformationInfiniteScroller = new InfiniteScroller<>(recipesContainer, RECIPE_ITEM_HEIGHT, (v, s, i) -> {
            Intent intent = new Intent(that, Recipe.class);
            intent.putExtra(ARG_HIST_PARAM_OUT, s);
            that.startActivity(intent);
        }, FoodItem::newInstance, startScreen);
    }

    private void initializeFirestoreHelpers() {
        newBillManager = new FirestoreHelper(new FirestoreHelper.FirestoreHelperListener() {
            @Override
            public void onError(Exception e) {
                Log.w(TAG, "Error getting documents.", e);
                Utils.toastError(startScreen);
            }

            @Override
            public void onGetDocument(Bill bill) {
                Intent intent = new Intent(startScreen, BillPage.class);
                intent.putExtra(ARG_HIST_PARAM_OUT, bill);
                startScreen.startActivity(intent);
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
                Utils.toastError(startScreen);
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

    }

    private void initializeVolleyHelpers() {
        jasonReq = new VolleyJsonHelper(response -> {
            for (int i = 0; i < response.length(); i++) {
                try {
                    RecipeInformation recipeInformation = RecipeInformation.instantiate(new JSONObject(response.getString(i)));
                    recipesInformation.add(recipeInformation);
                } catch (JSONException e) {
                    Log.w(TAG, "Error getting documents.", e.getCause());
                    Utils.toastError(startScreen);
                }
            }
            recipeInformationInfiniteScroller.populate(recipesInformation);
        }, error -> {
            recipesContainer.removeAllViews();
            Log.w(TAG, "Error getting documents.", error.getCause());
            Utils.toastError(startScreen);
        });

        stringReq = new VolleyStringHelper(response -> newBillManager.loadBill(response),
                error -> {
                    Utils.toastError(startScreen);
                    setProgressBarInvisible();
                }, startScreen);
    }

    private void initializeSmartTextView() {
        textView.setOnTypingEndsListener((v, actionId, event) -> {
            InputMethodManager inputManager = (InputMethodManager) startScreen.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(startScreen.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            recipesContainer.removeAllViews();
            progressBar = new ProgressBar(startScreen, null, android.R.attr.progressBarStyleLarge);
            recipesContainer.addView(progressBar);
            recipesInformation = new ArrayList<>();

            jasonReq.getRecipesInfo(v.getText().toString(),startScreen);
            return false;
        });

    }

    public VolleyJsonHelper getJasonReq() {
        return jasonReq;
    }


    public void setProgressBarInvisible() {
        progressBar.setVisibility(View.INVISIBLE);
        addBill.setVisibility(View.VISIBLE);
    }

    public void setProgressBarVisible() {
        progressBar.setVisibility(View.VISIBLE);
        addBill.setVisibility(View.INVISIBLE);
    }

    public FirestoreHelper getFirestoreHelper() {
        return firestoreHelper;
    }

    public PopupMenu getMenu() {
        return menu;
    }

    public ImageHelper getImageHelper() {
        return imageHelper;
    }

    public Button getAddBill() {
        return addBill;
    }

    public VolleyStringHelper getStringReq() {
        return stringReq;
    }

    public AccountHelper getAccountHelper() {
        return accountHelper;
    }
}

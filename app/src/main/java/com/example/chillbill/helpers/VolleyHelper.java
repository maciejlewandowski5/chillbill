package com.example.chillbill.helpers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.chillbill.BillPage;
import com.example.chillbill.StartScreen;
import com.example.chillbill.model.Bill;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import org.json.JSONArray;

import java.util.Objects;

import static android.content.ContentValues.TAG;

public class VolleyHelper<T> {

    private static final String URL = "https://chillbill-bv4675ezoa-ey.a.run.app/api";
    private static final String PARSE_BILL = "/vision/parseBill?userImage=";
    private static final String RECIPES = "/recipes/get?keyword=";
    protected static final String ADD_VOTE = "/addVote";
    protected static final String REMOVE_VOTE = "/removeVote";


    protected Response.Listener<T> responseListener;
    protected Response.ErrorListener errorListener;
    protected FirebaseAuth firebaseAuth;

    public VolleyHelper(Response.Listener<T> responseListener, Response.ErrorListener errorListener) {
        this.responseListener = responseListener;
        this.errorListener = errorListener;
        this.firebaseAuth = FirebaseAuth.getInstance();
    }

    protected String getParseBillURL(){
        return  URL + PARSE_BILL + Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
    }
    protected String getRecipesURL(){
        return  URL + RECIPES;
    }



}

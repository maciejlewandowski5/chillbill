package com.example.chillbill.helpers;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class VolleyStringHelper extends VolleyHelper<String> {
    private FirebaseAuth firebaseAuth;
    private Context context;
    private ArrayList <StringRequest> requests;

    public VolleyStringHelper(Response.Listener<String> responseListener, Response.ErrorListener errorListener, Context context) {
        super(responseListener, errorListener);
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.context = context;
        this.requests = new ArrayList<StringRequest>();
    }

    public void requestBillParsing(String billId) {

        String url = super.getParseBillURL() + "/" + billId;
        RequestQueue ExampleRequestQueue = Volley.newRequestQueue(context);
        StringRequest ExampleRequest = new StringRequest(Request.Method.GET, url,responseListener,errorListener);

        ExampleRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) {

            }
        });
        ExampleRequestQueue.add(ExampleRequest);

    }


    public void addRemoveVoteRequestToQueue(String shopName, String category, String productName){
        prepareChangeVoteRequest(shopName,category,productName,VolleyHelper.REMOVE_VOTE);
    }
    public void addAddVoteRequestToQueue(String shopName, String category, String productName){
        prepareChangeVoteRequest(shopName,category,productName,VolleyHelper.ADD_VOTE);
    }

    private void prepareChangeVoteRequest(String shopName, String category, String productName, String endpoint) {
        StringRequest sr = new StringRequest(Request.Method.POST, "https://chillbill-bv4675ezoa-ey.a.run.app/api/votes/" + endpoint,
              super.responseListener,super.errorListener) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                HashMap<String, String> params2 = new HashMap<String, String>();
                params2.put("productName", productName);
                params2.put("shopName", shopName);
                params2.put("category", category);

                return new JSONObject(params2).toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        requests.add(sr);
    }
    public void sendQueue(Context context){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        for(StringRequest stringRequest : requests){
            requestQueue.add(stringRequest);
        }
    }


}

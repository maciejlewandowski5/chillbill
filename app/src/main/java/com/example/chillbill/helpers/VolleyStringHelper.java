package com.example.chillbill.helpers;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;

public class VolleyStringHelper extends VolleyHelper<String> {
    private FirebaseAuth firebaseAuth;
    private Context context;

    public VolleyStringHelper(Response.Listener<String> responseListener, Response.ErrorListener errorListener, FirebaseAuth firebaseAuth, Context context) {
        super(responseListener, errorListener);
        this.firebaseAuth = firebaseAuth;
        this.context = context;
    }

    public void requestBillParsing(String billId) {

        String url = "https://chillbill-bv4675ezoa-ey.a.run.app/api/vision/parseBill?userImage=" + firebaseAuth.getCurrentUser().getUid() + "/" + billId;
        final String[] result = {""};
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

}

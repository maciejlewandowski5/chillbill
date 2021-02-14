package com.example.chillbill.helpers;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

public class VolleyJsonHelper extends VolleyHelper<JSONArray> {
    public VolleyJsonHelper(Response.Listener<JSONArray> responseListener, Response.ErrorListener errorListener) {
        super(responseListener, errorListener);
    }

    public void getRecipesInfo(String title, Context context) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://chillbill-bv4675ezoa-ey.a.run.app/api/recipes/get?keyword=" + title;
        RequestQueue ExampleRequestQueue = Volley.newRequestQueue(context);
        JsonArrayRequest ExampleRequest = new JsonArrayRequest(Request.Method.GET, url, null, responseListener, errorListener);
        ExampleRequestQueue.add(ExampleRequest);

    }
}

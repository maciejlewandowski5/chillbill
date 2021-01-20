package com.example.chillbill.factory;
import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;


public class jsonBillRequest<T> extends Request<T> {

    byte[] photo;

    public jsonBillRequest(int method, String url, @Nullable Response.ErrorListener listener) {
        super(method, url, listener);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        return null;
    }

    @Override
    protected void deliverResponse(T response) {

    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        return photo;
    }

    public void setBody(byte[] photo){
        this.photo = photo;
    }
}

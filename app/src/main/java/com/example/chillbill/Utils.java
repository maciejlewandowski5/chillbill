package com.example.chillbill;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import java.io.IOException;

public class Utils {
    /**
     * Uses {@link com.google.firebase.auth.FirebaseUser#getIdToken(boolean) getIdToken} under the hood to fetch idToken.
     * @param forceRefresh force refreshes the token. If false token will be valid for 1 hour and next calls will use cached value (no calls to Firebase)
     * @return idToken for current FirebaseUser
     * @throws IOException thrown when user is not logged in or fetched idToken is null
     */
    public static String getIdToken(boolean forceRefresh) throws IOException{
        try {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if(user == null) {
                throw  new Exception("User is not logged in");
            } else {
                Task<GetTokenResult> task = user.getIdToken(forceRefresh);
                GetTokenResult tokenResult = Tasks.await(task);
                String idToken = tokenResult.getToken();

                if(idToken == null) {
                    throw new Exception("idToken is null");
                } else {
                    return idToken;
                }
            }
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }
}

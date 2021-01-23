package com.example.chillbill;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

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

    public static Task<QuerySnapshot> getBillsInRange(Date from, Date to) {
        FirebaseAuth fa = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        return db.collection("Users").document(fa.getCurrentUser().getUid()).collection("Bills")
                .whereGreaterThanOrEqualTo("date", from)
                .whereLessThanOrEqualTo("date", to)
                .get();
    }

    public static Task<QuerySnapshot> getBills() {
        FirebaseAuth fa = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection("Users").document(fa.getCurrentUser().getUid()).collection("Bills");
        return reference.get();
    }

    public static Date getFirstDayOfTheMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, cal.getActualMinimum(Calendar.HOUR_OF_DAY));
        return cal.getTime();
    }
}

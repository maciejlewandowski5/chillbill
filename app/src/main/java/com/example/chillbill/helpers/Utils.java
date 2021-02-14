package com.example.chillbill.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.chillbill.R;
import com.example.chillbill.StartScreen;
import com.example.chillbill.model.Category;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Utils {

    public static int dpToPx(float dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static int spToPx(float sp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

    public static Date getFirstDayOfTheMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, cal.getActualMinimum(Calendar.HOUR_OF_DAY));
        return cal.getTime();
    }



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

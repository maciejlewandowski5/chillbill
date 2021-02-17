package com.example.chillbill.helpers;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.chillbill.HistoryItem;
import com.example.chillbill.MainActivity;
import com.example.chillbill.R;
import com.example.chillbill.StartScreen;
import com.example.chillbill.model.Category;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.Query;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

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
     *
     * @param forceRefresh force refreshes the token. If false token will be valid for 1 hour and next calls will use cached value (no calls to Firebase)
     * @return idToken for current FirebaseUser
     * @throws IOException thrown when user is not logged in or fetched idToken is null
     */
    public static String getIdToken(boolean forceRefresh) throws IOException {
        try {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                throw new Exception("User is not logged in");
            } else {
                Task<GetTokenResult> task = user.getIdToken(forceRefresh);
                GetTokenResult tokenResult = Tasks.await(task);
                String idToken = tokenResult.getToken();

                if (idToken == null) {
                    throw new Exception("idToken is null");
                } else {
                    return idToken;
                }
            }
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    public static void toastError(Context context) {
        Toast.makeText(context, context.getResources().getString(R.string.SmgWrg), Toast.LENGTH_LONG).show();
    }

    public static void toastMessage(String message, Context context) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static int[] getPrimaryColors(Fragment fragment) {
        return new int[]{
                ContextCompat.getColor(fragment.requireActivity(), R.color.purple),
                ContextCompat.getColor(fragment.requireActivity(), R.color.yellow),
                ContextCompat.getColor(fragment.requireActivity(), R.color.green),
                ContextCompat.getColor(fragment.requireActivity(), R.color.orange),
                ContextCompat.getColor(fragment.requireActivity(), R.color.blue)
        };
    }

    public static int[] getSecondaryColors(Fragment fragment) {
        return new int[]{
                ContextCompat.getColor(fragment.requireActivity(), R.color.dark_purple),
                ContextCompat.getColor(fragment.requireActivity(), R.color.dark_yellow),
                ContextCompat.getColor(fragment.requireActivity(), R.color.dark_green),
                ContextCompat.getColor(fragment.requireActivity(), R.color.dark_orange),
                ContextCompat.getColor(fragment.requireActivity(), R.color.dark_blue)
        };
    }

    public static Date localDateToDate(LocalDate dateToConvert) {
        return java.util.Date.from(dateToConvert.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }

    public static String formatPriceLocale(float price) {
        return String.format(Locale.getDefault(), "%.2f", price);
    }

    public static float parsePriceLocale(String price, Context context) {
        DecimalFormat decimalFormat = new DecimalFormat("0.##", DecimalFormatSymbols.getInstance(Locale.getDefault()));
        try {
            return Objects.requireNonNull(decimalFormat.parse(price)).floatValue();
        } catch (ParseException e) {
            Utils.toastError(context);
            return 0;
        }
    }


}

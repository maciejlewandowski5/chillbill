package com.example.chillbill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.icu.text.IDNA;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceFragment;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.chillbill.infos.RecipeInformation;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import java.util.ArrayList;


public class StartScreen extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;

    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    LinearLayout recipesContainer;
    ArrayList<RecipeInformation> recipeInformations;
    private final String ARG_RECEP_PARAM_OUT = "RECEPINFO";


    int[] sampleImages = {R.drawable.image_1, R.drawable.image_2, R.drawable.image_3};
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);

        HistoryItem first = HistoryItem.newInstance("LIDL", 621.50f, 20, 10, 20, 40, 10);
        HistoryItem second = HistoryItem.newInstance("Biedronka", 128.12f, 10, 20, 30, 30, 10);
        HistoryItem third = HistoryItem.newInstance("Empik", 12.50f, 5, 25, 35, 20, 15);

        FitItem fitItem = FitItem.newInstance("Aktywnosc fizyczna", "Lorem impsum dolores . . .", R.drawable.image_2);
        FitItem fitItem2 = FitItem.newInstance("Aktywnosc fizyczna", "Lorem impsum dolores . . .", R.drawable.image_2);
        FitItem fitItem3 = FitItem.newInstance("Aktywnosc fizyczna", "Lorem impsum dolores . . .", R.drawable.image_2);

        FoodItem foodItem1 = FoodItem.newInstance("Rzepak", R.drawable.food_item_background_image);
        FoodItem foodItem2 = FoodItem.newInstance("Szczaw", R.drawable.food_item_background_image);
        FoodItem foodItem3 = FoodItem.newInstance("Pomidorowa", R.drawable.food_item_background_image);
        FoodItem foodItem4 = FoodItem.newInstance("Salatka z Avocado", R.drawable.food_item_background_image);
        FoodItem foodItem5 = FoodItem.newInstance("Zupa paprykowa", R.drawable.food_item_background_image);
        FoodItem foodItem6 = FoodItem.newInstance("Cytrusy", R.drawable.food_item_background_image);
        FoodItem foodItem7 = FoodItem.newInstance("Kiszonki", R.drawable.food_item_background_image);


        androidx.fragment.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.fragment2, first);
        transaction.replace(R.id.fragment3, second);
        transaction.replace(R.id.fragment4, third);

        transaction.replace(R.id.fragment, fitItem);
        transaction.replace(R.id.fragment1, fitItem2);
        transaction.replace(R.id.fragment12, fitItem3);


        transaction.commit();



        StartScreen that = this;



        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }

        recipesContainer = findViewById(R.id.recipes_container);

        recipeInformations = new ArrayList<>();
        getRecipeInfos("naleśniki");

        TextView textView = findViewById(R.id.textInputEditText);
        textView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event != null &&
                                event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (event == null || !event.isShiftPressed()) {
                        recipesContainer.removeAllViews();
                        recipeInformations = new ArrayList<>();
                        getRecipeInfos(v.getText().toString());
                        return true; // consume.
                    }
                }

                return false;
            }
        });


    }

    public void getRecipeInfos(String title){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://chillbill-bv4675ezoa-ey.a.run.app/api/recipes/get?keyword="+ title;

        RequestQueue ExampleRequestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest ExampleRequest = new JsonArrayRequest(Request.Method.GET, url,null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                 org.json.JSONArray jsonArray = response;
                for(int i=0;i<jsonArray.length();i++){
                    try {
                        RecipeInformation recipeInformation = RecipeInformation.instantiate(new JSONObject(jsonArray.getString(i)));
                        recipeInformations.add(recipeInformation);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                displayFoodItems(recipeInformations);
            }

        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
            }
        });
        ExampleRequestQueue.add(ExampleRequest);
    }

    public void displayFoodItems(ArrayList<RecipeInformation> recipes) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        int i = 0;
        int previousId = recipesContainer.getId();
        for (RecipeInformation info : recipeInformations) {
            i++;
            ConstraintLayout constraintLayout = new ConstraintLayout(this);

            // TODO: Change to image from info url
            Fragment fragment = FoodItem.newInstance(info.getTitle(), R.drawable.food_item_background_image);

            constraintLayout.setId(View.generateViewId());

            System.out.println(previousId);


            recipesContainer.addView(constraintLayout);

            Button button = new Button(this);
            button.setId(View.generateViewId());


            button.setBackgroundColor(Color.TRANSPARENT);
            button.setLayoutParams(constraintLayout.getLayoutParams());
            //88 is height of food item with margins
            button.setHeight(dpToPx(88,this));

            StartScreen that = this;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(that, Recepe.class);
                    intent.putExtra(ARG_RECEP_PARAM_OUT, info);
                    that.startActivity(intent);
                }
            });

            constraintLayout.addView(button);
            transaction.add(constraintLayout.getId(), fragment, info.getTitle());
            previousId = constraintLayout.getId();
        }
        transaction.commit();

    }

    public static int dpToPx(float dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
    public static int spToPx(float sp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

    public void dispatchTakePictureIntent(View view) {

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
        } else {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }
    }

    private void sendPostRequest(byte[] postData) {
        //  String urlParameters  = "param1=a&param2=b&param3=c";
        //postData       = urlParameters.getBytes( StandardCharsets.UTF_8 );
        int postDataLength = postData.length;
        String request = "http://31.182.129.128/extractText";
        URL url = null;
        try {
            url = new URL(request);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //conn.addRequestProperty("image",postData);
        conn.setDoOutput(true);
        conn.setInstanceFollowRedirects(false);
        try {
            conn.setRequestMethod("POST");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("charset", "utf-8");
        conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
        conn.setUseCaches(false);
        try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
            wr.write(postData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            photo.recycle();
            sendPostRequest(byteArray);

        }
    }
}
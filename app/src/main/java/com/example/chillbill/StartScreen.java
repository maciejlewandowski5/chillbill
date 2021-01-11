package com.example.chillbill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.util.Size;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.chillbill.model.Bill;
import com.example.chillbill.model.Category;
import com.example.chillbill.model.Product;
import com.example.chillbill.model.RecipeInformation;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Blob;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class StartScreen extends AppCompatActivity {

    private static final String TAG = "StartScreen";

    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    LinearLayout recipesContainer;
    LinearLayout historyContainer;
    ArrayList<RecipeInformation> recipeInformations;
    private StorageReference mStorageRef;

    private final String ARG_RECEP_PARAM_OUT = "RECEPINFO";
    private final String ARG_HIST_PARAM_OUT = "HISTINFO";

    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;
    private FirebaseFirestore db;

    int[] sampleImages = {R.drawable.image_1, R.drawable.image_2, R.drawable.image_3};

    String currentPhotoPath;
    File currentPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);

        FitItem fitItem = FitItem.newInstance("Aktywnosc fizyczna", "Lorem impsum dolores . . .", R.drawable.image_2);
        FitItem fitItem2 = FitItem.newInstance("Aktywnosc fizyczna", "Lorem impsum dolores . . .", R.drawable.image_2);
        FitItem fitItem3 = FitItem.newInstance("Aktywnosc fizyczna", "Lorem impsum dolores . . .", R.drawable.image_2);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();


        androidx.fragment.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.fragment, fitItem);
        transaction.replace(R.id.fragment1, fitItem2);
        transaction.replace(R.id.fragment12, fitItem3);

        transaction.commit();


        //Camera permissons
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }

        recipesContainer = findViewById(R.id.recipes_container);
        historyContainer = findViewById(R.id.linearLayout10);

        recipeInformations = new ArrayList<>();
        getRecipeInfos("naleśniki");

        //Search recepes listner
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
        ArrayList<Bill> billInfos = new ArrayList<>();

        // here server should return only last three;
        Bill bill = new Bill("Biedronka", 326.12f, new Date());
        Bill bill2 = new Bill("Empik", 326.12f, new Date());
        Bill bill3 = new Bill("Lidl", 326.12f, new Date());

        Product product = new Product("Mąka", 5.50f, 5.5f, Category.BLUE);

        for (int i = 0; i < 20; i++) {
            bill.addProduct(product);
            bill2.addProduct(product);
            bill3.addProduct(product);
        }

        billInfos.add(bill);
        billInfos.add(bill2);
        billInfos.add(bill3);

        displayHistoryItems(billInfos)
        ;

        googleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN);
        firebaseAuth = FirebaseAuth.getInstance();

        Button signOutButton = findViewById(R.id.sign_out_button);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.w(TAG, "Signed out of google");
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        Toast.makeText(getApplicationContext(), "You Signed out", Toast.LENGTH_LONG).show();
                        startActivity(intent);
                    }
                });
            }
        });
    }

    public void getRecipeInfos(String title) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://chillbill-bv4675ezoa-ey.a.run.app/api/recipes/get?keyword=" + title;

        RequestQueue ExampleRequestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest ExampleRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                org.json.JSONArray jsonArray = response;
                for (int i = 0; i < jsonArray.length(); i++) {
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

    public void displayHistoryItems(ArrayList<Bill> bills) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        int i = 0;
        for (Bill bill : bills) {
            i++;
            ConstraintLayout constraintLayout = new ConstraintLayout(this);
            Fragment fragment = HistoryItem.newInstance(bill.getShopName(), bill.getTotalAmount(), bill.getCategoryPercentage().get(0).floatValue()
                    , bill.getCategoryPercentage().get(1).floatValue(), bill.getCategoryPercentage().get(2).floatValue(), bill.getCategoryPercentage().get(3).floatValue(), bill.getCategoryPercentage().get(4).floatValue());

            constraintLayout.setId(View.generateViewId());
            historyContainer.addView(constraintLayout);
            Button button = new Button(this);
            button.setId(View.generateViewId());
            button.setBackgroundColor(Color.TRANSPARENT);
            button.setLayoutParams(constraintLayout.getLayoutParams());
            // Height of historyItem. 12 is height of a margin
            button.setHeight(dpToPx(51 + 12, this));

            StartScreen that = this;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(that, BillPage.class);
                    intent.putExtra(ARG_HIST_PARAM_OUT, bill);
                    that.startActivity(intent);
                }
            });

            constraintLayout.addView(button);
            transaction.add(constraintLayout.getId(), fragment, bill.getShopName() + bill.getDate() + bill.getTotalAmount());
        }
        transaction.commit();
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

            recipesContainer.addView(constraintLayout);

            Button button = new Button(this);
            button.setId(View.generateViewId());
            button.setBackgroundColor(Color.TRANSPARENT);
            button.setLayoutParams(constraintLayout.getLayoutParams());
            //88 is height of food item with margins
            button.setHeight(dpToPx(88, this));

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
    //TODO: change initial photo to photo downloaded from web for food items

    public static int dpToPx(float dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static int spToPx(float sp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

    public void dispatchChoosePictureIntent(View view) {

        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, 1);//one can be replaced with any action code
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        currentPhoto=image;
        return image;
    }

    public void dispatchTakePictureIntent(View view) {

      /*  if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
        } else {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, 0);
        }*/

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 0);
            }
        }

    }
    private String requestBillParsing(String billId) {


        StartScreen that = this;
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://chillbill-bv4675ezoa-ey.a.run.app/api/vision/parseBill?userImage="+ firebaseAuth.getCurrentUser().getUid()+ "/" + billId;
        System.out.println(url);
        final String[] result = {""};
        RequestQueue ExampleRequestQueue = Volley.newRequestQueue(this);
        StringRequest ExampleRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("Response");
                System.out.println(response);
                System.out.println();


                db.collection("Users").document(firebaseAuth.getCurrentUser().getUid()).collection("Bills").document(response)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    Bill bill = task.getResult().toObject(Bill.class);

                                    Intent intent = new Intent(that, BillPage.class);
                                    intent.putExtra(ARG_HIST_PARAM_OUT, bill);
                                    that.startActivity(intent);

                                } else {
                                    Log.w(TAG, "Error getting documents.", task.getException());
                                }
                            }
                        });

                result[0] = response;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Error");
                System.out.println(error);
            }
        });

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
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        ExampleRequestQueue.add(ExampleRequest);
    return result[0];

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


        switch (requestCode) {
            case 0: // taking picture with camera
                if (resultCode == RESULT_OK) {
                   Uri file = Uri.fromFile(currentPhoto);
                    StorageReference riversRef = mStorageRef.child("/" + firebaseAuth.getCurrentUser().getUid() + "/" + file.hashCode());
                    riversRef.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            System.out.println("HO+RRAY");
                            requestBillParsing(String.valueOf(file.hashCode()));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle unsuccessful uploads
                            // ...
                        }
                    });

                }
                break;
            case 1: // selecting from gallery
                if (resultCode == RESULT_OK) {
                    Uri file = data.getData();
                    StorageReference riversRef = mStorageRef.child("/" + firebaseAuth.getCurrentUser().getUid() + "/" + file.hashCode());

                    riversRef.putFile(file)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // Get a URL to the uploaded content
                                    System.out.println("HO+RRAY");
                                    requestBillParsing(String.valueOf(file.hashCode()));
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle unsuccessful uploads
                                    // ...
                                }
                            });
                }
                break;
        }
    }


    public void startHistory(View view) {
        Intent intent = new Intent(this, History.class);
        startActivity(intent);
    }

    public void startCharts(View view) {
        Intent intent = new Intent(this, StatisticsActivity.class);
        startActivity(intent);
    }
}
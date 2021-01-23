package com.example.chillbill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.ActionBar;
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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
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
import com.example.chillbill.factory.NotifyingThread;
import com.example.chillbill.factory.ThreadCompleteListener;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
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

    private ProgressBar progressBar;
    private Button addBill;


    int[] sampleImages = {R.drawable.image_1, R.drawable.image_2, R.drawable.image_3};

    String currentPhotoPath;
    File currentPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();

        progressBar = findViewById(R.id.progress_bar);
        addBill = findViewById(R.id.add_bill);

        progressBar.setVisibility(View.INVISIBLE);
        addBill.setVisibility(View.VISIBLE);

        //Camera permissons
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }

        recipesContainer = findViewById(R.id.recipes_container);
        historyContainer = findViewById(R.id.linearLayout10);

        recipeInformations = new ArrayList<>();
        getRecipeInfos("naleśniki");
        StartScreen that= this;

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
                        progressBar = new ProgressBar(that, null, android.R.attr.progressBarStyleLarge);
                        recipesContainer.addView(progressBar);
                        recipeInformations = new ArrayList<>();
                        getRecipeInfos(v.getText().toString());
                        return true; // consume.
                    }
                }

                return false;
            }
        });

        googleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN);
        firebaseAuth = FirebaseAuth.getInstance();
        FragmentContainerView pieChart = findViewById(R.id.pieChartFragmentStart);
        pieChart.setOnClickListener(v -> {
            startCharts();
        });
       // loadLastestHistoryItems();
    }

    public void loadLastestHistoryItems(){
        ArrayList<Bill> billInfos = new ArrayList<>();
        db.collection("Users").document(firebaseAuth.getCurrentUser().getUid()).collection("Bills").orderBy("date", Query.Direction.ASCENDING).limit(3)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Bill bill = document.toObject(Bill.class);
                                billInfos.add(bill);
                            }
                            displayHistoryItems(billInfos);

                        } else {
                            Log.w("History", "Error getting documents.", task.getException());
                        }
                    }
                });

    }

    public void getRecipeInfos(String title) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://chillbill-bv4675ezoa-ey.a.run.app/api/recipes/get?keyword=" + title;
        StartScreen that = this;
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
                recipesContainer.removeAllViews();
                displayFoodItems(recipeInformations);
            }

        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                recipesContainer.removeAllViews();
                Toast.makeText(that, "Something went wrong, try again later.", Toast.LENGTH_LONG).show();
            }
        });
        ExampleRequestQueue.add(ExampleRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        historyContainer.removeAllViews();
        loadLastestHistoryItems();
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
        StartScreen that = this;
        for (RecipeInformation info : recipeInformations) {
            i++;
            ConstraintLayout constraintLayout = new ConstraintLayout(this);

            FoodItem fragment = FoodItem.newInstance(info.getTitle(), info.getImageURL());


            constraintLayout.setId(View.generateViewId());

            recipesContainer.addView(constraintLayout);

            Button button = new Button(this);
            button.setId(View.generateViewId());
            button.setBackgroundColor(Color.TRANSPARENT);
            button.setLayoutParams(constraintLayout.getLayoutParams());
            //88 is height of food item with margins
            button.setHeight(dpToPx(88, this));

            //StartScreen that = this;
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
        currentPhoto = image;
        return image;
    }

    public void dispatchTakePictureIntent(View view) {

        StartScreen that = this;
        PopupMenu menu = new PopupMenu(this, view);

        menu.getMenu().add(R.string.add_manually);
        menu.getMenu().add(R.string.take_from_galery);
        menu.getMenu().add(R.string.take_picture);


        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle().toString().equals(getResources().getString(R.string.add_manually))) {

                } else if (item.getTitle().toString().equals(getResources().getString(R.string.take_from_galery))) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, 1);//one can be replaced with any action code
                  view.setClickable(false);
                  view.setVisibility(View.INVISIBLE);
                  progressBar.setVisibility(View.VISIBLE);

                } else {
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
                            Uri photoURI = FileProvider.getUriForFile(that,
                                    "com.example.android.fileprovider",
                                    photoFile);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(takePictureIntent, 0);
                            view.setClickable(false);
                            view.setVisibility(View.INVISIBLE);
                            progressBar.setVisibility(View.VISIBLE);
                        }
                    }

                }

                return false;
            }
        });

        menu.show();

    }

    private String requestBillParsing(String billId) {
        StartScreen that = this;
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://chillbill-bv4675ezoa-ey.a.run.app/api/vision/parseBill?userImage=" + firebaseAuth.getCurrentUser().getUid() + "/" + billId;
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
                                    addBill.setClickable(true);
                                    addBill.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.INVISIBLE);

                                } else {
                                    Log.w(TAG, "Error getting documents.", task.getException());
                                    Toast.makeText(that, "Something went wrong, try again later.", Toast.LENGTH_LONG).show();
                                    addBill.setClickable(true);
                                    addBill.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.INVISIBLE);
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
                            addBill.setClickable(true);
                            addBill.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
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
                                    addBill.setClickable(true);
                                    addBill.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.INVISIBLE);
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

    public void startCharts() {
        Intent intent = new Intent(this, StatisticsActivity.class);
        startActivity(intent);
    }

    public void logOut(View view) {
        StartScreen that = this;
        PopupMenu menu = new PopupMenu(this, view);

        menu.getMenu().add(R.string.log_out);

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle().toString().equals(getResources().getString(R.string.log_out))) {
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
                return false;
            }
        });
        menu.show();

    }

}
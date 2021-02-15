package com.example.chillbill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.chillbill.helpers.FirestoreHelper;
import com.example.chillbill.helpers.InfiniteScroller;


import com.example.chillbill.helpers.ListenableInput;
import com.example.chillbill.helpers.VolleyHelper;
import com.example.chillbill.helpers.VolleyJsonHelper;
import com.example.chillbill.helpers.VolleyStringHelper;
import com.example.chillbill.model.Bill;
import com.example.chillbill.model.RecipeInformation;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;


public class StartScreen extends AppCompatActivity {


    private static final String TAG = "s";

    private static final int NAM_OF_LATEST_HIST_ITEMS = 3;
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
    PopupMenu menu;


    int[] sampleImages = {R.drawable.image_1, R.drawable.image_2, R.drawable.image_3};

    String currentPhotoPath;
    File currentPhoto;
    InfiniteScroller<Bill> billScroller;
    InfiniteScroller<RecipeInformation> recipeInformationInfiniteScroller;
    VolleyJsonHelper jasonReq;
    VolleyStringHelper stringReq;
    FirestoreHelper firestoreHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);

        // Firebase
        mStorageRef = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();
        googleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN);
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize views
        recipesContainer = findViewById(R.id.recipes_container);
        historyContainer = findViewById(R.id.linearLayout10);
        progressBar = findViewById(R.id.progress_bar);
        addBill = findViewById(R.id.add_bill);
        ListenableInput textView = new ListenableInput(findViewById(R.id.textInputEditText));
        FragmentContainerView pieChart = findViewById(R.id.pieChartFragmentStart);
        PopupMenu menu = new PopupMenu(this, addBill);
        pieChart.removeAllViews();

        StartScreen that = this;
        billScroller = new InfiniteScroller<>(historyContainer, 12 + 51, new InfiniteScroller.SpecificOnClickListener() {
            @Override
            public void onClick(View v, Serializable s, int i) {
                Intent intent = new Intent(that, BillPage.class);
                intent.putExtra(ARG_HIST_PARAM_OUT,s);
                startActivity(intent);
            }
        }, HistoryItem::newInstance, this);

        recipeInformationInfiniteScroller = new InfiniteScroller<>(recipesContainer, 88, new InfiniteScroller.SpecificOnClickListener() {
            @Override
            public void onClick(View v, Serializable s, int i) {
                Intent intent = new Intent(that, Recepe.class);
                intent.putExtra(ARG_HIST_PARAM_OUT,s);
                that.startActivity(intent);
            }
        }, FoodItem::newInstance, this);

        jasonReq = new VolleyJsonHelper(new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        RecipeInformation recipeInformation = RecipeInformation.instantiate(new JSONObject(response.getString(i)));
                        recipeInformations.add(recipeInformation);
                    } catch (JSONException e) {
                        Log.w("History", "Error getting documents.", e.getCause());
                    }
                }
                recipesContainer.removeAllViews();
                recipeInformationInfiniteScroller.populate(recipeInformations);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                recipesContainer.removeAllViews();
                Log.w("History", "Error getting documents.", error.getCause());
                Toast.makeText(that, "Something went wrong, try again later.", Toast.LENGTH_LONG).show();
            }
        });

        stringReq = new VolleyStringHelper(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
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

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(that, "Something went wrong, try again later.", Toast.LENGTH_LONG).show();
                addBill.setClickable(true);
                addBill.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }
        },firebaseAuth,this);

        final ArrayList<Bill>[] billsInfo = new ArrayList[]{new ArrayList<>()};

        firestoreHelper = new FirestoreHelper(firebaseAuth, db, new FirestoreHelper.OnGetDocument() {
            @Override
            public void onGetDocument(QueryDocumentSnapshot document) {
                Bill bill = document.toObject(Bill.class);
                billsInfo[0].add(bill);
            }
        }, new FirestoreHelper.OnDocumentsProcessingFinished() {
            @Override
            public void onDocumentsProcessingFinished() {
                billScroller.populate(billsInfo[0]);
            }
        }, new FirestoreHelper.OnError() {
            @Override
            public void onError(Exception e) {
                Log.w("History", "Error getting documents.", e);
            }
        }, new FirestoreHelper.OnTaskSuccessful() {
            @Override
            public void OnTaskSuccessful() {
                billsInfo[0] = new ArrayList<>();
            }
        });

        // Populate menu
        menu.getMenu().add(R.string.add_manually);
        menu.getMenu().add(R.string.take_from_galery);
        menu.getMenu().add(R.string.take_picture);

        setProgressBarInvisible();

        checkCameraPersmission();

        recipeInformations = new ArrayList<>();
        jasonReq.getRecipesInfo("naleśniki",this);

        // Set listeners
        textView.setOnTypingEndsListener(new ListenableInput.OnTypingEndsListener() {
            @Override
            public boolean onTypingEnds(TextView v, int actionId, KeyEvent event) {
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                recipesContainer.removeAllViews();
                progressBar = new ProgressBar(that, null, android.R.attr.progressBarStyleLarge);
                recipesContainer.addView(progressBar);
                recipeInformations = new ArrayList<>();

                jasonReq.getRecipesInfo(v.getText().toString(),that);
                return false;
            }
        });

        PieChartFragment pieChartFragment = PieChartFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,android.R.anim.bounce_interpolator,android.R.anim.bounce_interpolator);
        transaction.add(R.id.pieChartFragmentStart, pieChartFragment);
        transaction.commit();



    }

    public void setProgressBarInvisible() {
        progressBar.setVisibility(View.INVISIBLE);
        addBill.setVisibility(View.VISIBLE);
    }

    public void setProgressBarVisible() {
        progressBar.setVisibility(View.VISIBLE);
        addBill.setVisibility(View.INVISIBLE);
    }

    public void checkCameraPersmission(){
        //Camera permissons
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }
    }





    @Override
    protected void onResume() {
        super.onResume();
        historyContainer.removeAllViews();
        firestoreHelper.loadHistoryItems(NAM_OF_LATEST_HIST_ITEMS);
    }


    private File createImageFile() throws IOException {
        // Date format used for storage, no need to localize it.
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        currentPhotoPath = image.getAbsolutePath();
        currentPhoto = image;
        return image;
    }

    public void dispatchTakePictureIntent(View view) {

        StartScreen that = this;


        menu.setOnMenuItemClickListener(item -> {
            if (item.getTitle().toString().equals(getResources().getString(R.string.add_manually))) {

                // TODO: Create module for manual bill adding;

            } else if (item.getTitle().toString().equals(getResources().getString(R.string.take_from_galery))) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, 1);
                setProgressBarVisible();

            } else {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        Toast.makeText(that, "Something went wrong, try again later.", Toast.LENGTH_LONG).show();
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(that,
                                "com.example.android.fileprovider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, 0);
                        setProgressBarVisible();
                    }
                }

            }

            return false;
        });

        menu.show();

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
                addBill.setClickable(true);
                addBill.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
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
                            stringReq.requestBillParsing(String.valueOf(file.hashCode()));
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
                                    stringReq.requestBillParsing(String.valueOf(file.hashCode()));
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
    public void startChartsPage(View view) {
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
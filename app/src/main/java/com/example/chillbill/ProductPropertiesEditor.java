package com.example.chillbill;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.chillbill.model.Bill;
import com.example.chillbill.model.Category;
import com.example.chillbill.model.Product;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;

public class ProductPropertiesEditor extends AppCompatActivity {
    private static final String TAG = "dunno";
    private final String ARG_PROD_PARAM_OUT = "PRODINFO";
    TextView name;
    TextView quantity;
    TextView price;
    private FirebaseAuth firebaseAuth;
    DocumentReference documentReference;
    FirebaseFirestore db;
    FirebaseFirestore dc;
    Bill bill;
    Category selectedCategory;
    RadioButton[] radioButtons;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_properties_editor);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        bill = (Bill) intent.getSerializableExtra("BILL_FOR_PROD");
        name = findViewById(R.id.name_to_change);
        quantity = findViewById(R.id.editTextNumberDecimal2);
        price = findViewById(R.id.editTextNumberDecimal);
        int index = (Integer) intent.getSerializableExtra(ARG_PROD_PARAM_OUT);

        radioButtons = new RadioButton[]{findViewById(R.id.radioButton5),
                findViewById(R.id.radioButton4),
                findViewById(R.id.radioButton3),
                findViewById(R.id.radioButton2),
                findViewById(R.id.radioButton)};

        name.setText(bill.getProductList().get(index).getName());
        quantity.setText(String.format("%.2f", bill.getProductList().get(index).getQuantity()));
        price.setText(String.format("%.2f", bill.getProductList().get(index).getPrice()));

        if (bill.getProductList().get(index).getCategory() == Category.PURPLE) {
            radioButtons[0].setChecked(true);
        } else if (bill.getProductList().get(index).getCategory() == Category.YELLOW) {
            radioButtons[1].setChecked(true);
        } else if (bill.getProductList().get(index).getCategory() == Category.GREEN) {
            radioButtons[2].setChecked(true);
        } else if (bill.getProductList().get(index).getCategory() == Category.ORANGE) {
            radioButtons[3].setChecked(true);
        } else if (bill.getProductList().get(index).getCategory() == Category.BLUE) {
            radioButtons[4].setChecked(true);
        }


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


    }

    public void setNewValuesForProduct(View view) {
        if (!name.getText().toString().equals("") && !quantity.getText().toString().equals("") && !price.getText().toString().equals("")) {
            Log.d(TAG, "setNewValuesForProduct: ");
            String name1 = name.getText().toString();
            String quantity1 = quantity.getText().toString();
            String price1 = price.getText().toString();


            RequestQueue ExampleRequestQueue = Volley.newRequestQueue(this);

            Intent intent = getIntent();
            String billName = (String) intent.getSerializableExtra("ARG_SHOPNAME_OUT");
            Date billDate = (Date) intent.getSerializableExtra("ARG_DATE_OUT");
            int index = (int) intent.getSerializableExtra("ARG_PRODUCT_INDEX_OUT");
            String sIndex = String.valueOf(index);//Now it will return "10"

            if (radioButtons[0].isChecked()) {
                selectedCategory = Category.PURPLE;
            } else if (radioButtons[1].isChecked()) {
                selectedCategory = Category.YELLOW;
            } else if (radioButtons[2].isChecked()) {
                selectedCategory = Category.GREEN;
            } else if (radioButtons[3].isChecked()) {
                selectedCategory = Category.ORANGE;
            } else if (radioButtons[4].isChecked()) {
                selectedCategory = Category.BLUE;
            } else {
                selectedCategory = null;
            }

            StringRequest removeReq = null;
            StringRequest addReq = null;


            bill.getProductList().get(index).setName(name1);
            bill.getProductList().get(index).setPrice(Float.parseFloat(price1.replace(",", ".")));
            bill.getProductList().get(index).setQuantity(Float.parseFloat(quantity1.replace(",", ".")));

            removeReq = sendChangeVoteRequest(bill.getShopName(), new String(Product.getCategoryString(bill.getProductList().get(index)).toLowerCase()), name1, "removeVote");
            bill.getProductList().get(index).setCategory(selectedCategory);
            addReq = sendChangeVoteRequest(bill.getShopName(), new String(Product.getCategoryString(bill.getProductList().get(index)).toLowerCase()), name1, "addVote");


            bill.updateCategories();
            StringRequest finalRemoveReq1 = removeReq;
            StringRequest finalAddReq1 = addReq;
            db.collection("Users").document(firebaseAuth.getCurrentUser().getUid()).collection("Bills").document(bill.getId()).update("productList", bill.getProductList()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    ExampleRequestQueue.add(finalRemoveReq1);
                    ExampleRequestQueue.add(finalAddReq1);
                }
            });
            db.collection("Users").document(firebaseAuth.getCurrentUser().getUid()).collection("Bills").document(bill.getId()).update("categoryPercentage",bill.getCategoryPercentage()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    System.out.println("Fixxed");
                }
            });
            onBackPressed();
        }
    }

    private StringRequest sendChangeVoteRequest(String shopName, String category, String productName, String endpoint) {
        StringRequest sr = new StringRequest(Request.Method.POST, "https://chillbill-bv4675ezoa-ey.a.run.app/api/votes/" + endpoint, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(endpoint + "  productName: " + productName + "   shopName: " + shopName + "   category: " + category);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                HashMap<String, String> params2 = new HashMap<String, String>();
                params2.put("productName", productName);
                params2.put("shopName", shopName);
                params2.put("category", category);

                return new JSONObject(params2).toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        return sr;
    }
}

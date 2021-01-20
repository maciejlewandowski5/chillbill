package com.example.chillbill;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

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

import java.util.Date;

public class ProductPropertiesEditor extends AppCompatActivity {
    private static final String TAG ="dunno" ;
    private final String ARG_PROD_PARAM_OUT = "PRODINFO";
    Product product;
    TextView name;
    TextView quantity;
    TextView price;
    private FirebaseAuth firebaseAuth;
    DocumentReference documentReference;
    FirebaseFirestore db;
    FirebaseFirestore dc;
    Bill bill;
    Category selectedCategory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_properties_editor);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        product = (Product) intent.getSerializableExtra(ARG_PROD_PARAM_OUT);
        String billName = (String) intent.getSerializableExtra("ARG_SHOPNAME_OUT");
        Date billDate = (Date) intent.getSerializableExtra("ARG_DATE_OUT");
        int index = (int) intent.getSerializableExtra("ARG_PRODUCT_INDEX_OUT");
        bill = (Bill)intent.getSerializableExtra("BILL_FOR_PROD");
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        name = findViewById(R.id.name_to_change);
        quantity = findViewById(R.id.editTextNumberDecimal2);
        price = findViewById(R.id.editTextNumberDecimal);



        name.setText(product.getName());
        quantity.setText(String.format("%.2f", product.getQuantity()));
        price.setText(String.format("%.2f", product.getPrice()));


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        String category = spinner.getSelectedItem().toString();
        if(category.equals("Multimedia")){
            selectedCategory = Category.BLUE;
        }else if (category.equals("Meble")){
            selectedCategory = Category.GREEN;
        }else if (category.equals("Elektronika")){
            selectedCategory = Category.ORANGE;
        }else if (category.equals("Żywnośc")){
            selectedCategory = Category.PURPLE;
        }else if (category.equals("Ubrania")){
            selectedCategory = Category.YELLOW;
        }
    }

    public void setNewValuesForProduct(View view) {
        if(name.getText().toString()!=""&& quantity.getText().toString()!=""&& price.getText().toString()!="")
        {Log.d(TAG, "setNewValuesForProduct: ");
        String name1 = name.getText().toString();
        String quantity1 = quantity.getText().toString();
        String price1 = price.getText().toString();





        Intent intent = getIntent();
        String billName = (String) intent.getSerializableExtra("ARG_SHOPNAME_OUT");
        Date billDate = (Date) intent.getSerializableExtra("ARG_DATE_OUT");
        int index = (int) intent.getSerializableExtra("ARG_PRODUCT_INDEX_OUT");
        String sIndex=String.valueOf(index);//Now it will return "10"
        db.collection("Users").document(firebaseAuth.getCurrentUser().getUid()).collection("Bills")
          .whereEqualTo("shopName", billName)
          .whereEqualTo("date",billDate).get()
          .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                String docId;
                String path;

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    docId= documentSnapshot.getId();
                    bill.getProductList().get(index).setName(name1);
                    bill.getProductList().get(index).setPrice(Float.parseFloat(price1.replace(",",".")));
                    bill.getProductList().get(index).setQuantity(Float.parseFloat(quantity1.replace(",",".")));
                    bill.getProductList().get(index).setCategory(selectedCategory);
                    db.collection("Users").document(firebaseAuth.getCurrentUser().getUid()).collection("Bills").document(docId).update("productList",bill.getProductList());


                    
                }


            }
        });





        product.setName(name.getText().toString());
       // product.setQuantity(Float.parseFloat(quantity.getText().toString()));
       // product.setPrice(Float.parseFloat(price.getText().toString()));
        onBackPressed();}
        else {

        }
    }
}

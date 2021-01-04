package com.example.chillbill;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.chillbill.model.Bill;
import com.example.chillbill.model.Product;

import java.text.NumberFormat;

public class ProductPropertiesEditor extends AppCompatActivity {
    private final String ARG_PROD_PARAM_OUT = "PRODINFO";
    Product product;
    TextView name;
    TextView quantity;
    TextView price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_properties_editor);

        Intent intent = getIntent();
        product = (Product) intent.getSerializableExtra(ARG_PROD_PARAM_OUT);

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
    }

    public void setNewValuesForProduct(View view) {

        product.setName(name.getText().toString());
       // product.setQuantity(Float.parseFloat(quantity.getText().toString()));
       // product.setPrice(Float.parseFloat(price.getText().toString()));
        onBackPressed();
    }
}
package com.example.chillbill;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chillbill.helpers.FirestoreHelper;
import com.example.chillbill.helpers.Utils;
import com.example.chillbill.helpers.VolleyStringHelper;
import com.example.chillbill.model.Bill;
import com.example.chillbill.model.Category;
import com.example.chillbill.model.Product;

public class ProductPropertiesEditor extends AppCompatActivity {

    private static final String ARG_PROD_PARAM_OUT = "PRODINFO";
    private static final String BILL_FOR_PROD = "BILL_FOR_PROD";

    private TextView name;
    private TextView quantity;
    private TextView price;
    private RadioButton[] radioButtons;

    Bill bill;
    Category selectedCategory;
    Integer index;

    VolleyStringHelper volleyStringHelper;
    FirestoreHelper firestoreHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_properties_editor);

        // Firebase
        ProductPropertiesEditor that = this;
        volleyStringHelper = new VolleyStringHelper(response -> {/*not used -> not implemented*/},
                error -> {/*not used -> not implemented*/}
                , that);
        firestoreHelper = new FirestoreHelper(new FirestoreHelper.FirestoreHelperListener() {
            @Override
            public void onSuccessCategoryPercentageUpdate() {
                volleyStringHelper.sendQueue(that);
            }
        });

        // Get bill
        Intent intent = getIntent();
        bill = (Bill) intent.getSerializableExtra(BILL_FOR_PROD);
        index = (Integer) intent.getSerializableExtra(ARG_PROD_PARAM_OUT);

        // Initialise views
        name = findViewById(R.id.name_to_change);
        quantity = findViewById(R.id.editTextNumberDecimal2);
        price = findViewById(R.id.editTextNumberDecimal);
        radioButtons = initializeRadioButtons();

        // Set
        name.setText(bill.getProductList().get(index).getName());
        quantity.setText(Utils.formatPriceLocale(bill.getProductList().get(index).getQuantity()));
        price.setText(Utils.formatPriceLocale(bill.getProductList().get(index).getPrice()));
        setInitialCategory();

    }


    public void setNewValuesForProduct(View view) {
        if (!name.getText().toString().equals("") && !quantity.getText().toString().equals("") && !price.getText().toString().equals("")) {
            String name1 = name.getText().toString();
            String quantity1 = quantity.getText().toString();
            String price1 = price.getText().toString();

            selectCategoryFromRadioButton();

            Product tmp = bill.getProductList().get(index);
            tmp.setName(name1);
            tmp.setPrice(Utils.parsePriceLocale(price1, this));
            tmp.setQuantity(Utils.parsePriceLocale(quantity1, this));

            volleyStringHelper.addRemoveVoteRequestToQueue(bill.getShopName(),
                    Product.getCategoryString(tmp).toLowerCase(), name1);
            tmp.setCategory(selectedCategory);
            volleyStringHelper.addAddVoteRequestToQueue(bill.getShopName(),
                    Product.getCategoryString(tmp).toLowerCase(), name1);

            bill.updateCategories();
            firestoreHelper.updateBill(bill);

            onBackPressed();
        }
    }

    private RadioButton[] initializeRadioButtons() {
        return new RadioButton[]{findViewById(R.id.radioButton5),
                findViewById(R.id.radioButton4),
                findViewById(R.id.radioButton3),
                findViewById(R.id.radioButton2),
                findViewById(R.id.radioButton)};
    }

    public void setInitialCategory() {
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

    }

    public void selectCategoryFromRadioButton() {
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
    }

}

package com.example.chillbill;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chillbill.model.Product;
import com.example.chillbill.model.RecipeInformation;

import java.io.Serializable;


public class ListElment extends Fragment {


    private String title;
    private float price;
    private float quantity;
    private boolean isEven;
    private String category;

    public ListElment() {
        // Required empty public constructor
    }




    public static ListElment newInstance(Serializable ...serializable) {
        ListElment fragment = new ListElment();
        Bundle args = new Bundle();
        args.putSerializable("serializable",serializable[0]);
        args.putSerializable("i",serializable[1]);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Product product = (Product) getArguments().getSerializable("serializable");

            title = product.getName();
            price = product.getPrice();
            quantity = product.getQuantity();
            isEven =  (int)getArguments().getSerializable("i")%2==0;
            category = Product.getCategoryString(product);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list_elment, container, false);

        TextView titleTextView = root.findViewById(R.id.table_element_name);
        TextView priceTextView = root.findViewById(R.id.table_element_price);
        ImageView categoryImageView = root.findViewById(R.id.table_elment_category_color);
        ConstraintLayout constraintLayout = root.findViewById(R.id.table_elment_container);
       // TextView quantityTextView = root.findViewById(R.id.textView2);

        if (isEven) {
            constraintLayout.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
        } else {
            constraintLayout.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.very_light_grey));
        }
        titleTextView.setText(title);
        priceTextView.setText(String.format("%.2f", price));
       // quantityTextView.setText(String.format("%.2f", quantity));
        if(category=="purple"){
            categoryImageView.setImageResource(R.drawable.purple_small_circle);
        }else if(category == "yellow"){
            categoryImageView.setImageResource(R.drawable.yellow_small_circle);
        }else if(category == "green"){
            categoryImageView.setImageResource(R.drawable.green_small_circle);
        }else if(category == "orange"){
            categoryImageView.setImageResource(R.drawable.orange_small_circle);
        }else if(category == "blue"){
            categoryImageView.setImageResource(R.drawable.blue_small_circle);
        }else if(category == "null"){
            categoryImageView.setImageResource(R.drawable.gray_small_circle);
        }

        return root;
    }
}
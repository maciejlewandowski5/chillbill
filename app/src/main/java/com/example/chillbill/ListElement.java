package com.example.chillbill;

import android.annotation.SuppressLint;
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

import java.io.Serializable;


public class ListElement extends Fragment {


    private String title;
    private String category;
    private float price;
    private boolean isEven;


    public ListElement() {
        // Required empty public constructor
    }


    public static ListElement newInstance(Serializable ...serializable) {
        ListElement fragment = new ListElement();
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
            isEven =  (int)getArguments().getSerializable("i")%2==0;
            category = Product.getCategoryString(product);
        }
    }

    @SuppressLint("DefaultLocale") // For price formatting
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list_elment, container, false);

        TextView titleTextView = root.findViewById(R.id.table_element_name);
        TextView priceTextView = root.findViewById(R.id.table_element_price);
        ImageView categoryImageView = root.findViewById(R.id.table_elment_category_color);
        ConstraintLayout constraintLayout = root.findViewById(R.id.table_elment_container);

        if (isEven) {
            constraintLayout.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
        } else {
            constraintLayout.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.very_light_grey));
        }
        titleTextView.setText(title);
        priceTextView.setText(String.format("%.2f", price));
        setCategoryCircle(categoryImageView);

        return root;
    }

    private void setCategoryCircle(ImageView categoryImageView){
        if(category.equals("purple")){
            categoryImageView.setImageResource(R.drawable.purple_small_circle);
        }else if(category.equals("yellow")){
            categoryImageView.setImageResource(R.drawable.yellow_small_circle);
        }else if(category.equals("green")){
            categoryImageView.setImageResource(R.drawable.green_small_circle);
        }else if(category.equals("orange")){
            categoryImageView.setImageResource(R.drawable.orange_small_circle);
        }else if(category.equals("blue")){
            categoryImageView.setImageResource(R.drawable.blue_small_circle);
        }else if(category.equals("null")){
            categoryImageView.setImageResource(R.drawable.gray_small_circle);
        }
    }
}
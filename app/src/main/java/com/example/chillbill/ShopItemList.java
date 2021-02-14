package com.example.chillbill;

import android.graphics.Paint;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;


public class ShopItemList extends Fragment {


    private String name;
    private Boolean isEven;

    public ShopItemList() {
        // Required empty public constructor
    }


    public static ShopItemList newInstance(String name,boolean isEven) {
        ShopItemList fragment = new ShopItemList();
        Bundle args = new Bundle();
        args.putString("NameRecipeItem",name);
        args.putBoolean("EvenRecipeItem",isEven);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            name = getArguments().getString("NameRecipeItem");
            isEven = getArguments().getBoolean("EvenRecipeItem");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_shop_item_list, container, false);

        TextView nameTextView = root.findViewById(R.id.table_element_name);
        CheckBox checkBoxView = root.findViewById(R.id.checkBox);
        ConstraintLayout constraintLayout= root.findViewById(R.id.table_elment_container);
        nameTextView.setText(name);

        if (isEven) {
            constraintLayout.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
        } else {
            constraintLayout.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.very_light_grey));
        }

        if(checkBoxView.isChecked()){
            nameTextView.setPaintFlags(nameTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }else{
            nameTextView.setPaintFlags(nameTextView.getPaintFlags() &(~ Paint.STRIKE_THRU_TEXT_FLAG));
        }

        checkBoxView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    nameTextView.setPaintFlags(nameTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }else{
                    nameTextView.setPaintFlags(nameTextView.getPaintFlags() &(~ Paint.STRIKE_THRU_TEXT_FLAG));

                }
            }
        });



        return root;
    }
}
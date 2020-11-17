package com.example.chillbill;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FitItem#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FitItem extends Fragment {


    private String title;
    private String text;
    int imageId;

    public FitItem() {
        // Required empty public constructor
    }


    public static FitItem newInstance(String title, String text, int imageId) {
        FitItem fragment = new FitItem();
        Bundle args = new Bundle();
        args.putString("titleFit", title);
        args.putString("textFit", text);
        args.putInt("imageId",imageId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString("titleFit");
            text = getArguments().getString("textFit");
            imageId = getArguments().getInt("imageId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_fit_item, container, false);

        TextView titleTextView = rootView.findViewById(R.id.fit_title);
        TextView textTextView = rootView.findViewById(R.id.fit_text);

        titleTextView.setText(title);
        textTextView.setText(text);

        ImageView imageView = rootView.findViewById(R.id.fit_image);
        imageView.setImageResource(imageId);

        return rootView;
    }
}
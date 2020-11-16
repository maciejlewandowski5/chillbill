package com.example.chillbill;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryItem#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryItem extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ProgressBar purple;
    ProgressBar yellow;
    ProgressBar green;
    ProgressBar orange;
    ProgressBar blue;

    public HistoryItem() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HistoryItem.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryItem newInstance(float purpleCategory,float yellowCategory,float greenCategory,float orangeCategory,float blueCategory) {
        HistoryItem fragment = new HistoryItem();
        Bundle args = new Bundle();


        args.putFloat("purpleFloat", purpleCategory);
        args.putFloat("yellowFloat", yellowCategory);
        args.putFloat("greenFloat", greenCategory);
        args.putFloat("orangeFloat", orangeCategory);
        args.putFloat("blueFloat", blueCategory);


        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        float sum =0

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
           sum =  getArguments().getFloat("purpleFloat")+getFloat("purpleFloat")+getFloat("purpleFloat")+getFloat("purpleFloat")+getFloat("purpleFloat");
        }
        purple = getView().findViewById(R.id.progressBar5);
        yellow = getView().findViewById(R.id.progressBar4);
        green = getView().findViewById(R.id.progressBar2);
        orange = getView().findViewById(R.id.progressBar3);
        blue = getView().findViewById(R.id.progressBar7);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history_item, container, false);
    }
}
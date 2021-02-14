package com.example.chillbill;

import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.chillbill.model.Category;

import java.util.Comparator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FilterButtons#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FilterButtons extends Fragment {


    int[] colors;
    Button[] filterButtons;
    boolean[] activeFilters; //TODO: smarter filter, create way to show few categories at the same time



    private Sorter sorter;

    public FilterButtons() {
        // Required empty public constructor
    }

    public void setSorter(Sorter sorter) {
        this.sorter = sorter;
    }



    public static FilterButtons newInstance() {
        FilterButtons fragment = new FilterButtons();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            sorter = null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_filter_buttons, container, false);

        activeFilters = new boolean[]{false, false, false, false, false};
        filterButtons = new Button[]{
                root.findViewById(R.id.orange_category_button), root.findViewById(R.id.purple_category_button),
                root.findViewById(R.id.yellow_category_button),
                root.findViewById(R.id.green_categoty_button),
                root.findViewById(R.id.blue_category_button)};

        colors = new int[]{ResourcesCompat.getColor(getResources(), R.color.orange, null),
                ResourcesCompat.getColor(getResources(), R.color.purple, null),
                ResourcesCompat.getColor(getResources(), R.color.yellow, null),
                ResourcesCompat.getColor(getResources(), R.color.green, null),
                ResourcesCompat.getColor(getResources(), R.color.blue, null)};





        return root;
    }
    public void sortYellowHistory(View view) {
        sortColorHistory(view, R.color.yellow, Category.YELLOW, 2);
    }

    public void sortPurpleHistory(View view) {
        sortColorHistory(view, R.color.purple, Category.PURPLE, 1);
    }

    public void sortGreenHistory(View view) {
        sortColorHistory(view, R.color.green, Category.GREEN, 3);
    }

    public void sortOrangeHistory(View view) {
        sortColorHistory(view, R.color.orange, Category.ORANGE, 0);
    }

    public void sortBlueHistory(View view) {
        sortColorHistory(view, R.color.blue, Category.BLUE, 4);
    }


    public void setColorsToWhite(int buttonNumber) {
        for (int i = 0; i < filterButtons.length; i++) {
            if (i != buttonNumber) {
                filterButtons[i].setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
                filterButtons[i].setTextColor(colors[i]);
            }
        }
    }


    public void sortColorHistory(View view, int colorId, Category category, int activeFilterId) {
        if (!activeFilters[activeFilterId]) {
            sorter.sortAndDisplay(category);
            Button button = (Button) view;
            button.setBackgroundColor(ResourcesCompat.getColor(getResources(), colorId, null));
            button.setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
            setColorsToWhite(activeFilterId);
            activeFilters[activeFilterId] = true;
        } else {
            sorter.sortByDate();
            Button button = (Button) view;
            button.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
            button.setTextColor(ResourcesCompat.getColor(getResources(), colorId, null));
            setColorsToWhite(activeFilterId);
            activeFilters[activeFilterId] = false;
        }
    }
    public void setAllToWhite(){
        setColorsToWhite(0);
        filterButtons[0].setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
        filterButtons[0].setTextColor(colors[0]);
    }

    public void refreshFilters(){
        activeFilters = new boolean[]{false, false, false, false, false};
    }
    public interface Sorter{
        public void sortByDate();
        public void sortAndDisplay(Category category);

    }





}
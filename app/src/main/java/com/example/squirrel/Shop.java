package com.example.squirrel;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class Shop extends Fragment {

    StandartNote standartNote = new StandartNote();

    public Shop() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.standart_note,
                container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void getPoints(){
        
    }

}

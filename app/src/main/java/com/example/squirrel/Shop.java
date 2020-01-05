package com.example.squirrel;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class Shop extends Fragment {

    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    private Cursor userCursor;
    String[] tempArrBool;
    boolean[] booleans;
    String[] tempArr;

    public Shop() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.shop_fragment,
                container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getPoints();
    }

    public int getBtnID(){
        Bundle arguments = this.getArguments();
        assert arguments != null;
        return arguments.getInt("buttonID");
    }

    public String getBtnName(){
        Bundle arguments = this.getArguments();
        assert arguments != null;
        return arguments.getString("button name");
    }


    private void getPoints(){
        /* БД ************************ */
        mDBHelper = new DatabaseHelper(getActivity());
        mDBHelper.openDataBase();
        try {
            mDBHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }
        final TextView name = getActivity().findViewById(R.id.editNameShopNote);

        mDb = mDBHelper.getReadableDatabase();

        userCursor =  mDb.rawQuery("Select * from Notes", null);
        userCursor.moveToPosition(getBtnID());
        name.setText(getBtnName());

        final String temp = userCursor.getString(7);
        String tempBool = userCursor.getString(6);
        tempArr = temp.split("\r\n|\r|\n");
        tempArrBool = tempBool.split("\r\n|\r|\n");
        booleans = new boolean[tempArrBool.length];

        for (int i = 0; i < tempArrBool.length; i++) {
            booleans[i] = Boolean.valueOf(tempArrBool[i]);
        }



        for(int i = 0; i < tempArr.length; i++){
            addCheck(booleans[i], tempArr[i]);
        }

    }

    private void addCheck(boolean state, final String name){

        LinearLayout linear = getActivity().findViewById(R.id.shopScroll);
        View view = getLayoutInflater().inflate(R.layout.item_check, null);
        final CheckBox check = view.findViewById(R.id.checkBox);
        check.setChecked(state);
        check.setText(name);

        check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int index = Arrays.asList(tempArr).indexOf(check.getText().toString());
                booleans[index] = isChecked;
                StringBuilder sendBool = new StringBuilder();
                for (int j = 0; j < booleans.length; j++) {
                    sendBool.append(String.valueOf(booleans[j]) + "\n");
                }
                updDatabase("Notes", name,sendBool.toString());
            }
        });
        linear.addView(check);
    }

    private void updDatabase(String databaseName, String name, String booleans){
        mDb = mDBHelper.getWritableDatabase();
        //код сохранения в бд
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("isChecked", booleans);
        Date currentDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy",
                Locale.getDefault());

        cv.put("date", dateFormat.format(currentDate));

        //обновление базы данных
        mDb.update(databaseName, cv, "id =" + (getBtnID() + 1), null);
    }


}

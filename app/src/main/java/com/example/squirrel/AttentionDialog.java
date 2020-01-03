package com.example.squirrel;


import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 */
public class AttentionDialog extends Fragment implements View.OnClickListener {

    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;

    public AttentionDialog() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attention_dialog, container,
                false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        mDBHelper = new DatabaseHelper(getActivity());
        mDBHelper.openDataBase();
        try {
            mDBHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.delBtn:
                delete(getBtnID());
                break;
        }
    }

    private int getBtnID(){
        Bundle arguments = this.getArguments();
        assert arguments != null;
        return arguments.getInt("buttonID");
    }

    //удаление проекта из активити и удаление его из бд
    public void delete(int selected){
        if(MainActivity.id >= 0) {

            mDb = mDBHelper.getWritableDatabase();
            mDb.delete("Notes", "id=" + selected, null);
            MainActivity mainActivity = new MainActivity();
            mainActivity.dataProjects.remove(selected - 1);
            LinearLayout linear = mainActivity.findViewById(R.id.linear);
            linear.removeViewAt(selected - 1);
            ContentValues cv = new ContentValues();
            for(int i = 0; i < mainActivity.dataProjects.size(); i++){
                cv.put("id", String.valueOf(i + 1));
                mDb.update("Notes", cv, "id =" + (i + 1), null);
            }

            if(MainActivity.id - 1 >=0){
                MainActivity.id--;
            } else {
                MainActivity.id = 0;
            }
        }

    }
}

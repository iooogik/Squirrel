package iooogik.app.modelling.tests;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import iooogik.app.modelling.Database;
import iooogik.app.modelling.MainActivity;
import iooogik.app.modelling.R;

public class Tests extends Fragment implements View.OnClickListener{

    static View VIEW;

    //Переменная для работы с БД
    private Database mDBHelper;

    private Cursor userCursor;
    static TestsAdapter TEST_ADAPTER;
    static List<TestTheme> TEST_ITEMS = new ArrayList<>();


    public Tests() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        VIEW = inflater.inflate(R.layout.fragment_test, container, false);

        mDBHelper = new Database(getContext());
        mDBHelper.openDataBase();
        mDBHelper.updateDataBase();

        loadAndSetThemes();
        return VIEW;
    }

    private void loadAndSetThemes(){
        SQLiteDatabase mDb = mDBHelper.getReadableDatabase();
        userCursor = mDb.rawQuery("Select * from Tests", null);
        userCursor.moveToFirst();
        String name, desc;

        //необходимо очистить содержимое, чтобы при старте активити не было повторяющихся элементов
        try {
            TEST_ITEMS.clear();
        } catch (Exception e){
            Log.i("Notes", String.valueOf(e));
        }

        while (!userCursor.isAfterLast()) {
            //получение имени
            name = String.valueOf(userCursor.getString(1));
            //получение описания
            desc = String.valueOf(userCursor.getString(2));
            //получение количества правильных и неправильных ответов
            float rightScore = 0, wrongScore = 0;

            rightScore = Float.valueOf(userCursor.
                    getString(userCursor.getColumnIndex("trueAnswers")));
            wrongScore = Float.valueOf(userCursor.
                    getString(userCursor.getColumnIndex("wrongAnswers")));

            int isPassedDB = userCursor.
                    getInt(userCursor.getColumnIndex("isPassed"));
            boolean isPassed = false;
            isPassed = isPassedDB == 1;

            //добавляем тест в recyclerView
            TEST_ITEMS.add(new TestTheme(name, desc, rightScore, wrongScore, isPassed));
            userCursor.moveToNext();

        }
        userCursor.close();
        RecyclerView recyclerView = VIEW.findViewById(R.id.test_items);
        TEST_ADAPTER = new TestsAdapter(getContext(), TEST_ITEMS);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(TEST_ADAPTER);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.back){
            Intent main = new Intent(getContext(), MainActivity.class);
            startActivity(main);
        }
    }
}

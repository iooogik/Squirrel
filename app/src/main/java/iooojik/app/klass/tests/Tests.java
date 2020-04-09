package iooojik.app.klass.tests;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import iooojik.app.klass.Database;
import iooojik.app.klass.MainActivity;
import iooojik.app.klass.R;

public class Tests extends Fragment implements View.OnClickListener{

    static View VIEW;

    //Переменная для работы с БД
    private Database mDBHelper;

    private Cursor userCursor;
    static TestsAdapter TEST_ADAPTER;
    static List<TestTheme> TEST_ITEMS;


    public Tests() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        VIEW = inflater.inflate(R.layout.fragment_test, container, false);

        FloatingActionButton floatingActionButton = getActivity().findViewById(R.id.fab);
        floatingActionButton.hide();

        TEST_ITEMS = new ArrayList<>();
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

            rightScore = Float.parseFloat(userCursor.
                    getString(userCursor.getColumnIndex("trueAnswers")));
            wrongScore = Float.parseFloat(userCursor.
                    getString(userCursor.getColumnIndex("wrongAnswers")));

            int isPassedDB = userCursor.
                    getInt(userCursor.getColumnIndex("isPassed"));
            boolean isPassed = false;
            isPassed = isPassedDB == 1;
            int id = userCursor.getInt(userCursor.getColumnIndex("_id"));
            //добавляем тест в recyclerView
            TEST_ITEMS.add(new TestTheme(name, desc, rightScore, wrongScore, isPassed, id));
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

package iooogik.app.modelling;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class Test extends Fragment implements View.OnClickListener{

    static View VIEW;

    //Переменная для работы с БД
    private Database mDBHelper;
    private SQLiteDatabase mDb;

    Bundle bundle = new Bundle();
    Cursor userCursor;
    static TestsAdapter TEST_ADAPTER;
    static List<TestTheme> TEST_ITEMS = new ArrayList<>();


    public Test() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        VIEW = inflater.inflate(R.layout.fragment_test, container, false);
        FloatingActionButton back = VIEW.findViewById(R.id.back);
        back.setOnClickListener(this);


        mDBHelper = new Database(getContext());
        mDBHelper.openDataBase();
        mDBHelper.updateDataBase();

        loadAndSetThemes();
        return VIEW;
    }

    private void loadAndSetThemes(){
        mDb = mDBHelper.getReadableDatabase();
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
            Toast.makeText(getContext(), String.valueOf(isPassed), Toast.LENGTH_SHORT).show();

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

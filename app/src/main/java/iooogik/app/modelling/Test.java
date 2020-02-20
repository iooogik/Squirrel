package iooogik.app.modelling;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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

    static View view;
    private ArrayList<String> testTitles;

    //Переменная для работы с БД
    private Database mDBHelper;
    private SQLiteDatabase mDb;

    Bundle bundle = new Bundle();
    Cursor userCursor;


    public Test() {
        testTitles = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_test, container, false);
        FloatingActionButton back = view.findViewById(R.id.back);
        back.setOnClickListener(this);


        mDBHelper = new Database(getContext());
        mDBHelper.openDataBase();
        mDBHelper.updateDataBase();
        loadAndSetThemes();
        return view;
    }

    private void loadAndSetThemes(){
        mDb = mDBHelper.getReadableDatabase();
        userCursor = mDb.rawQuery("Select * from Tests", null);
        userCursor.moveToFirst();
        String name, desc;
        int identificator = 0;
        while (!userCursor.isAfterLast()) {

            name = String.valueOf(userCursor.getString(1)); //колонки считаются с 0
            testTitles.add(name);
            desc = String.valueOf(userCursor.getString(2));

            identificator = testTitles.size() - 1;

            if(name != null)
                addToScroll(name, desc, identificator);
            userCursor.moveToNext();
        }
    }

    private void addToScroll(String name, String desc, int identificator){
        View view1 = getLayoutInflater().inflate(R.layout.item_test, null, false);

        //заголовок
        TextView nameNote = view1.findViewById(R.id.name);
        nameNote.setText(name);
        //описание
        TextView description = view1.findViewById(R.id.description);
        description.setText(desc);

        float rightScore = 0, wrongScore = 0;

        mDb = mDBHelper.getReadableDatabase();
        userCursor = mDb.rawQuery("Select * from Tests", null);
        userCursor.moveToPosition(identificator);

        rightScore = Float.valueOf(userCursor.
                getString(userCursor.getColumnIndex("trueAnswers")));
        wrongScore = Float.valueOf(userCursor.
                getString(userCursor.getColumnIndex("wrongAnswers")));

        if(rightScore != 0 || wrongScore != 0){
            //находим диаграмму на активити
            PieChart pieChart = view1.findViewById(R.id.chart);
            pieChart.setVisibility(View.VISIBLE);
            //добавляем диаграмму
            setDiagram(view1, rightScore, wrongScore);
            //процент правильных ответов
            TextView textView = view1.findViewById(R.id.result);
            textView.setText("Тест был на пройден на " + (rightScore/wrongScore)* 100 + "%");

        } else {
            //обработка нажатия на view
            view1.setOnClickListener(v -> {
                bundle.putString("button name", name);
                bundle.putInt("button ID", identificator);
                Questions questions = new Questions();
                showFragment(questions);
            });
        }

        //установка на активити
        LinearLayout linearLayout = view.findViewById(R.id.scrollTestThemes);
        linearLayout.addView(view1);
    }

    private void setDiagram(View view1, float rightScore, float wrongScore){
        //находим диаграмму на активити
        PieChart pieChart = view1.findViewById(R.id.chart);
        //добавляем данные в диаграмму
        List<Float> score = new ArrayList<>();
        score.add(rightScore);
        score.add(wrongScore);
        //преобразуем в понятные для диаграммы данные
        List<PieEntry> entries = new ArrayList<PieEntry>();
        for (int i = 0; i < score.size(); i++) {
            entries.add(new PieEntry(score.get(i), i));
        }

        PieDataSet pieDataSet = new PieDataSet(entries, "");
        pieDataSet.setSliceSpace(3);
        //устанавливаем цвета
        List<Integer> colors = new ArrayList<Integer>();
        colors.add(Color.GREEN);
        colors.add(Color.RED);

        pieDataSet.setColors(colors);
        pieDataSet.setSelectionShift(15);

        PieData pieData = new PieData(pieDataSet);
        //анимация
        pieChart.animateY(1200);

        Description description = new Description();
        description.setText("");
        pieChart.setDescription(description);

        pieChart.getLegend().setFormSize(0f);
        pieData.setValueTextSize(0f);

        pieChart.setHoleRadius(40);
        pieChart.setUsePercentValues(true);
        pieChart.setData(pieData);
    }

    private void showFragment(Fragment fragment){
        FrameLayout frameLayout = view.findViewById(R.id.test_frame);
        frameLayout.setVisibility(View.VISIBLE);

        fragment.setArguments(bundle);

        FragmentManager fm = getFragmentManager();
        assert fm != null;
        FragmentTransaction ft = fm.beginTransaction();

        if (fragment != null) {
            ft.remove(fragment).commitAllowingStateLoss();
        }

        FragmentTransaction addTransaction = fm.beginTransaction();
        addTransaction.setCustomAnimations
                (R.anim.nav_default_enter_anim, R.anim.nav_default_exit_anim);
        addTransaction.addToBackStack(null);
        assert fragment != null;
        addTransaction.add(R.id.test_frame, fragment,
                "testFrame").commitAllowingStateLoss();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.back){
            Intent main = new Intent(getContext(), MainActivity.class);
            startActivity(main);
        }
    }
}

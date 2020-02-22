package iooogik.app.modelling;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

public class TestsAdapter extends RecyclerView.Adapter<TestsAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<TestTheme> tests;
    Bundle bundle = new Bundle();

    TestsAdapter(Context context, List<TestTheme> tests){
        this.tests = tests;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_test, parent, false);//поиск элемента списка
        return new TestsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TestTheme testTheme = tests.get(position);
        holder.name.setText(testTheme.getName());
        holder.desc.setText(testTheme.getDesc());
        float rightScore = testTheme.getRightAnswers(), wrongScore = testTheme.getWrongAnswers();

        if(testTheme.isPassed()){
            //находим диаграмму на активити
            holder.pieChart.setVisibility(View.VISIBLE);

            //добавляем данные в диаграмму
            List<Float> score = new ArrayList<>();
            score.add((rightScore/wrongScore)* 100);
            score.add(100 - (rightScore/wrongScore)* 100);
            //преобразуем в понятные для диаграммы данные
            List<PieEntry> entries = new ArrayList<PieEntry>();
            for (int i = 0; i < score.size(); i++) {
                entries.add(new PieEntry(score.get(i), i));
            }

            PieDataSet pieDataSet = new PieDataSet(entries, "");
            pieDataSet.setSliceSpace(7);
            //устанавливаем цвета
            List<Integer> colors = new ArrayList<Integer>();
            int green = Color.parseColor("#7CFC00");
            int red = Color.parseColor("#FF0000");
            colors.add(green);
            colors.add(red);

            pieDataSet.setColors(colors);
            pieDataSet.setSelectionShift(15);

            PieData pieData = new PieData(pieDataSet);
            //анимация
            holder.pieChart.animateY(600);
            //убираем надписи
            Description description = new Description();
            description.setText("");
            holder.pieChart.setDescription(description);

            holder.pieChart.getLegend().setFormSize(0f);
            pieData.setValueTextSize(0f);

            holder.pieChart.setHoleRadius(25);
            holder.pieChart.setData(pieData);

            //процент правильных ответов
            TextView textView = holder.result.findViewById(R.id.result);
            textView.setText
                    ("Тест был на пройден на " +  Math.round((rightScore/wrongScore)* 100) + "%");

        } else {
            //обработка нажатия на view
            holder.frameLayout.setOnClickListener(v -> {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                FrameLayout quest_frame = activity.findViewById(R.id.test_frame);
                quest_frame.setVisibility(View.VISIBLE);

                bundle.putString("button name", testTheme.getName());
                bundle.putInt("button ID", position);

                Questions questions = new Questions();
                questions.setArguments(bundle);

                activity.getSupportFragmentManager().beginTransaction()

                        .setCustomAnimations(R.anim.nav_default_enter_anim,
                                R.anim.nav_default_exit_anim).

                        replace(R.id.test_frame, questions,
                                "testFrame").commitAllowingStateLoss();

            });
        }
    }

    @Override
    public int getItemCount() {
        return tests.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder{
        final TextView name;
        final TextView desc;
        final TextView result;
        final PieChart pieChart;
        final FrameLayout frameLayout;

        ViewHolder(View view){
            super(view);
            name = view.findViewById(R.id.name);
            desc = view.findViewById(R.id.description);
            result = view.findViewById(R.id.result);
            pieChart = view.findViewById(R.id.chart);
            frameLayout = view.findViewById(R.id.frame);
        }
    }

}

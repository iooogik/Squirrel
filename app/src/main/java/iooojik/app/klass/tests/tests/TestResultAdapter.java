package iooojik.app.klass.tests.tests;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

import iooojik.app.klass.R;
import iooojik.app.klass.models.passed_test_result.PassedTest;

public class TestResultAdapter extends RecyclerView.Adapter<TestResultAdapter.ViewHolder>{

    private List<PassedTest> passedTests;
    private LayoutInflater inflater;

    TestResultAdapter(List<PassedTest> passedTests, Context context) {
        this.passedTests = passedTests;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_view_test_result_item, parent, false);//поиск элемента списка
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PassedTest passedTest = passedTests.get(position);
        holder.name.setText(String.format("Название теста: %s", passedTest.getTestName()));
        List<Float> score = new ArrayList<>();
        float rightScore = Float.valueOf(passedTest.getResult());
        float wrongScore = 100.0f;
        score.add((rightScore/wrongScore)* 100);
        score.add(100 - (rightScore/wrongScore)* 100);

        //преобразуем в понятные для диаграммы данные
        List<PieEntry> entries = new ArrayList<>();
        for (int i = 0; i < score.size(); i++) entries.add(new PieEntry(score.get(i), i));
        PieDataSet pieDataSet = new PieDataSet(entries, "");

        //устанавливаем цвета
        List<Integer> colors = new ArrayList<>();
        int green = Color.parseColor("#56CF54");
        int red = Color.parseColor("#FF5252");
        colors.add(green);
        colors.add(red);
        pieDataSet.setColors(colors);

        PieData pieData = new PieData(pieDataSet);
        //анимация
        holder.pieChart.animateY(500);
        //убираем надписи
        Description description = new Description();
        description.setText("");
        holder.pieChart.setDescription(description);

        holder.pieChart.getLegend().setFormSize(0f);
        pieData.setValueTextSize(0f);

        holder.pieChart.setTransparentCircleRadius(0);

        holder.pieChart.setHoleRadius(0);
        holder.pieChart.setData(pieData);

        //процент правильных ответов
        holder.result.setText(String.format("Тест был пройден на %s из 100", String.valueOf(Math.round((rightScore / wrongScore) * 100))));
    }

    @Override
    public int getItemCount() {
        return passedTests.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView name, result;
        PieChart pieChart;


        ViewHolder(View view){
            super(view);
            name = view.findViewById(R.id.name);
            pieChart = view.findViewById(R.id.chart);
            result = view.findViewById(R.id.result);
        }
    }

}

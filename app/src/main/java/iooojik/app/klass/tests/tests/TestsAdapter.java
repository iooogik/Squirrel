package iooojik.app.klass.tests.tests;

import android.annotation.SuppressLint;
import android.content.Context;
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

import iooojik.app.klass.R;
import iooojik.app.klass.tests.questions.Questions;

public class TestsAdapter extends RecyclerView.Adapter<TestsAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<TestObject> tests;
    private Bundle bundle = new Bundle();

    TestsAdapter(Context context, List<TestObject> tests){
        this.tests = tests;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_view_item_test, parent, false);//поиск элемента списка
        return new ViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TestObject testObject = tests.get(position);
        holder.name.setText(testObject.getName());
        holder.desc.setText(testObject.getDesc());


        //если тест пройден
        /*
        if(testObject.isPassed()){
            float rightScore = testObject.getUserScore(), wrongScore = testObject.getWrongAnswers();

            //находим диаграмму на активити
            holder.pieChart.setVisibility(View.VISIBLE);

            //добавляем данные в диаграмму
            List<Float> score = new ArrayList<>();
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
            TextView textView = holder.result.findViewById(R.id.result);
            textView.setText(String.format("Тест пройден на %d%%",
                    Math.round((rightScore / wrongScore) * 100)));

        } else {

         */
        //обработка нажатия на view
        if (!testObject.isPassed()) {
            holder.frameLayout.setOnClickListener(v -> {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                FrameLayout quest_frame = activity.findViewById(R.id.test_frame);
                quest_frame.setVisibility(View.VISIBLE);

                bundle.putInt("test id", testObject.getId());

                Questions questions = new Questions();
                questions.setArguments(bundle);

                activity.getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.nav_default_enter_anim,
                                R.anim.nav_default_exit_anim).
                        replace(R.id.test_frame, questions, "testFrame").commitAllowingStateLoss();

            });
        }
    }

    @Override
    public int getItemCount() {
        return tests.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder{
        final TextView name;
        final TextView desc;
        final FrameLayout frameLayout;

        ViewHolder(View view){
            super(view);
            name = view.findViewById(R.id.name);
            desc = view.findViewById(R.id.description);
            frameLayout = view.findViewById(R.id.frame);
        }
    }

}

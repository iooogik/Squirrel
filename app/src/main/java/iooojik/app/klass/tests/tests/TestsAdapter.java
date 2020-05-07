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

        //обработка нажатия на view
        if (!testObject.isPassed()) {
            holder.itemView.setOnClickListener(v -> {
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
        View itemView;

        ViewHolder(View view){
            super(view);
            name = view.findViewById(R.id.name);
            desc = view.findViewById(R.id.description);
            itemView = view;
        }
    }

}

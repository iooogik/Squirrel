package iooojik.app.klass.tests.questions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import iooojik.app.klass.R;


public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.ViewHolder> {

    private LayoutInflater inflater;

    QuestionsAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_view_item_question, parent, false);//поиск элемента
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuestionObject object = Questions.questionObjects.get(position);
        holder.question.setText(object.getQuestion());

        //первый ответ на вопрос
        holder.firstAnswer.setText(object.getAnswers().get(0));
        holder.firstAnswer.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked && object.getTrueAnswer().contains(buttonView.getText().toString())){
                Questions.userScore+= Questions.scorePerAnswer;

            }else if (!isChecked && object.getTrueAnswer().contains(buttonView.getText().toString())){
                Questions.userScore-=Questions.scorePerAnswer;
            }
            object.setSelectedAnswer(buttonView.getText().toString());
        });

        //второй ответ на вопрос
        holder.secondAnswer.setText(object.getAnswers().get(1));
        holder.secondAnswer.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked && object.getTrueAnswer().contains(buttonView.getText().toString())){
                Questions.userScore+= Questions.scorePerAnswer;
            }else if (!isChecked && object.getTrueAnswer().contains(buttonView.getText().toString())){
                Questions.userScore-=Questions.scorePerAnswer;
            }
            object.setSelectedAnswer(buttonView.getText().toString());
        });

        //третий ответ на вопрос
        holder.thirdAnswer.setText(object.getAnswers().get(2));
        holder.thirdAnswer.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked && object.getTrueAnswer().contains(buttonView.getText().toString())){
                Questions.userScore+= Questions.scorePerAnswer;
            }else if (!isChecked && object.getTrueAnswer().contains(buttonView.getText().toString())){
                Questions.userScore-=Questions.scorePerAnswer;
            }
            object.setSelectedAnswer(buttonView.getText().toString());
        });

        //четвёртый ответ на вопрос
        holder.fourthAnswer.setText(object.getAnswers().get(3));
        holder.fourthAnswer.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked && object.getTrueAnswer().contains(buttonView.getText().toString())){
                Questions.userScore+= Questions.scorePerAnswer;
            }else if (!isChecked && object.getTrueAnswer().contains(buttonView.getText().toString())){
                Questions.userScore-=Questions.scorePerAnswer;
            }
            object.setSelectedAnswer(buttonView.getText().toString());
        });
        Questions.recyclerViewItems.add(holder);
    }

    @Override
    public int getItemCount() {
        return Questions.questionObjects.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView question;
        RadioButton firstAnswer;
        RadioButton secondAnswer;
        RadioButton thirdAnswer;
        RadioButton fourthAnswer;

        ViewHolder(View view){
            super(view);
            question = view.findViewById(R.id.task);
            firstAnswer = view.findViewById(R.id.radioButton1);
            secondAnswer = view.findViewById(R.id.radioButton2);
            thirdAnswer = view.findViewById(R.id.radioButton3);
            fourthAnswer = view.findViewById(R.id.radioButton4);
        }
    }
}

package iooojik.app.klass.tests.questions;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import iooojik.app.klass.R;


public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<QuestionObject> questionObjects;
    private Context context;

    QuestionsAdapter(Context context, List<QuestionObject> questionObjects) {
        this.inflater = LayoutInflater.from(context);
        this.questionObjects = questionObjects;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_view_item_question, parent, false);//поиск элемента
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuestionObject object = questionObjects.get(position);
        holder.question.setText(object.getQuestion());

        holder.difficulties.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                Questions.difficultiesCount++;
            }else {
                Questions.difficultiesCount--;
            }
        });

        //первый ответ на вопрос
        holder.firstAnswer.setText(object.getAnswers().get(0));
        holder.firstAnswer.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked && object.getTrueAnswer().contains(buttonView.getText().toString())){
                Questions.userScore+=object.getScore();

            }else if (!isChecked && object.getTrueAnswer().contains(buttonView.getText().toString())){
                Questions.userScore-=object.getScore();
            }
            object.setSelectedAnswer(buttonView.getText().toString());
        });

        //второй ответ на вопрос
        holder.secondAnswer.setText(object.getAnswers().get(1));
        holder.secondAnswer.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked && object.getTrueAnswer().contains(buttonView.getText().toString())){
                Questions.userScore+=object.getScore();
            }else if (!isChecked && object.getTrueAnswer().contains(buttonView.getText().toString())){
                Questions.userScore-=object.getScore();
            }
            object.setSelectedAnswer(buttonView.getText().toString());
        });

        //третий ответ на вопрос
        holder.thirdAnswer.setText(object.getAnswers().get(2));
        holder.thirdAnswer.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked && object.getTrueAnswer().contains(buttonView.getText().toString())){
                Questions.userScore+=object.getScore();
            }else if (!isChecked && object.getTrueAnswer().contains(buttonView.getText().toString())){
                Questions.userScore-=object.getScore();
            }
            object.setSelectedAnswer(buttonView.getText().toString());
        });

        //четвёртый ответ на вопрос
        holder.fourthAnswer.setText(object.getAnswers().get(3));
        holder.fourthAnswer.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked && object.getTrueAnswer().contains(buttonView.getText().toString())){
                Questions.userScore+=object.getScore();
            }else if (!isChecked && object.getTrueAnswer().contains(buttonView.getText().toString())){
                Questions.userScore-=object.getScore();
            }
            object.setSelectedAnswer(buttonView.getText().toString());
        });


        //получаем ссылку на файл и если там картинка, то показываем её,
        // если файл то появляется кнопка "скачать"

        String fileURL = object.getFileURL();
        Log.e("rt", fileURL);
        if (!fileURL.trim().isEmpty()) {
            int lastPointID = fileURL.lastIndexOf('.');
            StringBuilder extension = new StringBuilder();
            for (int i = lastPointID + 1; i < fileURL.length(); i++) {
                extension.append(fileURL.charAt(i));
            }
            Log.e("tttt", extension.toString());
            switch (extension.toString()){
                case "jpg":
                case "jpeg":
                case "png":
                case "tiff":
                    holder.urlImage.setVisibility(View.VISIBLE);
                    Picasso.with(context).load(fileURL).resize(150, 150).into(holder.urlImage);
                    break;
                default:

                    //показываем кнопку для скачивания файла
                    holder.downloadButton.setVisibility(View.VISIBLE);
                    holder.downloadButton.setOnClickListener(v -> {
                        StringBuilder fileName = new StringBuilder();
                        int pointIndex = fileURL.lastIndexOf('/');

                        for (int i = pointIndex + 1; i < fileURL.length(); i++) {
                            fileName.append(fileURL.charAt(i));
                        }

                        String path = Environment.getExternalStorageDirectory() + "/Download/" + fileName.toString();

                        new LoadFile(fileURL, new File(path)).start();
                    });
                    break;
            }
        }

        Questions.recyclerViewItems.add(holder);
    }
    private void onDownloadComplete(boolean success) {
        if (success)
            Toast.makeText(context, "Файл находится в папке Download", Toast.LENGTH_LONG).show();
        else Toast.makeText(context, "Что-то пошло не так", Toast.LENGTH_LONG).show();
        Log.i("***", "************** " + success);
    }

    private class LoadFile extends Thread {
        private final String src;
        private final File dest;

        LoadFile(String src, File dest) {
            this.src = src;
            this.dest = dest;
        }

        @Override
        public void run() {
            try {
                FileUtils.copyURLToFile(new URL(src), dest);
                onDownloadComplete(true);
            } catch (IOException e) {
                e.printStackTrace();
                onDownloadComplete(false);
            }
        }
    }

    @Override
    public int getItemCount() {
        return questionObjects.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView question;
        RadioButton firstAnswer;
        RadioButton secondAnswer;
        RadioButton thirdAnswer;
        RadioButton fourthAnswer;
        CheckBox difficulties;
        ImageView urlImage;
        Button downloadButton;

        ViewHolder(View view){
            super(view);
            difficulties = view.findViewById(R.id.checkBox);
            question = view.findViewById(R.id.task);
            firstAnswer = view.findViewById(R.id.radioButton1);
            secondAnswer = view.findViewById(R.id.radioButton2);
            thirdAnswer = view.findViewById(R.id.radioButton3);
            fourthAnswer = view.findViewById(R.id.radioButton4);
            urlImage = view.findViewById(R.id.image);
            downloadButton = view.findViewById(R.id.downloadAttachment);
        }
    }
}

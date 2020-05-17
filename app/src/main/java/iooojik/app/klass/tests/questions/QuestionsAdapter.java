package iooojik.app.klass.tests.questions;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import iooojik.app.klass.R;


public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.ViewHolder>{

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

    @SuppressLint({"SetJavaScriptEnabled", "ClickableViewAccessibility", "InflateParams" })
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

        if (!fileURL.trim().isEmpty()) {
            int lastPointID = fileURL.lastIndexOf('.');
            StringBuilder extension = new StringBuilder();
            for (int i = lastPointID + 1; i < fileURL.length(); i++) {
                extension.append(fileURL.charAt(i));
            }

            switch (extension.toString()){
                case "jpg":
                case "jpeg":
                case "png":
                case "tiff":
                    holder.urlImage.setVisibility(View.VISIBLE);
                    Picasso.with(context).load(fileURL).into(holder.urlImage);
                    break;
                case "mp3":
                case "wav":
                case "aac":
                    View audioView = inflater.inflate(R.layout.open_in_browser, null);
                    /*MediaPlayer mediaPlayer = new MediaPlayer();
                    ImageView playButton = audioView.findViewById(R.id.play);
                    TextView timeAudio = audioView.findViewById(R.id.time);
                    SeekBar seekBar2 = audioView.findViewById(R.id.seekBar);
                    ProgressBar progressBar = audioView.findViewById(R.id.progressBar);
                    AtomicBoolean isLoadedAudio = new AtomicBoolean(false);
                    playButton.setEnabled(false);
                    seekBar2.setMax(100);
                    new Thread(() -> {
                        try {
                            mediaPlayer.setDataSource(fileURL);
                            mediaPlayer.setOnPreparedListener(mp -> {
                                playButton.setEnabled(true);
                                isLoadedAudio.set(true);
                                progressBar.setVisibility(View.INVISIBLE);
                                timeAudio.setText(msToTime(mediaPlayer.getDuration()));
                            });
                            mediaPlayer.prepare();
                        }
                        catch (IOException e) { e.printStackTrace(); }
                    }).start();

                    seekBar2.setOnTouchListener((v, event) -> {
                        mediaPlayer.seekTo((mediaPlayer.getDuration() / 100) * mediaPlayer.getCurrentPosition());
                        timeAudio.setText(msToTime(mediaPlayer.getCurrentPosition()));
                        mediaPlayer.start();
                        return false;
                    });

                    playButton.setOnClickListener(v -> {
                        if (!mediaPlayer.isPlaying()){
                            if (isLoadedAudio.get()){
                                playButton.setImageResource(R.drawable.baseline_pause_circle_outline_24);
                                mediaPlayer.start();
                            }
                        }
                        else {
                            playButton.setImageResource(R.drawable.baseline_play_arrow_24);
                            mediaPlayer.pause();
                        }
                    });

                     */

                    Button openInBrowser = audioView.findViewById(R.id.openInBrowser);
                    openInBrowser.setOnClickListener(v -> {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(fileURL));
                        context.startActivity(browserIntent);
                    });


                    holder.questionLayout.addView(audioView);
                    break;
                case "mp4":
                case "avi":
                case "mov":

                    View videoView = inflater.inflate(R.layout.video_player, null);
                    /*VideoView video = videoView.findViewById(R.id.videoView);

                    TextView time = videoView.findViewById(R.id.time);
                    ImageView play = videoView.findViewById(R.id.play);
                    SeekBar seekBar = videoView.findViewById(R.id.seekBar);
                    AtomicBoolean isLoaded = new AtomicBoolean(false);

                    play.setOnClickListener(v -> {
                        if (!video.isPlaying()) {
                            if (!isLoaded.get()) {

                                ProgressDialog progressDialog = new ProgressDialog(context);
                                progressDialog.setMessage("Загрузка...");
                                progressDialog.setCancelable(false);
                                progressDialog.show();

                                try {
                                    if (!video.isPlaying()) {
                                        video.setVideoPath(fileURL);
                                    }
                                } catch (Exception e) {
                                    Log.e("VIDEO PLAY", String.valueOf(e));
                                }
                                video.requestFocus();
                                video.setOnPreparedListener(mp -> {
                                    progressDialog.dismiss();
                                    play.setImageResource(R.drawable.baseline_pause_circle_outline_24);
                                    video.start();
                                    isLoaded.set(true);
                                });
                            }else {
                                play.setImageResource(R.drawable.baseline_pause_circle_outline_24);
                                video.start();
                            }
                        } else {
                            play.setImageResource(R.drawable.baseline_play_arrow_24);
                            video.pause();
                        }
                    });

                    seekBar.setMax(100);
                    seekBar.setOnTouchListener((v, event) -> {
                        video.seekTo((video.getDuration() / 100) * seekBar.getProgress());
                        time.setText(msToTime(video.getCurrentPosition()));
                        return false;
                    });

                     */

                    //кнопка "открыть в браузере"
                    Button openInBrowser2 = videoView.findViewById(R.id.openInBrowser);
                    openInBrowser2.setOnClickListener(v -> {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(fileURL));
                        context.startActivity(browserIntent);
                    });

                    holder.questionLayout.addView(videoView);
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

    private String msToTime(long ms){
        String timeText = "";
        String secondsText = "";
        int hours = (int) (ms / (1000 * 60 * 60));
        int minutes = (int) (ms % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((ms % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        if (hours > 0) timeText = String.valueOf(hours) + ':';
        if (seconds < 10)secondsText = "0" + seconds; else secondsText = String.valueOf(seconds);

        timeText = timeText + minutes + ":" + secondsText;

        return timeText;
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

        TextView question, time;
        RadioButton firstAnswer;
        RadioButton secondAnswer;
        RadioButton thirdAnswer;
        RadioButton fourthAnswer;
        CheckBox difficulties;
        ImageView urlImage;
        Button downloadButton, openInBrowser;
        LinearLayout questionLayout;


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
            questionLayout = view.findViewById(R.id.questionLayout);
            openInBrowser = view.findViewById(R.id.open_in_browser);
            time = view.findViewById(R.id.time);
        }
    }
}

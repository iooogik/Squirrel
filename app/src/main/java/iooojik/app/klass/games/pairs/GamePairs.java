package iooojik.app.klass.games.pairs;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import iooojik.app.klass.R;


public class GamePairs extends Fragment implements View.OnClickListener{

    public GamePairs() {}

    private View view;
    //высота и ширина таблицы
    private int WIDTH = 4;
    private int HEIGHT = 4;
    //счётчик нажатий
    private int tapCounter = 0;
    //количество отгаданных пар
    private int guessedPairs;

    private int tempPressed = -1;
    private ImageButton tempButton;

    private GridLayout cellsLayout;
    private List<Cell> buttons;
    private List<ImageButton> activeButtons;

    private final Handler chrono = new Handler();
    private boolean running = true;
    private Context context;
    private int seconds;

    //создаём массив с картинками
    private int[] image_res = {R.drawable.img1, R.drawable.img2, R.drawable.img3, R.drawable.img4,
            R.drawable.img5, R.drawable.img6, R.drawable.img7, R.drawable.img8, R.drawable.img9,
            R.drawable.img10, R.drawable.img11, R.drawable.img12, R.drawable.img13, R.drawable.img14,
            R.drawable.img15, R.drawable.img16, R.drawable.img17, R.drawable.img18};


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pairs_game, container, false);

        Bundle args = this.getArguments();
        if(args != null) {
            HEIGHT = args.getInt("Height");
            WIDTH = args.getInt("Width");
        }
        context = getContext();

        //метод создания кнопок
        makeCells();

        ImageButton menu = view.findViewById(R.id.menuButton);
        menu.setOnClickListener(v -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);


            Button goToMainMenu = new Button(new ContextThemeWrapper(context,
                    R.style.Widget_MaterialComponents_Button_OutlinedButton),
                    null, R.style.Widget_MaterialComponents_Button_OutlinedButton);

            goToMainMenu.setText("Выйти в главное меню");


            Button startNewGame = new Button(new ContextThemeWrapper(context,
                    R.style.Widget_MaterialComponents_Button_OutlinedButton),
                    null, R.style.Widget_MaterialComponents_Button_OutlinedButton);

            layout.addView(startNewGame);
            layout.addView(goToMainMenu);
            builder.setView(layout);
            final Dialog dialog = builder.create();
            startNewGame.setText("Начать игру заново");
            startNewGame.setOnClickListener(v12 -> {
                makeCells();
                dialog.dismiss();
            });

            goToMainMenu.setOnClickListener(v1 -> {
                NavController navController = NavHostFragment.findNavController(getParentFragment());
                navController.navigate(R.id.nav_games);
                dialog.dismiss();
            });

            dialog.show();
        });
        return view;
    }

    @Override
    public void onClick(View v) {

        //обработка нажатия на кнопку
        final ImageButton tappedCell = (ImageButton) v;
        Handler handler = new Handler(); //поток для задержки перед закрытием картинок
        //получаем id ресурса, чтобы установить на кнопку
        final int tappedResID = getResID(tappedCell);
        //ставим картинку
        tappedCell.setBackgroundResource(tappedResID);
        //убираем возможность повторного нажатия на кнопку
        if(tappedCell == tempButton){
            tempButton.setBackgroundColor(Color.GRAY);
            tempButton.setEnabled(true);
            tempButton = null;
            tempPressed = -1;
            return;
        }

        if (tempPressed == -1) {
            tempPressed = tappedResID;
            tempButton = tappedCell;
        } else {

            for (ImageButton b: activeButtons) {
                b.setClickable(false); //делаем кликабельными кнопки
            }

            //задержка перед проверкой, чтобы у пользователя появилось время на запоминание картинок
            handler.postDelayed(() -> {

                //если картинки совпадают
                if (tempPressed == tappedResID) {
                    //делаем кнопки невидимыми, иначе они сместятся
                    tappedCell.setBackgroundColor(Color.TRANSPARENT);
                    tempButton.setBackgroundColor(Color.TRANSPARENT);
                    //обнуляем счётчики и переменные, делаем неактивными кнопки
                    tappedCell.setEnabled(false);
                    tempButton.setEnabled(false);
                    tempPressed = -1;

                    tempButton = null;
                    guessedPairs++;
                    isGameEnd(); //проверяем, найдены все пары или нет

                } else { //если не совпадают
                    tappedCell.setBackgroundColor(Color.GRAY);
                    tempButton.setBackgroundColor(Color.GRAY);

                    tempPressed = -1;
                    tempButton.setEnabled(true);
                    tempButton = null;
                }

                for (ImageButton b: activeButtons) {
                    b.setClickable(true); //делаем кликабельными кнопки
                }

            }, 1000); //задержка в мс
        }

        tapCounter++; //счётчик нажатий
    }

    @SuppressLint("SetTextI18n")
    private void isGameEnd(){
        if(guessedPairs == (HEIGHT*WIDTH)/2){
            running = false;
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Поздравляю! Вы нашли все пары!");
            TextView time = view.findViewById(R.id.timer);

            TextView textView = new TextView(getContext());
            textView.setTextSize(16.0f);
            int padding = 25;
            textView.setPadding(padding, padding, padding, 0);
            textView.setText("Игра закончена \nХодов: " + tapCounter/2 +
                    "\nВремя: " + time.getText().toString() + "\n Вы получили 3 койна!");
            
            addCoins();
            
            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.addView(textView);
            builder.setView(linearLayout);
            builder.setCancelable(false);
            builder.setPositiveButton("Выйти в главное меню", (dialog, which) -> {
                NavController navController = NavHostFragment.findNavController(getParentFragment());
                navController.navigate(R.id.nav_games);
            });
            builder.setNegativeButton("Начать заново", (dialog, which) -> {
                makeCells();
                dialog.cancel();
            });
            builder.create().show();
        }
    }

    private void addCoins() {
    }

    private int getResID(View v) {
        //получаем id ресурса
        return Integer.parseInt(((String) v.getTag()).split(",")[2]);
    }

    private void makeCells() {
        Collections.shuffle(Collections.singletonList(image_res));
        running = false;

        TextView time = view.findViewById(R.id.timer);
        time.setText("");
        time.setVisibility(View.INVISIBLE);
        guessedPairs = 0;
        tapCounter = 0;
        //метод создания кнопок
        ImageButton[][] cells = new ImageButton[HEIGHT][WIDTH];
        //инициализация сетки
        cellsLayout = view.findViewById(R.id.CellsLayout);
        cellsLayout.removeAllViews();
        cellsLayout.setColumnCount(HEIGHT);
        //массив с картинками и кнопками
        List<Image> images = new ArrayList<>();
        buttons = new ArrayList<>();

        //заносим картинки в массив
        for (int i = 0; i < (HEIGHT * WIDTH)/2; i++) {
            images.add(new Image(i, image_res[i], 0));
        }


        int k = 0; //переменная, которая используется для получения кнопки и картинки из массива
        for (int i = 0; i < HEIGHT; i++) {//строка
            for (int j = 0; j < WIDTH; j++) { //столбец
                LayoutInflater inflater = (LayoutInflater) this.context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                Image image = images.get(k); //получаем картинку

                cells[i][j] = (ImageButton) inflater.inflate(R.layout.cell, cellsLayout, false);

                //ставим картинку на кнопку
                cells[i][j].setBackgroundResource(image.getResourceID());
                //делаем проверку, чтобы не было больше 2 одинаковых картинок
                image.setTwiceUsed(image.getTwiceUsed() + 1);
                if (image.getTwiceUsed() >= 2) {
                    k++;
                }

                //устанавливаем тег, по которому будем определять схожесть картинок
                cells[i][j].setTag(i + "," + j + "," + image.getResourceID());
                //добавляем в массив кнопок кнопку
                buttons.add(new Cell(cells[i][j]));
            }
        }

        //перемешиваем кнопки
        Collections.shuffle(buttons);
        //показываем пользователю картинки
        int u = 0;
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                Cell cell = buttons.get(u);
                cellsLayout.addView(cell.getButton());
                u++;
            }
        }
        //задержка
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (getContext() == context) {
                setButtonsInGrid();
                seconds = 0;
                time.setVisibility(View.VISIBLE);
                startTimer();
            }

        }, 5000);

    }

    private void setButtonsInGrid(){
        activeButtons = new ArrayList<>();
        //размещаем картинки в сетке
        cellsLayout.removeAllViews();
        int u = 0;
        //ставим кнопки в сетку
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                Cell cell = buttons.get(u);
                ImageButton btnCell = cell.getButton();
                LayoutInflater inflater = (LayoutInflater) getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                ImageButton button = (ImageButton)
                        inflater.inflate(R.layout.cell, cellsLayout, false);
                button.setBackgroundColor(Color.GRAY);
                button.setTag(btnCell.getTag());
                button.setOnClickListener(this);
                activeButtons.add(button);
                //добавляем кнопку в сетку
                cellsLayout.addView(button);
                u++;
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private void startTimer(){
        final TextView timer = view.findViewById(R.id.timer);
        running = true;
        seconds = 0;
        chrono.post(new Runnable() {
            @Override
            public void run() {
                if(running) {
                    int minutes = (seconds % 3600) / 60;
                    int secon = seconds % 60;
                   String time = String.format("%02d:%02d", minutes, secon);
                    timer.setText(time);
                    seconds++;
                    chrono.postDelayed(this, 1000);
                }

            }
        });
    }

}

package iooojik.app.klass.games.tiktaktoe;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

import iooojik.app.klass.R;
import iooojik.app.klass.games.cells.CellImageView;

public class GameTikTak extends Fragment implements View.OnClickListener{

    public GameTikTak() {}

    private View view;
    //высота и ширина таблицы
    private int WIDTH = 3;
    private int HEIGHT = 3;

    private int tapCounter = 0;

    private GridLayout cellsLayout;
    private List<CellImageView> buttons;
    private List<ImageView> activeButtons;
    private int[][] gameProcess = new int[3][3];


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_game_tik_tak, container, false);
        makeCells();
        setButtonsInGrid();
        return view;
    }

    @Override
    public void onClick(View v) {
        if (tapCounter == 8){
            gameEnd(3);
        }else {
            tapCounter++;
            final ImageView tappedCell = (ImageView) v;
            String[] coordinates = tappedCell.getTag().toString().split(",");

            int raw = Integer.valueOf(coordinates[0]);
            int column = Integer.valueOf(coordinates[1]);
            if (gameProcess[raw][column] != 1 && gameProcess[raw][column] != 2) {
                gameProcess[raw][column] = 1;
                tappedCell.setImageResource(R.drawable.dagger_game);
            }
            int k = 0, n = 0;
            //проверяем, выйграл ли кто-то
            //проверяем по горизонтали
            for (int i = 0; i < HEIGHT; i++) {
                n = 0;
                k = 0;
                for (int j = 0; j < WIDTH; j++) {
                    if (gameProcess[i][j] == 1) k++;
                    else if (gameProcess[i][j] == 2) n+=2;
                }
                if (k == 3 || n ==6) break;
            }
            if (k == 3) gameEnd(1);
            else if (n == 6) gameEnd(2);
            else {
                //проверяем по вертикали
                for (int i = 0; i < WIDTH; i++) {
                    n = 0;
                    k = 0;
                    for (int j = 0; j < HEIGHT; j++) {
                        if (gameProcess[j][i] == 1) k++;
                        else if (gameProcess[j][i] == 2) n+=2;
                    }
                    if (k == 3 || n ==6) break;
                }
            }
            if (k == 3) gameEnd(1);
            else if (n == 6) gameEnd(2);
            else {
                n = 0;
                k = 0;
                //проверяем диагонали
                for (int i = 0; i < HEIGHT; i++) {
                    if (gameProcess[i][i] == 1) k++;
                    else if (gameProcess[i][i] == 2) n+=2;
                }
                if (k == 3) gameEnd(1);
                else if (n == 6) gameEnd(2);
                else {
                    n = 0;
                    k = 0;
                    int q = 0;
                    for (int i = 2; i > -1; i--) {
                        if (gameProcess[i][q] == 1) k++;
                        else if (gameProcess[i][q] == 2) n+=2;
                        q++;
                    }
                    if (k == 3) gameEnd(1);
                    else if (n == 6) gameEnd(2);
                    else opponentsMove();
                }
            }
        }
    }

    private void opponentsMove() {
        tapCounter++;
        boolean empty = true;
        while (empty) {
            int a = 3, b = 3;
            while (a >= 3) a = (int) (Math.random() * 10);
            while (b >= 3) b = (int) (Math.random() * 10);
            if (gameProcess[a][b] != 1 && gameProcess[a][b] != 2) {
                gameProcess[a][b] = 2;
                //показываем ход ai
                for (ImageView img: activeButtons) {
                    if (img.getTag().toString().equals(a + "," + b))
                        img.setImageResource(R.drawable.zero_game);
                }
                empty = false;
            }

        }

    }

    @SuppressLint("SetTextI18n")
    private void gameEnd(int winner){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
        builder.setCancelable(true);
        switch (winner){
            case 1:
                builder.setMessage("Игра закончена! Вы победили робота!");
                break;
            case 2:
                builder.setMessage("Игра закончена! Робот победил вас...");
                break;
            case 3:
                builder.setMessage("Игра закончена! Ничья");
        }

        builder.setNegativeButton("Закончить", (dialog, which) -> {
            NavController navController = NavHostFragment.findNavController(getParentFragment());
            navController.navigate(R.id.nav_games);
            dialog.cancel();
        });

        builder.setPositiveButton("Начать заново", (dialog, which) -> makeCells());

        builder.create().show();

    }

    private void addCoins() {}

    private void makeCells() {
        tapCounter = 0;
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                gameProcess[i][j] = 0;
            }
        }
        //метод создания кнопок
        ImageView[][] cells = new ImageView[HEIGHT][WIDTH];
        //инициализация сетки
        cellsLayout = view.findViewById(R.id.CellsLayout);
        cellsLayout.removeAllViews();
        cellsLayout.setColumnCount(HEIGHT);
        //массив с картинками и кнопками
        buttons = new ArrayList<>();
        activeButtons = new ArrayList<>();


        for (int i = 0; i < WIDTH; i++) {//строка
            for (int j = 0; j < HEIGHT; j++) { //столбец
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                cells[i][j] = (ImageView) inflater.inflate(R.layout.cell, cellsLayout, false);

                //устанавливаем тег, по которому будем определять схожесть картинок
                cells[i][j].setTag(i + "," + j);
                //добавляем в массив кнопок кнопку
                buttons.add(new CellImageView(cells[i][j]));
            }
        }
        setButtonsInGrid();
    }

    private void setButtonsInGrid(){
        activeButtons = new ArrayList<>();
        //размещаем картинки в сетке
        cellsLayout.removeAllViews();
        int u = 0;
        //ставим кнопки в сетку
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {

                CellImageView cell = buttons.get(u);
                ImageView btnCell = cell.getButton();
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                ImageView button = (ImageView) inflater.inflate(R.layout.cell_image_view, cellsLayout, false);

                button.setTag(btnCell.getTag());
                button.setOnClickListener(this);
                activeButtons.add(button);
                //добавляем кнопку в сетку
                cellsLayout.addView(button);
                u++;

            }
        }
    }
}

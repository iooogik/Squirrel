package iooojik.app.klass.games.tiktaktoe;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import iooojik.app.klass.R;
import iooojik.app.klass.games.Cell;

public class GameTikTak extends Fragment implements View.OnClickListener{

    public GameTikTak() {}

    private View view;
    //высота и ширина таблицы
    private int WIDTH = 3;
    private int HEIGHT = 3;

    private int tempPressed = -1;
    private ImageButton tempButton;

    private GridLayout cellsLayout;
    private List<Cell> buttons;
    private List<ImageButton> activeButtons;
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
        final ImageButton tappedCell = (ImageButton) v;
        tappedCell.setImageResource(R.drawable.game_dagger);
        String[] coordinates = tappedCell.getTag().toString().split(",");
        int raw = Integer.valueOf(coordinates[0]);
        int column = Integer.valueOf(coordinates[1]);
        gameProcess[raw][column] = 1;
        int k = 0;
        //проверяем варианты по горизонтали
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                if (gameProcess[i][j] == 1){k++;}
            }
            if (k != 3) k = 0;
        }
        //проверяем по вертикали
        int q = 0;
        k = 0;
        for (int i = 0; i < WIDTH; i++) {
            while (q != 3) { if (gameProcess[i][q] == 1) k++; q++; }
            q = 0;
        }

        if (k == 0) {
            //проверяем варианты по диагоналям
            //справа налево
            for (int i = 0; i < HEIGHT; i++) if (gameProcess[i][i] == 1) k++;

            if (k != 3) {
                k = 0;
                //слева направо
                q = 2;
                for (int i = 0; i < HEIGHT; i++) {if (gameProcess[i][q] == 1) k++; q--; }
            }
        }
        if (k == 3) gameEnd();

        opponentsMove();

    }

    private void opponentsMove() {
        boolean empty = true;
        while (empty) {
            int a = 4, b = 4;
            while (a >= 4) a = (int) (Math.random() * 10);
            while (b >= 4) b = (int) (Math.random() * 10);
            if (gameProcess[a][b] != 1) {
                gameProcess[a][b] = 2;
                //показываем ход ai
                empty = false;
            }

        }

    }

    @SuppressLint("SetTextI18n")
    private void gameEnd(){
        Toast.makeText(getContext(), "Игра закончена!", Toast.LENGTH_LONG).show();
    }

    private void addCoins() {}

    private void makeCells() {
        //метод создания кнопок

        ImageButton[][] cells = new ImageButton[HEIGHT][WIDTH];
        //инициализация сетки
        cellsLayout = view.findViewById(R.id.CellsLayout);
        cellsLayout.removeAllViews();
        cellsLayout.setColumnCount(HEIGHT);
        //массив с картинками и кнопками
        buttons = new ArrayList<>();

        for (int i = 0; i < HEIGHT; i++) {//строка
            for (int j = 0; j < WIDTH; j++) { //столбец
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                cells[i][j] = (ImageButton) inflater.inflate(R.layout.cell, cellsLayout, false);

                //устанавливаем тег, по которому будем определять схожесть картинок
                cells[i][j].setTag(i + "," + j);
                //добавляем в массив кнопок кнопку
                buttons.add(new Cell(cells[i][j]));
            }
        }

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
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
}

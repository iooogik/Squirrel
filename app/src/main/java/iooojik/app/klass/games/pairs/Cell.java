package iooojik.app.klass.games.pairs;

import android.widget.ImageButton;

public class Cell {
    ImageButton button;
    int x, y;

    public Cell(ImageButton button, int x, int y) {
        this.button = button;
        this.x = x;
        this.y = y;
    }

    public ImageButton getButton() {
        return button;
    }
}

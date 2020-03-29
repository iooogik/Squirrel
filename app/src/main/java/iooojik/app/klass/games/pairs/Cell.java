package iooojik.app.klass.games.pairs;

import android.widget.ImageButton;

public class Cell {
    private ImageButton button;

    Cell(ImageButton button) {
        this.button = button;
    }

    public ImageButton getButton() {
        return button;
    }
}

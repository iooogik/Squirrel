package iooojik.app.klass.games;

public class Game {
    private String name;
    private int imageID;
    int gameID;

    Game(String name, int imageID, int gameID) {
        this.name = name;
        this.imageID = imageID;
        this.gameID = gameID;
    }

    public String getName() {
        return name;
    }

    int getImageID() {
        return imageID;
    }

    int getGameID() {
        return gameID;
    }
}

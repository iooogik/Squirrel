package iooojik.app.klass.games;

public class Game {
    String name;
    int imageID;
    int gameID;

    public Game(String name, int imageID, int gameID) {
        this.name = name;
        this.imageID = imageID;
        this.gameID = gameID;
    }

    public String getName() {
        return name;
    }

    public int getImageID() {
        return imageID;
    }

    public int getGameID() {
        return gameID;
    }
}

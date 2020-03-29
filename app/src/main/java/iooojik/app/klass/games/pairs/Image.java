package iooojik.app.klass.games.pairs;


public class Image {
    private int id;
    private int resourceID;
    private int TwiceUsed;

    Image(int id, int resourceID, int twiceUsed) {
        this.id = id;
        this.resourceID = resourceID;
        this.TwiceUsed = twiceUsed;
    }

    int getTwiceUsed() {
        return TwiceUsed;
    }

    void setTwiceUsed(int twiceUsed) {
        TwiceUsed = twiceUsed;
    }

    public int getId() {
        return id;
    }

    int getResourceID() {
        return resourceID;
    }
}

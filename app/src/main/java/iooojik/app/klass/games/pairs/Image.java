package iooojik.app.klass.games.pairs;


public class Image {
    int id;
    int resourceID;
    int TwiceUsed;

    public Image(int id, int resourceID, int twiceUsed) {
        this.id = id;
        this.resourceID = resourceID;
        this.TwiceUsed = twiceUsed;
    }

    public int getTwiceUsed() {
        return TwiceUsed;
    }

    public void setTwiceUsed(int twiceUsed) {
        TwiceUsed = twiceUsed;
    }

    public int getId() {
        return id;
    }

    public int getResourceID() {
        return resourceID;
    }
}

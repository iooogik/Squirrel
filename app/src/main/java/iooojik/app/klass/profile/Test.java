package iooojik.app.klass.profile;

public class Test {
    String SQL; //sql-запрос, чтобы сохранить в локальной бд тест
    String author;//автор

    public Test(String SQL, String author) {
        this.SQL = SQL;
        this.author = author;
    }

    public String getSQL() {
        return SQL;
    }

    public String getAuthor() {
        return author;
    }
}

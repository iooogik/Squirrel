package iooogik.app.modelling.notes;

interface NoteInterface {
    void updData(String databaseName, String name, String note, String shortNote);
    String getBtnName();
    int getBtnID();
    void updFragment();
    void updShopNotes(String databaseName, String name, String booleans);
    void alarmDialog(final String title, final String text);

}

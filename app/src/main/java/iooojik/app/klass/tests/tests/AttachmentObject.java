package iooojik.app.klass.tests.tests;

class AttachmentObject {
    private int numQuestion;
    private String fileURL;

    AttachmentObject(int numQuestion, String fileURL) {
        this.numQuestion = numQuestion;
        this.fileURL = fileURL;
    }

    int getNumQuestion() {
        return numQuestion;
    }

    String getFileURL() {
        return fileURL;
    }
}

package iooojik.app.klass.group;

class FileInfo {
    private int res_id;
    private String name;
    private String fileURL;

    FileInfo(int res_id, String name, String fileURL) {
        this.res_id = res_id;
        this.name = name;
        this.fileURL = fileURL;
    }

    String getFileURL() {
        return fileURL;
    }

    void setFileURL(String fileURL) {
        this.fileURL = fileURL;
    }

    int getRes_id() {
        return res_id;
    }

    void setRes_id(int res_id) {
        this.res_id = res_id;
    }

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }
}

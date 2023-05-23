package commons;

public enum URL {
    IMPORT_API("/api/api/definition/import"),
    IMPORT_SCENARIO("/api/api/automation/import");

    public String url;

    URL(String url) {
        this.url = url;
    }

}

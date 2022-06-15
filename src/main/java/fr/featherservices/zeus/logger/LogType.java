package fr.featherservices.zeus.logger;

public enum LogType {

    INFO(""),
    ERROR(""),
    WARNING("");

    private String display;

    LogType(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }
}

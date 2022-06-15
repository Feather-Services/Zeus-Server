package fr.featherservices.zeus.instance.commons;

import java.util.Map;

public class Image {

    private String name;
    private ServerType serverType;

    private Map<String, Object> environmentVariables;

    public Image(String name, ServerType serverType, Map<String, Object> environmentVariables) {
        this.name = name;
        this.serverType = serverType;
        this.environmentVariables = environmentVariables;
    }

    public String getName() {
        return name;
    }

    public ServerType getServerType() {
        return serverType;
    }

    public Map<String, Object> getEnvironmentVariables() {
        return environmentVariables;
    }
}

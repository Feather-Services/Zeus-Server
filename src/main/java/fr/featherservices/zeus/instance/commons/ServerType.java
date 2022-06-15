package fr.featherservices.zeus.instance.commons;

public class ServerType {

    private String name;
    private String[] description;
    private String proxyName;

    public ServerType(String name, String[] description, String proxyName) {
        this.name = name;
        this.description = description;
        this.proxyName = proxyName;
    }

    public String getName() {
        return name;
    }

    public String[] getDescription() {
        return description;
    }

    public String getProxyName() {
        return proxyName;
    }
}

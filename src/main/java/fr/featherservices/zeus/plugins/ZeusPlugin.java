package fr.featherservices.zeus.plugins;

import java.io.File;

import fr.featherservices.zeus.ZeusServer;
import fr.featherservices.zeus.file.FileConfiguration;

public class ZeusPlugin {

    private String name;
    private File dataFolder;
    private PluginInfo info;
    private File pluginJar;

    protected final void setInfo(FileConfiguration file, File pluginJar) {
        this.info = new PluginInfo(file);
        this.name = info.getName();
        this.dataFolder = new File(ZeusServer.getInstance().getPluginFolder(), name);
        this.pluginJar = pluginJar;
    }

    protected final File getPluginJar() {
        return pluginJar;
    }

    public void onLoad() {

    }

    public void onEnable() {

    }

    public void onDisable() {

    }

    public final String getName() {
        return name;
    }

    public final File getDataFolder() {
        return new File(dataFolder.getAbsolutePath());
    }

    public final PluginInfo getInfo() {
        return info;
    }

    public final ZeusServer getServer() {
        return ZeusServer.getInstance();
    }

}

package fr.featherservices.zeus.plugins;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import fr.featherservices.zeus.ZeusServer;
import fr.featherservices.zeus.command.CommandExecutor;
import fr.featherservices.zeus.command.DefaultCommands;
import fr.featherservices.zeus.file.FileConfiguration;
import fr.featherservices.zeus.logger.LogType;

public class PluginManager {

    private Map<String, ZeusPlugin> plugins;
    private DefaultCommands defaultExecutor;
    private List<Executor> executors;
    private File pluginFolder;

    public PluginManager(DefaultCommands defaultExecutor, File pluginFolder) {
        this.defaultExecutor = defaultExecutor;
        this.pluginFolder = pluginFolder;
        this.executors = new ArrayList<>();
        this.plugins = new LinkedHashMap<>();
    }

    protected void loadPlugins() {
        for (File file : pluginFolder.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".jar")) {
                boolean found = false;
                try (ZipInputStream zip = new ZipInputStream(new FileInputStream(file))) {
                    while (true) {
                        ZipEntry entry = zip.getNextEntry();
                        if (entry == null) {
                            break;
                        }
                        String name = entry.getName();
                        if (name.endsWith("plugin.yml") || name.endsWith("zeus.yml")) {
                            found = true;

                            FileConfiguration pluginYaml = new FileConfiguration(zip);
                            String main = pluginYaml.get("main", String.class);
                            String pluginName = pluginYaml.get("name", String.class);

                            if (plugins.containsKey(pluginName)) {
                                System.err.println("Ambiguous plugin name in " + file.getName() + " with the plugin \"" + plugins.get(pluginName).getClass().getName() + "\"");
                                break;
                            }
                            URLClassLoader child = new URLClassLoader(new URL[]{file.toURI().toURL()}, ZeusServer.getInstance().getClass().getClassLoader());
                            Class<?> clazz = Class.forName(main, true, child);
                            ZeusPlugin plugin = (ZeusPlugin) clazz.getDeclaredConstructor().newInstance();
                            plugin.setInfo(pluginYaml, file);
                            plugins.put(plugin.getName(), plugin);
                            plugin.onLoad();
                            ZeusServer.getInstance().getConsole().sendMessage("Loading plugin " + file.getName() + " " + plugin.getInfo().getVersion() + " by " + plugin.getInfo().getAuthor());
                            break;
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Unable to load plugin \"" + file.getName() + "\"");
                    e.printStackTrace();
                }
                if (!found) {
                    System.err.println("Jar file " + file.getName() + " has no plugin.yml!");
                }
            }
        }
    }

    public List<ZeusPlugin> getPlugins() {
        return new ArrayList<>(plugins.values());
    }

    public ZeusPlugin getPlugin(String name) {
        return plugins.get(name);
    }

    public void fireExecutors(String[] args) throws Exception {
        try {
            defaultExecutor.execute(args);
        } catch (Exception e) {
            System.err.println("Error while running default command \"" + args[0] + "\"");
            e.printStackTrace();
        }
        for (Executor entry : executors) {
            try {
                entry.executor.execute(args);
            } catch (Exception e) {
                System.err.println("Error while passing command \"" + args[0] + "\" to the plugin \"" + entry.plugin.getName() + "\"");
                e.printStackTrace();
            }
        }
    }

    public void registerCommands(ZeusPlugin plugin, CommandExecutor executor) {
        executors.add(new Executor(plugin, executor));
    }

    public void unregsiterAllCommands(ZeusPlugin plugin) {
        executors.removeIf(each -> each.plugin.equals(plugin));
    }

    public File getPluginFolder() {
        return new File(pluginFolder.getAbsolutePath());
    }

    protected static class Executor {
        public ZeusPlugin plugin;
        public CommandExecutor executor;

        public Executor(ZeusPlugin plugin, CommandExecutor executor) {
            this.plugin = plugin;
            this.executor = executor;
        }
    }

}

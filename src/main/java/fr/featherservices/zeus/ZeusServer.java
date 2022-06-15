package fr.featherservices.zeus;

import fr.featherservices.zeus.command.DefaultCommands;
import fr.featherservices.zeus.console.Console;
import fr.featherservices.zeus.file.ServerProperties;
import fr.featherservices.zeus.license.License;
import fr.featherservices.zeus.logger.LogType;
import fr.featherservices.zeus.logger.Logger;
import fr.featherservices.zeus.plugins.PluginManager;
import fr.featherservices.zeus.plugins.ZeusPlugin;
import fr.featherservices.zeus.scheduler.Tick;
import fr.featherservices.zeus.scheduler.ZeusScheduler;
import fr.featherservices.zeus.utils.CustomStringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicBoolean;

public class ZeusServer {

    private static ZeusServer instance;

    public static final String VERSION = "0.0.1";

    private static Thread thread;

    public static void main(String[] args) throws IOException {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Try to start Zeus server...");
            }
        });
        thread.start();

        new ZeusServer();
    }

    public static Thread getThread() {
        return thread;
    }

    //==========================================================================

    private AtomicBoolean isRunning;
    private boolean licenseValid;
    private Console console;
    private Tick tick;
    private ZeusScheduler zeusScheduler;


    private ServerProperties serverProperties;
    private File messageFile;
    private File dockerFile;
    private File imagesFile;
    private File serverTypeFile;

    private File pluginFolder;
    private File imagesFolder;

    private PluginManager pluginManager;

    public ZeusServer() throws IOException {
        instance = this;
        this.isRunning = new AtomicBoolean(true);
        console = new Console(System.in, System.out, System.err);

        File serverPropertiesFile = new File("server.properties");
        if (!serverPropertiesFile.exists()) {
            try (InputStream in = getClass().getClassLoader().getResourceAsStream("server.properties")) {
                Files.copy(in, serverPropertiesFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.serverProperties = new ServerProperties(serverPropertiesFile);

        License license = new License(this.serverProperties.getLicense(), "http://dev.helderia.fr");
        license.request();
        console.sendMessage(" |- FeatherServices - License checking: " + license.getLicense());
        if (license.isValid()) {
            console.sendMessage("------------------------");
            console.sendMessage(" |- License accepted");
            console.sendMessage(" |- Welcome: " + license.getLicensedTo());
            console.sendMessage(" |- Starting...");
            console.sendMessage("------------------------");
        } else {
            console.sendMessage("------------------------");
            console.sendMessage(" |- License denied");
            console.sendMessage(" |- Return error: " + license.getReturn());
            console.sendMessage("------------------------");
            return;
        }

        console.sendMessage("Loading message configuration..");
        File messagesFile = new File("messagesFile.yml");
        if (!messagesFile.exists()) {
            try (InputStream in = getClass().getClassLoader().getResourceAsStream("messagesFile.yml")) {
                Files.copy(in, messagesFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        console.sendMessage("Loading docker file..");
        dockerFile = new File("/images/Dockerfile");
        if (!dockerFile.exists()) {
            try (InputStream in = getClass().getClassLoader().getResourceAsStream("Dockerfile")) {
                Files.copy(in, dockerFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        console.sendMessage("Loading images..");
        imagesFile = new File("images.json");
        if (!imagesFile.exists()) {
            try (InputStream in = getClass().getClassLoader().getResourceAsStream("images.json")) {
                Files.copy(in, imagesFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        imagesFolder = new File("images");
        if (!imagesFolder.exists()) {
            Files.createDirectories(Paths.get("/images"));
        }

        System.out.println("Loading server type..");
        serverTypeFile = new File("serverType.json");
        if (!serverTypeFile.exists()) {
            try (InputStream in = getClass().getClassLoader().getResourceAsStream("serverType.json")) {
                Files.copy(in, serverTypeFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        zeusScheduler = new ZeusScheduler();
        tick = new Tick(this);

        console.sendMessage("Loading plugins..");
        pluginFolder = new File("plugins");
        if (!pluginFolder.exists()) {
            Files.createDirectories(Paths.get("/plugins"));
        }
        pluginManager = new PluginManager(new DefaultCommands(), pluginFolder);
        try {
            Method loadPluginsMethod = PluginManager.class.getDeclaredMethod("loadPlugins");
            loadPluginsMethod.setAccessible(true);
            loadPluginsMethod.invoke(pluginManager);
            loadPluginsMethod.setAccessible(false);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException |
                 InvocationTargetException e) {
            e.printStackTrace();
        }

        for (ZeusPlugin plugin : ZeusServer.getInstance().getPluginManager().getPlugins()) {
            console.sendMessage("Enabling plugin " + plugin.getName() + " " + plugin.getInfo().getVersion());
            plugin.onEnable();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            ZeusServer.getInstance().terminate();
        }));

        console.run();
    }

    protected void terminate() {
        isRunning.set(false);
        console.sendMessage("Stopping Server...");

        for (ZeusPlugin plugin : ZeusServer.getInstance().getPluginManager().getPlugins()) {
            console.sendMessage("Disabling plugin " + plugin.getName() + " " + plugin.getInfo().getVersion());
            plugin.onDisable();
        }

        tick.waitAndKillThreads(5000);

        console.sendMessage("Server closed");
        console.logs.close();
    }

    public void stopServer() {
        System.exit(0);
    }

    public void dispatchCommand(String str) {
        String[] command;
        if (str.startsWith("/")) {
            command = CustomStringUtils.splitStringToArgs(str.substring(1));
        } else {
            command = CustomStringUtils.splitStringToArgs(str);
        }
        dispatchCommand(command);
    }

    public void dispatchCommand(String... args) {
        try {
            ZeusServer.getInstance().getPluginManager().fireExecutors(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PluginManager getPluginManager() {
        return pluginManager;
    }

    public Tick getHeartBeat() {
        return tick;
    }

    public Console getConsole() {
        return console;
    }

    public ZeusScheduler geScheduler() {
        return zeusScheduler;
    }

    public AtomicBoolean getIsRunning() {
        return isRunning;
    }

    public boolean isLicenseValid() {
        return licenseValid;
    }

    public ServerProperties getServerProperties() {
        return serverProperties;
    }

    public File getMessageFile() {
        return messageFile;
    }

    public File getDockerFile() {
        return dockerFile;
    }

    public File getImagesFile() {
        return imagesFile;
    }

    public File getServerTypeFile() {
        return serverTypeFile;
    }

    public File getPluginFolder() {
        return pluginFolder;
    }

    public File getImagesFolder() {
        return imagesFolder;
    }

    public boolean isRunning() {
        return isRunning.get();
    }

    public static ZeusServer getInstance() {
        return instance;
    }
}

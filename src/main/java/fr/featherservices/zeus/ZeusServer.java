package fr.featherservices.zeus;

import fr.featherservices.zeus.file.ServerProperties;
import fr.featherservices.zeus.license.License;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.concurrent.atomic.AtomicBoolean;

public class ZeusServer {

    private static ZeusServer instance;

    private static final String VERSION = "0.0.1";

    public static void main(String[] args) throws IOException {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Try to start Zeus server...");
            }
        });
        thread.start();

        new ZeusServer();
    }

    //==========================================================================

    private AtomicBoolean isRunning;
    private boolean licenseValid;

    private ServerProperties serverProperties;

    public ZeusServer() throws IOException {
        instance = this;
        isRunning = new AtomicBoolean(true);

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
        System.out.println(" |- FeatherServices - License checking: " + license.getLicense());
        if (license.isValid()) {
            System.out.println("------------------------");
            System.out.println(" |- License accepted");
            System.out.println(" |- Welcome: " + license.getLicensedTo());
            System.out.println(" |- Starting...");
            System.out.println("------------------------");
        } else {
            System.out.println("------------------------");
            System.out.println(" |- License denied");
            System.out.println(" |- Return error: " + license.getReturn());
            System.out.println("------------------------");
            return;
        }


    }

    public static ZeusServer getInstance() {
        return instance;
    }
}

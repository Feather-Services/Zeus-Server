package fr.featherservices.zeus.file;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;

public class ServerProperties {

    public static final String COMMENT = "For explaination of what each of the options does, please visit: http://wiki.featherservices.fr/wiki/ZeusServer/Server_properties";

    private File file;
    private String license;

    private String user;
    private String password;

    public ServerProperties(File file) throws IOException {
        this.file = file;

        Properties def = new Properties();
        InputStreamReader dfReader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream("server.properties"), StandardCharsets.UTF_8);
        def.load(dfReader);
        dfReader.close();

        Properties prop = new Properties();
        InputStreamReader stream = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
        prop.load(stream);
        stream.close();

        for (Map.Entry<Object, Object> entry : def.entrySet()) {
            String key = entry.getKey().toString();
            String value = entry.getValue().toString();
            prop.putIfAbsent(key, value);
        }
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
        prop.store(pw, COMMENT);
        pw.close();

        this.license = prop.getProperty("license");
        this.user = prop.getProperty("user");
        this.password = prop.getProperty("password");
    }

    public File getFile() {
        return file;
    }

    public String getLicense() {
        return license;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }
}

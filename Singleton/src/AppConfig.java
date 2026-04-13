import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


class AppConfig {
    private static final AppConfig INSTANCE = new AppConfig();
    private final Properties properties;

    private AppConfig() {
        properties = new Properties();

        try (InputStream input = getClass().getResourceAsStream("/config.properties")) {
            properties.load(input);

        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении config.properties: " + e.getMessage(), e);
        }
    }

    public  static synchronized AppConfig getInstance() {
        return INSTANCE;
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }
}

class Test {
    public static void main(String[] args) {

        AppConfig config1 = AppConfig.getInstance();
        AppConfig config2 = AppConfig.getInstance();

        System.out.println("Имя приложения 1 : " + config1.getProperty("app.name"));
        System.out.println("Имя приложения 2 : " + config2.getProperty("app.name"));

        config1.setProperty("app.name", "ChangeApp");

        System.out.println("Имя приложения 1 : " + config1.getProperty("app.name"));
        System.out.println("Имя приложения 2 : " + config2.getProperty("app.name"));

        System.out.println("Версия 2 : " + config2.getProperty("app.version"));
        System.out.println("Порт сервера 2 : " + config2.getProperty("server.port"));
    }
}
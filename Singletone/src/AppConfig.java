import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║               ПАТТЕРН SINGLETON — AppConfig                     ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║ Задача: загрузить файл config.properties ОДИН РАЗ и             ║
 * ║ предоставлять его содержимое всем частям программы.             ║
 * ╚══════════════════════════════════════════════════════════════════╝
 *
 * СУТЬ ПАТТЕРНА:
 *   Singleton гарантирует, что класс имеет только ОДИН экземпляр,
 *   и предоставляет глобальную точку доступа к нему.
 *
 * ДЛЯ ЧЕГО НЕ ЗНАЮЩИМ JAVA:
 *   - "static" означает, что поле/метод принадлежит КЛАССУ, а не объекту.
 *     Т.е. существует в единственном экземпляре независимо от того,
 *     сколько объектов создано.
 *   - "private" — доступ только внутри этого класса.
 *   - "final" — значение нельзя изменить после присвоения.
 */
public class AppConfig {

    // ── ШАГ 1: Единственный экземпляр класса ──────────────────────────────
    //
    // "private"  → снаружи поле не видно — никто не может заменить его.
    // "static"   → поле принадлежит классу, а не объекту (создаётся сразу
    //              при загрузке класса JVM).
    // "final"    → ссылку нельзя перезаписать после первого присвоения.
    //
    // Это называется "ранняя инициализация" (eager initialization):
    // экземпляр создаётся при загрузке класса, а не при первом обращении.
    private static final AppConfig INSTANCE = new AppConfig();

    // Объект Properties из стандартной библиотеки Java.
    // Он хранит пары ключ=значение, прочитанные из .properties файла.
    private final Properties properties;

    // ── ШАГ 2: Приватный конструктор ─────────────────────────────────────
    //
    // "private" конструктор ЗАПРЕЩАЕТ писать: new AppConfig() снаружи класса.
    // Это ключевой приём Singleton — только сам класс может создать себя.
    private AppConfig() {
        properties = new Properties();

        // Загружаем файл из classpath (папки, где лежат скомпилированные файлы).
        // getClass().getResourceAsStream() ищет файл в classpath, а не по
        // абсолютному пути — так программа работает и в JAR-архиве.
        try (InputStream input = getClass().getResourceAsStream("/config.properties")) {

            if (input == null) {
                // Если файл не найден — выбрасываем RuntimeException.
                // RuntimeException — необъявляемое исключение, т.е. метод
                // не обязан объявлять его через "throws".
                throw new RuntimeException("Файл config.properties не найден в classpath!");
            }

            // Метод load() читает файл в формате key=value и заполняет properties.
            properties.load(input);
            System.out.println("[AppConfig] Файл настроек успешно загружен.");

        } catch (IOException e) {
            // IOException — ошибка ввода-вывода (диск, сеть и т.п.)
            throw new RuntimeException("Ошибка при чтении config.properties: " + e.getMessage(), e);
        }
    }

    // ── ШАГ 3: Публичный статический метод доступа ────────────────────────
    //
    // Единственный способ получить экземпляр класса снаружи.
    // Каждый раз возвращает один и тот же объект INSTANCE.
    public static AppConfig getInstance() {
        return INSTANCE;
    }

    // ── ШАГ 4: Методы для чтения настроек ────────────────────────────────

    /**
     * Возвращает значение настройки по ключу.
     *
     * @param key  ключ из файла (например "app.name")
     * @return     значение или null, если ключ не найден
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * Возвращает весь объект Properties для обхода всех настроек.
     */
    public Properties getAllProperties() {
        return properties;
    }

    // ── Точка входа: демонстрация работы ──────────────────────────────────
    public static void main(String[] args) {

        System.out.println("=== Демонстрация паттерна Singleton ===\n");

        // Получаем экземпляр первый раз — файл загружается здесь.
        AppConfig config1 = AppConfig.getInstance();

        // Получаем экземпляр второй раз — файл НЕ загружается снова.
        AppConfig config2 = AppConfig.getInstance();

        // Проверяем: это действительно ОДИН И ТОТ ЖЕ объект?
        // "==" для объектов сравнивает ссылки (адреса в памяти).
        System.out.println("config1 == config2: " + (config1 == config2));
        // Вывод: true — оба указывают на один объект в памяти.

        System.out.println("\n--- Все настройки из файла ---");
        // entrySet() возвращает множество пар ключ-значение.
        // Цикл for-each перебирает каждую пару.
        config1.getAllProperties().entrySet().forEach(entry ->
            System.out.printf("  %-25s = %s%n", entry.getKey(), entry.getValue())
        );

        System.out.println("\n--- Чтение отдельных настроек ---");
        System.out.println("Имя приложения : " + config1.getProperty("app.name"));
        System.out.println("Версия         : " + config1.getProperty("app.version"));
        System.out.println("Порт сервера   : " + config1.getProperty("server.port"));
    }
}

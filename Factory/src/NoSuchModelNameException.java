/**
 * Исключение: имя модели не найдено.
 *
 * "extends Exception" делает его ОБЪЯВЛЯЕМЫМ (checked exception).
 * Это значит: любой метод, который его выбрасывает, ОБЯЗАН указать
 * "throws NoSuchModelNameException" в своей сигнатуре.
 * Компилятор Java проверяет это — отсюда и название "checked".
 */
public class NoSuchModelNameException extends Exception {

    public NoSuchModelNameException(String modelName) {
        // Вызов конструктора родительского класса Exception с сообщением.
        super("Модель с названием \"" + modelName + "\" не существует.");
    }
}

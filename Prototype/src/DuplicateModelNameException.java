/**
 * Исключение: дублирование названия модели.
 *
 * Тоже ОБЪЯВЛЯЕМОЕ (checked) — наследует Exception.
 */
public class DuplicateModelNameException extends Exception {

    public DuplicateModelNameException(String modelName) {
        super("Модель с названием \"" + modelName + "\" уже существует.");
    }
}

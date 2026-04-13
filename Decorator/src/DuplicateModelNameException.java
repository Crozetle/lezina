public class DuplicateModelNameException extends Exception {

    public DuplicateModelNameException(String modelName) {
        super("Модель с названием \"" + modelName + "\" уже существует.");
    }
}

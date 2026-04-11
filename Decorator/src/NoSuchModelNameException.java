
public class NoSuchModelNameException extends Exception {

    public NoSuchModelNameException(String modelName) {
        super("Модель с названием \"" + modelName + "\" не существует.");
    }
}

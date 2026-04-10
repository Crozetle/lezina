/**
 * Исключение: недопустимая цена модели (отрицательная или NaN).
 *
 * "extends RuntimeException" делает его НЕОБЪЯВЛЯЕМЫМ (unchecked exception).
 * Методы могут выбрасывать его без указания "throws" в сигнатуре.
 * Это логическая ошибка программиста, а не ситуация, которую звонящий
 * обязан обрабатывать.
 */
public class ModelPriceOutOfBoundsException extends RuntimeException {

    public ModelPriceOutOfBoundsException(double price) {
        super("Недопустимая цена модели: " + price + ". Цена должна быть >= 0.");
    }
}


public class ModelPriceOutOfBoundsException extends RuntimeException {

    public ModelPriceOutOfBoundsException(double price) {
        super("Недопустимая цена модели: " + price + ". Цена должна быть >= 0.");
    }
}

/**
 * Интерфейс «Транспортное средство».
 *
 * Описывает общий контракт для классов Автомобиль и Мотоцикл.
 *
 * В Java интерфейс — это «обещание»: любой класс, объявивший
 * "implements Transport", ОБЯЗАН реализовать все методы интерфейса.
 *
 * Благодаря интерфейсу можно работать с переменной типа Transport,
 * не зная, Автомобиль это или Мотоцикл (полиморфизм).
 */
public interface Transport {

    /** Возвращает марку транспортного средства. */
    String getBrand();

    /** Устанавливает марку транспортного средства. */
    void setBrand(String brand);

    /**
     * Возвращает массив названий всех моделей.
     * String[] — массив строк в Java.
     */
    String[] getModelNames();

    /**
     * Возвращает массив цен всех моделей.
     * double[] — массив вещественных чисел.
     */
    double[] getModelPrices();

    /**
     * Добавляет модель с указанным именем и ценой.
     *
     * @throws DuplicateModelNameException если имя уже занято
     * @throws ModelPriceOutOfBoundsException если цена < 0
     */
    void addModel(String name, double price)
            throws DuplicateModelNameException;

    /**
     * Удаляет модель по имени.
     *
     * @throws NoSuchModelNameException если модель не найдена
     */
    void removeModel(String name) throws NoSuchModelNameException;

    /**
     * Возвращает цену модели по имени.
     *
     * @throws NoSuchModelNameException если модель не найдена
     */
    double getModelPrice(String name) throws NoSuchModelNameException;

    /**
     * Устанавливает цену модели по имени.
     *
     * @throws NoSuchModelNameException если модель не найдена
     * @throws ModelPriceOutOfBoundsException если цена < 0
     */
    void setModelPrice(String name, double price) throws NoSuchModelNameException;

    /**
     * Переименовывает модель.
     *
     * @throws NoSuchModelNameException      если старое имя не найдено
     * @throws DuplicateModelNameException   если новое имя уже существует
     */
    void setModelName(String oldName, String newName)
            throws NoSuchModelNameException, DuplicateModelNameException;

    /** Возвращает количество моделей. */
    int getModelCount();
}

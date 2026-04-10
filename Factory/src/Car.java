import java.util.Arrays;

/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║                  КЛАСС «АВТОМОБИЛЬ» (Car)                       ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║ Хранит марку и массив моделей.                                  ║
 * ║ Реализует интерфейс Transport.                                  ║
 * ╚══════════════════════════════════════════════════════════════════╝
 *
 * Структура данных: простой массив объектов Model[].
 * При добавлении/удалении массив пересоздаётся с помощью Arrays.copyOf
 * и System.arraycopy (как требует задание).
 */
public class Car implements Transport {

    // ── Внутренний класс Model ────────────────────────────────────────────
    //
    // "private" внутренний класс — виден только внутри Car.
    // В задании требуется, чтобы Автомобиль хранил массив моделей.
    private class Model {
        String name;   // название модели
        double price;  // цена модели

        // Конструктор внутреннего класса
        Model(String name, double price) {
            this.name  = name;
            this.price = price;
        }
    }

    // ── Поля класса ───────────────────────────────────────────────────────
    private String brand;    // марка автомобиля
    private Model[] models;  // массив моделей

    // ── Конструктор ───────────────────────────────────────────────────────
    //
    // Принимает марку и начальный размер массива моделей.
    // Все элементы массива изначально null.
    public Car(String brand) {
        this.brand  = brand;
        this.models = new Model[1];
    }

    // ── Геттер/Сеттер марки ───────────────────────────────────────────────
    @Override
    public String getBrand() { return brand; }

    @Override
    public void setBrand(String brand) { this.brand = brand; }

    // ── Получить массив имён всех моделей ─────────────────────────────────
    @Override
    public String[] getModelNames() {
        // Создаём новый массив строк той же длины
        String[] names = new String[models.length];
        for (int i = 0; i < models.length; i++) {
            // Проверяем на null: незаполненные ячейки пропускаем
            names[i] = (models[i] != null) ? models[i].name : null;
        }
        return names;
    }

    // ── Получить массив цен всех моделей ──────────────────────────────────
    @Override
    public double[] getModelPrices() {
        double[] prices = new double[models.length];
        for (int i = 0; i < models.length; i++) {
            prices[i] = (models[i] != null) ? models[i].price : Double.NaN;
        }
        return prices;
    }

    // ── Получить цену по имени ────────────────────────────────────────────
    @Override
    public double getModelPrice(String name) throws NoSuchModelNameException {
        for (Model m : models) {                  // for-each по массиву
            if (m != null && m.name.equals(name)) // .equals() для строк!
                return m.price;
        }
        throw new NoSuchModelNameException(name); // не нашли — исключение
    }

    // ── Установить цену по имени ──────────────────────────────────────────
    @Override
    public void setModelPrice(String name, double price) throws NoSuchModelNameException {
        if (price < 0) throw new ModelPriceOutOfBoundsException(price);
        for (Model m : models) {
            if (m != null && m.name.equals(name)) {
                m.price = price;
                return;
            }
        }
        throw new NoSuchModelNameException(name);
    }

    // ── Переименовать модель ──────────────────────────────────────────────
    @Override
    public void setModelName(String oldName, String newName)
            throws NoSuchModelNameException, DuplicateModelNameException {
        // Проверяем, что новое имя ещё не занято
        for (Model m : models) {
            if (m != null && m.name.equals(newName))
                throw new DuplicateModelNameException(newName);
        }
        for (Model m : models) {
            if (m != null && m.name.equals(oldName)) {
                m.name = newName;
                return;
            }
        }
        throw new NoSuchModelNameException(oldName);
    }

    // ── Добавить модель ───────────────────────────────────────────────────
    //
    // Задание требует использовать Arrays.copyOf() для расширения массива.
    // Arrays.copyOf(original, newLength) создаёт НОВЫЙ массив длиной newLength,
    // копирует в него элементы из original (или заполняет null если newLength больше).
    @Override
    public void addModel(String name, double price) throws DuplicateModelNameException {
        if (price < 0) throw new ModelPriceOutOfBoundsException(price);

        // Проверка на дублирование
        for (Model m : models) {
            if (m != null && m.name.equals(name))
                throw new DuplicateModelNameException(name);
        }

        // Сначала ищем пустую ячейку (null)
        for (int i = 0; i < models.length; i++) {
            if (models[i] == null) {
                models[i] = new Model(name, price);
                return;
            }
        }

        // Пустых ячеек нет — расширяем массив на 1 элемент
        // Arrays.copyOf создаёт новый массив длиной models.length + 1
        models = Arrays.copyOf(models, models.length + 1);
        models[models.length - 1] = new Model(name, price);
    }

    // ── Удалить модель ────────────────────────────────────────────────────
    //
    // Задание требует использовать System.arraycopy и Arrays.copyOf.
    //
    // System.arraycopy(src, srcPos, dest, destPos, length) — быстрое
    // нативное копирование участка массива в другой массив.
    @Override
    public void removeModel(String name) throws NoSuchModelNameException {
        // Ищем индекс удаляемого элемента
        int idx = -1;
        for (int i = 0; i < models.length; i++) {
            if (models[i] != null && models[i].name.equals(name)) {
                idx = i;
                break;
            }
        }
        if (idx == -1) throw new NoSuchModelNameException(name);

        // Создаём новый массив на 1 меньше
        Model[] newModels = Arrays.copyOf(models, models.length - 1);

        // Копируем элементы после idx, «сдвигая» их влево на 1 позицию
        // System.arraycopy(источник, откуда, куда, позиция_куда, сколько)
        System.arraycopy(models, idx + 1, newModels, idx, models.length - idx - 1);

        models = newModels;
    }

    // ── Размер массива моделей ────────────────────────────────────────────
    @Override
    public int getModelCount() { return models.length; }

    // ── toString для удобного вывода ──────────────────────────────────────
    //
    // @Override означает, что мы переопределяем метод из родительского
    // класса Object. Все классы в Java неявно наследуют Object.
    @Override
    public String toString() {
        // StringBuilder — изменяемая строка (String неизменяем в Java)
        StringBuilder sb = new StringBuilder();
        sb.append("Автомобиль [").append(brand).append("]\n");
        for (Model m : models) {
            if (m != null) {
                sb.append("  Модель: ").append(m.name)
                  .append(", цена: ").append(m.price).append("\n");
            }
        }
        return sb.toString();
    }
}

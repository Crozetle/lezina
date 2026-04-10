/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║             ПАТТЕРН FACTORY METHOD                              ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║ TransportFactory — интерфейс фабрики.                          ║
 * ║ AutoFactory     — конкретная фабрика Автомобилей.               ║
 * ║ MotoFactory     — конкретная фабрика Мотоциклов.                ║
 * ║ TransportUtils  — класс со статическими методами и полем        ║
 * ║                   factory (хранит ссылку на текущую фабрику).  ║
 * ╚══════════════════════════════════════════════════════════════════╝
 *
 * СУТЬ ПАТТЕРНА Factory Method:
 *   Определяет интерфейс для создания объекта, но делегирует
 *   конкретным подклассам/реализациям решение о том, какой класс
 *   инстанциировать.
 *
 *   Клиентский код (TransportUtils.createInstance) работает через
 *   интерфейс TransportFactory и ничего не знает о Car или Motorcycle.
 *   Чтобы переключить тип создаваемых объектов — достаточно вызвать
 *   setTransportFactory() с другой реализацией фабрики.
 */

// ═════════════════════════════════════════════════════════════════════
// 1. Интерфейс фабрики
// ═════════════════════════════════════════════════════════════════════

interface TransportFactory {
    /**
     * Создаёт новое транспортное средство.
     *
     * @param brand марка
     * @return новый объект, реализующий Transport
     */
    Transport createInstance(String brand);
}


// ═════════════════════════════════════════════════════════════════════
// 2. Конкретная фабрика: Автомобиль
// ═════════════════════════════════════════════════════════════════════

class AutoFactory implements TransportFactory {

    // "implements TransportFactory" — объявляем, что выполняем контракт.
    // Компилятор проверит, что createInstance реализован.
    @Override
    public Transport createInstance(String brand) {
        // Создаём и возвращаем Car.
        // Возвращаем как Transport (интерфейс) — клиент не знает о Car напрямую.
        return new Car(brand);
    }
}


// ═════════════════════════════════════════════════════════════════════
// 3. Конкретная фабрика: Мотоцикл
// ═════════════════════════════════════════════════════════════════════

class MotoFactory implements TransportFactory {
    @Override
    public Transport createInstance(String brand) {
        return new Motorcycle(brand);
    }
}


// ═════════════════════════════════════════════════════════════════════
// 4. Вспомогательный класс TransportUtils
// ═════════════════════════════════════════════════════════════════════

class TransportUtils {

    // Приватное статическое поле — хранит ссылку на текущую фабрику.
    // По умолчанию — AutoFactory (порождает Автомобили).
    //
    // "private" → нельзя заменить напрямую снаружи.
    // "static"  → одно на весь класс, не зависит от экземпляра.
    private static TransportFactory factory = new AutoFactory();

    // Приватный конструктор — запрещает создавать экземпляры TransportUtils.
    // Все методы статические — объект не нужен.
    private TransportUtils() {}

    // ── Установить фабрику ────────────────────────────────────────────────
    public static void setTransportFactory(TransportFactory f) {
        factory = f;
    }

    // ── Создать транспорт через текущую фабрику ───────────────────────────
    //
    // Это и есть «фабричный метод» — клиент вызывает createInstance(),
    // не зная о Car/Motorcycle.
    public static Transport createInstance(String brand) {
        return factory.createInstance(brand);
    }

    // ── Среднее арифметическое цен ────────────────────────────────────────
    public static double getAveragePrice(Transport t) {
        double[] prices = t.getModelPrices();
        if (prices.length == 0) return 0;
        double sum = 0;
        for (double p : prices) {
            if (!Double.isNaN(p)) sum += p;
        }
        return sum / prices.length;
    }

    // ── Вывод всех моделей ────────────────────────────────────────────────
    public static void printModels(Transport t) {
        System.out.println("Марка: " + t.getBrand());
        System.out.println("Модели:");
        for (String name : t.getModelNames()) {
            System.out.println("  - " + (name != null ? name : "(пусто)"));
        }
    }

    // ── Вывод всех цен ────────────────────────────────────────────────────
    public static void printPrices(Transport t) {
        System.out.println("Цены моделей [" + t.getBrand() + "]:");
        double[] prices = t.getModelPrices();
        String[] names  = t.getModelNames();
        for (int i = 0; i < prices.length; i++) {
            System.out.printf("  %-15s : %.2f%n", names[i], prices[i]);
        }
    }

    // ── Точка входа: демонстрация Factory Method ──────────────────────────
    public static void main(String[] args) {
        System.out.println("=== Демонстрация паттерна Factory Method ===\n");

        // --- Фабрика по умолчанию: AutoFactory ---
        Transport car = TransportUtils.createInstance("Toyota");
        System.out.println("Создан объект типа: " + car.getClass().getSimpleName());

        try {
            car.addModel("Camry",  35000);
            car.addModel("Corolla", 25000);
            car.addModel("RAV4",   42000);
        } catch (DuplicateModelNameException e) {
            System.err.println("Ошибка: " + e.getMessage());
        }

        printModels(car);
        printPrices(car);
        System.out.printf("Средняя цена: %.2f%n%n", getAveragePrice(car));

        // --- Переключаем фабрику на MotoFactory ---
        System.out.println("--- Переключение фабрики на MotoFactory ---");
        TransportUtils.setTransportFactory(new MotoFactory());

        Transport moto = TransportUtils.createInstance("Honda");
        System.out.println("Создан объект типа: " + moto.getClass().getSimpleName());

        try {
            moto.addModel("CBR600RR", 12000);
            moto.addModel("Africa Twin", 15000);
        } catch (DuplicateModelNameException e) {
            System.err.println("Ошибка: " + e.getMessage());
        }

        printModels(moto);
        printPrices(moto);
        System.out.printf("Средняя цена: %.2f%n%n", getAveragePrice(moto));

        // --- Демонстрация исключений ---
        System.out.println("--- Демонстрация исключений ---");
        try {
            car.getModelPrice("Несуществующая");
        } catch (NoSuchModelNameException e) {
            System.out.println("Поймано: " + e.getMessage());
        }

        try {
            car.addModel("Camry", 0); // дублирование
        } catch (DuplicateModelNameException e) {
            System.out.println("Поймано: " + e.getMessage());
        }

        try {
            car.addModel("Yaris", -1000); // отрицательная цена
        } catch (DuplicateModelNameException e) {
            System.out.println("Поймано: " + e.getMessage());
        } catch (ModelPriceOutOfBoundsException e) {
            System.out.println("Поймано (unchecked): " + e.getMessage());
        }

        // --- [PROTOTYPE] Демонстрация clone() ---
        System.out.println("\n--- Демонстрация паттерна Prototype (clone) ---");
        try {
            Car carOriginal = new Car("BMW");
            carOriginal.addModel("X5", 80000);
            carOriginal.addModel("M3", 70000);

            // Клонируем — создаём независимую копию объекта
            Car carCopy = (Car) carOriginal.clone();
            carCopy.setBrand("BMW (копия)");
            carCopy.setModelPrice("X5", 99999); // меняем цену только в копии

            System.out.println("Оригинал: " + carOriginal);
            System.out.println("Копия:    " + carCopy);
            // Оба false — полностью независимые объекты:
            System.out.println("carOriginal != carCopy            : " + (carOriginal != carCopy));
            System.out.println("Массивы тоже разные (глубокий клон): " + (carOriginal.getModelNames() != carCopy.getModelNames()));

        } catch (CloneNotSupportedException e) {
            System.err.println("Клонирование не поддерживается: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }
}
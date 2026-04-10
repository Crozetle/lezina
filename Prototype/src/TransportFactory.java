
interface TransportFactory {
    Transport createInstance(String brand);
}


class AutoFactory implements TransportFactory {
    @Override
    public Transport createInstance(String brand) {
        return new Car(brand);
    }
}


class MotoFactory implements TransportFactory {
    @Override
    public Transport createInstance(String brand) {
        return new Motorcycle(brand);
    }
}


class TransportUtils {
    private static TransportFactory factory = new AutoFactory();

    private TransportUtils() {
    }

    public static void setTransportFactory(TransportFactory f) {
        factory = f;
    }

    public static Transport createInstance(String brand) {
        return factory.createInstance(brand);
    }

    public static double getAveragePrice(Transport t) {
        double[] prices = t.getModelPrices();
        if (prices.length == 0) return 0;
        double sum = 0;
        for (double p : prices) {
            if (!Double.isNaN(p)) sum += p;
        }
        return sum / prices.length;
    }

    public static void printModels(Transport t) {
        System.out.println("Марка: " + t.getBrand());
        System.out.println("Модели:");
        for (String name : t.getModelNames()) {
            System.out.println("  - " + (name != null ? name : "(пусто)"));
        }
    }

    public static void printPrices(Transport t) {
        System.out.println("Цены моделей [" + t.getBrand() + "]:");
        double[] prices = t.getModelPrices();
        String[] names = t.getModelNames();
        for (int i = 0; i < prices.length; i++) {
            System.out.printf("  %-15s : %.2f%n", names[i], prices[i]);
        }
    }

    public static void main(String[] args) {
        System.out.println("AutoFactory\n");
        Transport car = TransportUtils.createInstance("Toyota");
        System.out.println("Создан объект типа: " + car.getClass().getSimpleName());

        try {
            car.addModel("Camry", 35000);
            car.addModel("Corolla", 25000);
            car.addModel("RAV4", 42000);
        } catch (DuplicateModelNameException e) {
            System.err.println("Ошибка: " + e.getMessage());
        }

        printModels(car);
        printPrices(car);
        System.out.printf("Средняя цена: %.2f%n%n", getAveragePrice(car));


        System.out.println("\nMotoFactory\n");
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


        System.out.println("\nPrototype\n");
        try {
            Car carOriginal = new Car("BMW");
            carOriginal.addModel("X5", 80000);
            carOriginal.addModel("M3", 70000);

            Car carCopy = (Car) carOriginal.clone();
            carCopy.setBrand("BMW копия");
            carCopy.setModelPrice("X5", 99999);

            System.out.println("Оригинал: " + carOriginal);
            System.out.println("Копия:    " + carCopy);

            System.out.println(carOriginal != carCopy);
            System.out.println(carOriginal.getModelNames() != carCopy.getModelNames());

        } catch (CloneNotSupportedException e) {
            System.err.println("Клонирование не поддерживается: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }
}
import java.util.concurrent.CountDownLatch;

class Test {
    public static void main(String[] args) throws InterruptedException {

        // --- Часть 1: базовая работа декоратора ---
        System.out.println("=== Часть 1: синхронизированный декоратор ===\n");

        // Создаём «сырой» объект и оборачиваем через фабричный метод.
        // Дальше работаем с ним только как с Transport — тип декоратора скрыт.
        Transport syncCar = TransportUtils.synchronizedTransport(new Car("Toyota"));

        try {
            syncCar.addModel("Camry",   35000);
            syncCar.addModel("RAV4",    42000);
            syncCar.addModel("Corolla", 25000);
        } catch (DuplicateModelNameException e) {
            System.err.println("Ошибка: " + e.getMessage());
        }

        TransportUtils.printModels(syncCar);

        // --- Часть 2: демонстрация потокобезопасности ---
        // Без синхронизации несколько потоков, одновременно добавляющих модели
        // в Motorcycle (связный список), могут повредить его внутреннюю структуру:
        // два потока могут вставить узел в одно и то же место, затерев ссылки.
        // Декоратор предотвращает это без изменения кода самого Motorcycle.
        System.out.println("\n=== Часть 2: конкурентная запись моделей ===\n");

        final int THREAD_COUNT    = 5;
        final int MODELS_PER_THREAD = 4;

        Transport sharedMoto = TransportUtils.synchronizedTransport(new Motorcycle("Honda"));

        // CountDownLatch позволяет дождаться завершения всех потоков
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        for (int t = 0; t < THREAD_COUNT; t++) {
            final int threadId = t;
            new Thread(() -> {
                try {
                    for (int m = 0; m < MODELS_PER_THREAD; m++) {
                        // Имена уникальны: каждый поток добавляет свои модели
                        sharedMoto.addModel("T" + threadId + "-M" + m,
                                            10000 + threadId * 1000 + m * 100);
                    }
                } catch (DuplicateModelNameException e) {
                    System.err.println("Дубликат: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            }).start();
        }

        latch.await();

        int expected = THREAD_COUNT * MODELS_PER_THREAD;
        int actual   = sharedMoto.getModelCount();
        System.out.println("Все потоки завершены.");
        System.out.println("Ожидалось моделей: " + expected + ", получено: " + actual
                + (actual == expected ? "  [OK]" : "  [ОШИБКА — потеря данных!]"));
        TransportUtils.printModels(sharedMoto);
    }
}

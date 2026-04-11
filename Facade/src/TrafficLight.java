// Светофор — часть подсистемы, скрытой за фасадом.
// Класс имеет package-private доступ: снаружи пакета он недостижим.
// Клиент не может ни создать его, ни напрямую вызвать его методы.
class TrafficLight {

    // Перечисление сигналов доступно внутри пакета, поскольку Car его читает.
    // Для клиента оно тоже скрыто — он видит только строковое описание в фасаде.
    enum Signal { RED, YELLOW, GREEN }

    // volatile: запись из потока светофора немедленно видна потоку автомобиля
    // без дополнительной синхронизации. Тонкий момент: volatile гарантирует
    // видимость, но не атомарность составных операций. Для одного поля-enum
    // этого достаточно — запись одной ссылки в Java атомарна.
    private volatile Signal signal  = Signal.RED;
    private volatile boolean running = false;
    private Thread thread;

    Signal getSignal() { return signal; }

    void start() {
        running = true;
        thread = new Thread(() -> {
            try {
                while (running) {
                    signal = Signal.RED;
                    Thread.sleep(3000);

                    signal = Signal.GREEN;
                    Thread.sleep(3000);

                    // Жёлтый — предупреждающий сигнал, короткий интервал
                    signal = Signal.YELLOW;
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "TrafficLight-Thread");
        thread.setDaemon(true); // не мешает JVM завершиться
        thread.start();
    }

    void stop() {
        running = false;
        thread.interrupt();
    }
}

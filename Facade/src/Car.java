// Автомобиль — часть подсистемы, скрытой за фасадом.
// Package-private: клиент не знает об этом классе.
class Car {

    // Позиция остановки: автомобиль встаёт здесь при запрещающем сигнале.
    // Выбрана так, чтобы символ [>>] (ширина 4) заканчивался прямо перед |светофором|:
    //   позиции STOP_ZONE .. STOP_ZONE+3 = автомобиль
    //   позиции 57..59               = |сигнал|
    static final int STOP_ZONE  = 53;

    // Сброс происходит сразу после стоп-зоны: машина «проехала» светофор
    // и появляется слева снова. Значение STOP_ZONE + 1 убирает кадры,
    // где машина уже не рисуется (вышла за светофор), но ещё не сброшена.
    static final int WRAP_POINT = STOP_ZONE + 1;

    // Пауза между шагами определяет «скорость» движения на экране
    private static final int TICK_MS = 150;

    private volatile int position    = 0;
    private volatile boolean moving  = false;
    private volatile boolean running = false;

    // Ссылка на светофор: автомобиль читает его сигнал каждый тик.
    // Зависимость намеренно однонаправленная: Car → TrafficLight.
    // TrafficLight ничего не знает о Car.
    private final TrafficLight light;
    private Thread thread;

    Car(TrafficLight light) {
        this.light = light;
    }

    int getPosition()  { return position; }
    boolean isMoving() { return moving;   }

    void start() {
        running = true;
        thread = new Thread(() -> {
            try {
                while (running) {
                    int pos = position;
                    TrafficLight.Signal sig = light.getSignal();

                    if (pos >= WRAP_POINT) {
                        // Автомобиль миновал светофор — сбросить на старт
                        position = 0;
                        moving   = true;
                    } else if (pos == STOP_ZONE && sig != TrafficLight.Signal.GREEN) {
                        // Достигли стоп-зоны при запрещающем сигнале — стоим.
                        // Важно: проверка только на == STOP_ZONE.
                        // Если автомобиль уже переехал стоп-зону (например, тронулся
                        // на зелёный и дошёл до 54+), он продолжает ехать даже если
                        // сигнал снова стал красным — в реальности так и происходит.
                        moving = false;
                    } else {
                        moving   = true;
                        position = pos + 1;
                    }

                    Thread.sleep(TICK_MS);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Car-Thread");
        thread.setDaemon(true);
        thread.start();
    }

    void stop() {
        running = false;
        thread.interrupt();
    }
}

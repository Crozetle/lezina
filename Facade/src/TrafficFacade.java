import java.util.Arrays;

// Фасад — единственный класс, видимый клиенту.
//
// Паттерн Facade даёт единую точку входа к подсистеме из нескольких классов.
// Здесь подсистема состоит из TrafficLight (светофор) и Car (автомобиль):
//   - оба класса package-private, клиент не может их использовать напрямую
//   - взаимодействие между ними (Car читает сигнал светофора) скрыто здесь
//   - управление потоками, логика отрисовки — всё инкапсулировано внутри
//
// Клиент вызывает start(seconds) и наблюдает результат. Больше ничего.
public class TrafficFacade {

    // Ширина «дороги» в символах
    private static final int ROAD_WIDTH = 60;
    // Позиция светофорного знака (|X| занимает 3 символа: LIGHT_POS..LIGHT_POS+2)
    private static final int LIGHT_POS  = 57;
    // Ширина символа автомобиля [>>]
    private static final int CAR_WIDTH  = 4;
    // Интервал перерисовки кадра (мс)
    private static final int RENDER_MS  = 150;

    private TrafficLight light;
    private Car          car;

    // Запускает симуляцию на указанное число секунд.
    // Цикл отрисовки блокирует вызывающий поток — намеренно:
    // фасад управляет всем временем жизни симуляции изнутри.
    public void start(int durationSeconds) throws InterruptedException {
        light = new TrafficLight();
        car   = new Car(light);

        light.start();
        car.start();

        long deadline   = System.currentTimeMillis() + durationSeconds * 1000L;
        boolean first   = true;

        while (System.currentTimeMillis() < deadline) {
            String roadLine   = buildRoadLine(car.getPosition(), light.getSignal());
            String signalLine = buildSignalLine(light.getSignal());
            String statusLine = buildStatusLine(car.getPosition(), car.isMoving());

            if (!first) {
                // Переместить курсор на 3 строки вверх, чтобы перезаписать кадр.
                // \033[3A — ANSI escape-последовательность (работает в большинстве
                // современных терминалов, включая Windows 10 cmd и PowerShell).
                System.out.print("\033[3A\r");
            }
            System.out.println(roadLine);
            System.out.println(signalLine);
            System.out.println(statusLine);
            first = false;

            Thread.sleep(RENDER_MS);
        }

        light.stop();
        car.stop();
    }

    // Строит строку дороги: точки-разделители, символ машины и знак светофора.
    private String buildRoadLine(int carPos, TrafficLight.Signal signal) {
        char[] road = new char[ROAD_WIDTH];
        Arrays.fill(road, '.');

        // Знак светофора: |X|
        road[LIGHT_POS]     = '|';
        road[LIGHT_POS + 1] = signalChar(signal);
        road[LIGHT_POS + 2] = '|';

        // Автомобиль [>>] — только если он ещё не достиг светофора
        if (carPos + CAR_WIDTH <= LIGHT_POS) {
            road[carPos]     = '[';
            road[carPos + 1] = '>';
            road[carPos + 2] = '>';
            road[carPos + 3] = ']';
        }

        return new String(road);
    }

    // Строка с цветным названием текущего сигнала (ANSI-цвета)
    private String buildSignalLine(TrafficLight.Signal signal) {
        String ansi;
        String name;
        switch (signal) {
            case RED:    ansi = "\033[31m"; name = "КРАСНЫЙ"; break;
            case YELLOW: ansi = "\033[33m"; name = "ЖЁЛТЫЙ";  break;
            default:     ansi = "\033[32m"; name = "ЗЕЛЁНЫЙ"; break;
        }
        // \033[0m сбрасывает цвет обратно — без этого весь последующий вывод окрасился бы
        return "Сигнал: " + ansi + name + "\033[0m";
    }

    private String buildStatusLine(int pos, boolean moving) {
        String status = moving ? "Едет   " : "Стоит  ";
        return "Статус: " + status + "  Позиция: " + String.format("%2d", pos) + "/" + (LIGHT_POS - 1);
    }

    private char signalChar(TrafficLight.Signal signal) {
        switch (signal) {
            case RED:    return 'R';
            case YELLOW: return 'Y';
            default:     return 'G';
        }
    }
}

import java.util.Arrays;

public class TrafficFacade {

    private static final int ROAD_WIDTH = 60;
    private static final int LIGHT_POS = 57;
    private static final int CAR_WIDTH = 4;
    private static final int RENDER_MS = 150;

    private TrafficLight light;
    private Car car;

    public void start(int durationSeconds) throws InterruptedException {
        light = new TrafficLight();
        car = new Car(light);

        light.start();
        car.start();

        long deadline = System.currentTimeMillis() + durationSeconds * 1000L;

        while (System.currentTimeMillis() < deadline) {
            String roadLine = buildRoadLine(car.getPosition(), light.getSignal());
            String signalLine = buildSignalLine(light.getSignal());
            String statusLine = buildStatusLine(car.getPosition(), car.isMoving());

            System.out.println(roadLine);
            System.out.println(signalLine);
            System.out.println(statusLine);

            Thread.sleep(RENDER_MS);
        }

        light.stop();
        car.stop();
    }

    private String buildRoadLine(int carPos, TrafficLight.Signal signal) {
        char[] road = new char[ROAD_WIDTH];
        Arrays.fill(road, '.');

        road[LIGHT_POS] = '|';
        road[LIGHT_POS + 1] = signalChar(signal);
        road[LIGHT_POS + 2] = '|';

        if (carPos + CAR_WIDTH <= LIGHT_POS) {
            road[carPos] = '[';
            road[carPos + 1] = '>';
            road[carPos + 2] = '>';
            road[carPos + 3] = ']';
        }

        return new String(road);
    }

    private String buildSignalLine(TrafficLight.Signal signal) {
        String ansi;
        String name;
        switch (signal) {
            case RED: ansi = "\033[31m"; name = "КРАСНЫЙ"; break;
            case YELLOW: ansi = "\033[33m"; name = "ЖЁЛТЫЙ";  break;
            default: ansi = "\033[32m"; name = "ЗЕЛЁНЫЙ"; break;
        }
        // \033[0m сбрасывает цвет обратно
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

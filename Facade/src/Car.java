class Car {

    private static final int SPEED = 3;
    private static final int STOP_X = 530;
    private static final int WRAP_X = 790;

    private volatile int x = 0;
    private volatile boolean running;
    private final TrafficLight light;
    private Thread thread;

    Car(TrafficLight light) {
        this.light = light;
    }

    int getX() { return x; }

    void start() {
        running = true;
        thread = new Thread(() -> {
            try {
                while (running) {
                    TrafficLight.Signal sig = light.getSignal();
                    if (x >= WRAP_X) {
                        x = 0;
                    } else if (x >= STOP_X && sig != TrafficLight.Signal.GREEN) {
                        // стоим
                    } else {
                        x += SPEED;
                    }
                    Thread.sleep(50);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    void stop() {
        running = false;
        thread.interrupt();
    }
}

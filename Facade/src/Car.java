class Car {

    static final int STOP_ZONE = 53;
    static final int WRAP_POINT = STOP_ZONE + 1;
    private static final int TICK_MS = 150;

    private volatile int position = 0;
    private volatile boolean moving = false;
    private volatile boolean running = false;

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
                        position = 0;
                        moving   = true;
                    } else if (pos == STOP_ZONE && sig != TrafficLight.Signal.GREEN) {
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

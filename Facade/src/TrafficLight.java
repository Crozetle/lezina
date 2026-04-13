class TrafficLight {

    enum Signal { RED, YELLOW, GREEN }

    private volatile Signal signal = Signal.RED;
    private volatile boolean running;
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
                    signal = Signal.YELLOW;
                    Thread.sleep(1000);
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

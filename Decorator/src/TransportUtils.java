class TransportUtils {

    private TransportUtils() {}

    static Transport synchronizedTransport(Transport t) {
        return new SynchronizedTransport(t);
    }

    static void printModels(Transport t) {
        System.out.println("Марка: " + t.getBrand() + "  (моделей: " + t.getModelCount() + ")");
        String[] names  = t.getModelNames();
        double[] prices = t.getModelPrices();
        for (int i = 0; i < names.length; i++) {
            System.out.printf("  %-20s : %.2f%n", names[i], prices[i]);
        }
    }
}

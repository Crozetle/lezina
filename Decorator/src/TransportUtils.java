// Утилитарный класс со статическими методами для работы с Transport.
//
// Метод synchronizedTransport — фабричный метод-обёртка по образцу
// Collections.synchronizedList(): клиент получает потокобезопасный «вид»
// переданного объекта, не зная о существовании класса SynchronizedTransport.
// Сигнатура соответствует условию задачи: Transport synchronizedTransport(Transport t).
class TransportUtils {

    // Утилитарный класс не должен порождать экземпляры
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

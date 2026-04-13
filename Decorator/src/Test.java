import java.util.concurrent.CountDownLatch;

class Test {
    public static void main(String[] args) {

        Transport syncCar = TransportUtils.synchronizedTransport(new Car("Toyota"));

        try {
            syncCar.addModel("Camry",   35000);
            syncCar.addModel("RAV4",    42000);
            syncCar.addModel("Corolla", 25000);
        } catch (DuplicateModelNameException e) {
            System.err.println("Ошибка: " + e.getMessage());
        }

        TransportUtils.printModels(syncCar);
    }
}

class Test {
    static Transport synchronizedTransport(Transport t) {
        return new SynchronizedTransport(t);
    }

    public static void main(String[] args) {
        Transport car = synchronizedTransport(new Car("Toyota"));

        car.addModel("Camry",   35000);
        car.addModel("RAV4",    42000);
        car.addModel("Corolla", 25000);

        System.out.println("Марка: " + car.getBrand());
        for (String name : car.getModelNames()) {
            System.out.printf("  %-15s : %.0f%n", name, car.getModelPrice(name));
        }

        car.setModelPrice("Camry", 36000);
        System.out.println("\nЦена Camry после изменения: " + car.getModelPrice("Camry"));

        car.removeModel("RAV4");
        System.out.println("Моделей после удаления RAV4: " + car.getModelCount());
    }
}

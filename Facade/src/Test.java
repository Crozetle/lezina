class Test {
    public static void main(String[] args) throws InterruptedException {
        // Клиент работает только с фасадом.
        // О существовании Car и TrafficLight, об управлении потоками,
        // о протоколе взаимодействия между ними — он не знает ничего.
        TrafficFacade facade = new TrafficFacade();

        System.out.println("Симуляция запущена. Продолжительность: 14 секунд.");
        System.out.println("(Красный 3с → Зелёный 3с → Жёлтый 1с — полный цикл 7с)\n");

        // Два полных цикла светофора: достаточно увидеть остановку и движение
        facade.start(14);

        System.out.println("\nСимуляция завершена.");
    }
}

class Test {
    public static void main(String[] args) throws InterruptedException {
        TrafficFacade facade = new TrafficFacade();

        System.out.println("Симуляция запущена.");
        facade.start(14);
        System.out.println("\nСимуляция завершена.");
    }
}

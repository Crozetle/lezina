class Test {
    public static void main(String[] args) throws Exception {
        // В реальном сценарии сервер — отдельный процесс или машина.
        // Здесь запускаем его в фоновом потоке той же JVM для демонстрации.
        MultiplyServer server = new MultiplyServer();
        server.start();
        System.out.println("Сервер запущен на порту " + MultiplyServer.PORT);

        // Небольшая пауза: гарантируем, что сервер успел занять порт
        // до того, как прокси попробует подключиться
        Thread.sleep(200);

        // Клиентская часть: создаём прокси и работаем только через интерфейс Multiplier.
        // Строка ниже — единственное место, где клиент знает о прокси-классе.
        // Дальше он использует только интерфейс и не замечает сетевых вызовов.
        Multiplier proxy = new MultiplyProxy("localhost", MultiplyServer.PORT);

        System.out.println("\nРезультаты умножения (через прокси → сервер):\n");

        double[][] cases = {
            { 3.5,   2.0  },
            {-4.0,   1.5  },
            { 0.0,  999.9 },
            { 7.0,   7.0  },
            { 1.0 / 3.0,  3.0 }  // проверка точности с вещественными числами
        };

        for (double[] c : cases) {
            double result = proxy.multiply(c[0], c[1]);
            System.out.printf("  %8.4f  ×  %8.4f  =  %.6f%n", c[0], c[1], result);
        }

        server.stop();
        System.out.println("\nСервер остановлен.");
    }
}

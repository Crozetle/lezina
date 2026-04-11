class Test {
    public static void main(String[] args) throws Exception {
        MultiplyServer server = new MultiplyServer();
        server.start();
        System.out.println("Сервер запущен на порту " + MultiplyServer.PORT);
        Thread.sleep(200);

        Multiplier proxy = new MultiplyProxy("localhost", MultiplyServer.PORT);

        double[][] cases = {
            { 3.5,   2.0  },
            {-4.0,   1.5  },
            { 0.0,  999.9 },
            { 7.0,   7.0  }
        };

        for (double[] c : cases) {
            double result = proxy.multiply(c[0], c[1]);
            System.out.printf("  %8.4f  ×  %8.4f  =  %.6f%n", c[0], c[1], result);
        }

        server.stop();
        System.out.println("\nСервер остановлен.");
    }
}

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

// Серверная часть: слушает порт 5000 и выполняет умножение для клиентов.
// Реальный объект (Real Subject) — именно здесь происходит вычисление.
// Прокси на клиенте перенаправляет запросы сюда и возвращает ответ.
class MultiplyServer {

    static final int PORT = 5000;

    private ServerSocket serverSocket;
    private volatile boolean running = false;
    private Thread thread;

    void start() throws IOException {
        serverSocket = new ServerSocket(PORT);
        running = true;
        thread = new Thread(() -> {
            // Простой однопоточный цикл: принимаем клиентов по одному.
            // Для демонстрации паттерна этого достаточно;
            // в production каждый accept порождал бы отдельный поток или задачу.
            while (running) {
                try {
                    Socket client = serverSocket.accept();
                    handleClient(client);
                } catch (IOException e) {
                    if (running) e.printStackTrace();
                }
            }
        }, "MultiplyServer-Thread");
        thread.setDaemon(true);
        thread.start();
    }

    // Обработка одного клиентского запроса.
    // Протокол: клиент посылает строку "a b\n", сервер отвечает строкой "result\n".
    // Текстовый протокол прост для отладки и не требует сериализации объектов.
    private void handleClient(Socket client) {
        try (BufferedReader in  = new BufferedReader(new InputStreamReader(client.getInputStream()));
             PrintWriter    out = new PrintWriter(client.getOutputStream(), true)) {

            String line  = in.readLine();           // "3.5 2.0"
            String[] parts = line.trim().split("\\s+");
            double a = Double.parseDouble(parts[0]);
            double b = Double.parseDouble(parts[1]);

            out.println(a * b); // реальное вычисление происходит здесь, на сервере
        } catch (IOException | NumberFormatException e) {
            System.err.println("Сервер: ошибка обработки запроса: " + e.getMessage());
        }
    }

    void stop() throws IOException {
        running = false;
        serverSocket.close(); // прерывает блокирующий accept()
    }
}

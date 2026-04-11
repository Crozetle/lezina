import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

// Прокси (Remote Proxy) — суррогатный объект, заменяющий реальный вычислитель.
//
// Паттерн Proxy создаёт заместителя, управляющего доступом к реальному объекту.
// Здесь реальный объект (сервер) находится в другом адресном пространстве —
// это классический Remote Proxy.
//
// Клиентский код вызывает multiply(a, b) на прокси, считая что операция локальная.
// Прокси незаметно:
//   1. открывает TCP-соединение с сервером
//   2. сериализует аргументы в текст и отправляет
//   3. читает числовой ответ
//   4. возвращает его как double
//
// Клиент ничего не знает о сети. Если сервер переедет на другой хост или
// протокол изменится — меняется только этот класс, не клиент.
class MultiplyProxy implements Multiplier {

    private final String host;
    private final int    port;

    // Адрес сервера передаётся в конструктор: прокси настраивается однократно,
    // а не «зашивает» координаты сервера в код метода.
    MultiplyProxy(String host, int port) {
        this.host = host;
        this.port = port;
    }

    // Каждый вызов открывает новое соединение — socket per call.
    // Это просто и корректно для демонстрации; в высоконагруженной системе
    // стоило бы держать постоянное соединение или использовать пул.
    // try-with-resources гарантирует закрытие сокета даже при исключении.
    @Override
    public double multiply(double a, double b) throws Exception {
        try (Socket socket      = new Socket(host, port);
             PrintWriter   out  = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in  = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(a + " " + b);              // отправить запрос
            return Double.parseDouble(in.readLine()); // получить и вернуть ответ
        }
    }
}

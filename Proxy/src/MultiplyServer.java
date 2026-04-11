import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

class MultiplyServer {

    static final int PORT = 5000;

    private ServerSocket serverSocket;
    private volatile boolean running = false;
    private Thread thread;

    void start() throws IOException {
        serverSocket = new ServerSocket(PORT);
        running = true;
        thread = new Thread(() -> {
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

    private void handleClient(Socket client) {
        try (BufferedReader in  = new BufferedReader(new InputStreamReader(client.getInputStream()));
             PrintWriter    out = new PrintWriter(client.getOutputStream(), true)) {

            String line  = in.readLine();
            String[] parts = line.trim().split(" ");
            double a = Double.parseDouble(parts[0]);
            double b = Double.parseDouble(parts[1]);

            out.println(a * b);
        } catch (IOException | NumberFormatException e) {
            System.err.println("Сервер: ошибка обработки запроса: " + e.getMessage());
        }
    }

    void stop() throws IOException {
        running = false;
        serverSocket.close();
    }
}

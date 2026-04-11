import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class MultiplyProxy implements Multiplier {

    private final String host;
    private final int port;

    MultiplyProxy(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public double multiply(double a, double b) throws Exception {
        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(a + " " + b);
            return Double.parseDouble(in.readLine());
        }
    }
}

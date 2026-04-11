import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;


class OutputStreamAdapter implements StringArrayWriter {

    private final OutputStream out; // Adaptee

    OutputStreamAdapter(OutputStream out) {
        this.out = out;
    }

    @Override
    public void writeLines(String[] lines) throws IOException {
        for (String line : lines) {
            out.write(line.getBytes(StandardCharsets.UTF_8));
            out.write('\n');
        }
    }
}

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

class StringArrayAdapter {

    private final ByteTarget target;

    StringArrayAdapter(ByteTarget target) {
        this.target = target;
    }

    byte[] writeLines(String[] lines) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (String line : lines) {
            baos.write(line.getBytes(StandardCharsets.UTF_8));
            baos.write('\n');
        }
        return target.write(baos.toByteArray());
    }
}

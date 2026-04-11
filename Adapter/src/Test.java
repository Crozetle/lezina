import java.io.IOException;

class Test {
    public static void main(String[] args) throws IOException {
        String[] lines = {
            "строка 1",
            "строка 22",
            "строка 333"
        };

        StringArrayWriter consoleWriter = new OutputStreamAdapter(System.out);
        consoleWriter.writeLines(lines);
    }
}

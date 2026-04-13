import java.nio.charset.StandardCharsets;

class Test {
    public static void main(String[] args) throws Exception {
        String[] lines = {"строка 1", "строка 22", "строка 333"};

        System.out.println("Входной массив строк:");
        for (String line : lines) System.out.println("  " + line);

        StringArrayAdapter adapter = new StringArrayAdapter(new ByteTarget());

        System.out.println("\nТаргет выводит байтовый поток:");
        byte[] bytes = adapter.writeLines(lines);

        String[] restored = new String(bytes, StandardCharsets.UTF_8).split("\n");
        System.out.println("\nОбратный перевод в массив строк:");
        for (String line : restored) System.out.println("  " + line);
    }
}

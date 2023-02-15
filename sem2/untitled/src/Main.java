public class Main {
    //    Остаток от урока:
    public static void main(String[] args) throws IOException {
        Path file = Path.of("file.txt");

        boolean exists = Files.exists(file);
// Files.createFile(file);
// Files.createDirectory(file);

// Files.writeString(file, "content");
        String content = Files.readString(file);
        System.out.println(content);

        OutputStream outputStream = Files.newOutputStream(file);
        InputStream inputStream = System.in;

        // inputStream -> outputStream

    }
}

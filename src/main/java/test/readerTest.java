package test;

import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class readerTest {
    public static void main(String[] args) throws Exception{
        String content = new String(Files.readAllBytes(Paths.get("src/main/resources/previousGame.json")));
        System.out.println(content);
    }
}

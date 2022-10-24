package br.edu.utfpr.sistemarquivos;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public class FileReader {

    public void read(Path path) {
        File text = new File(path.toUri());
        int content;

        if(text.exists()) {
            try (InputStream reader = new FileInputStream(text.toString())) {
                while ((content = reader.read()) != -1) {
                    System.out.print((char) content);
                }
                System.out.println();
            } catch (IOException ex) {
                System.out.println("Error you specified a wrong input!");
            }
        } else {
            System.out.println("File doesn't exist!");
        }

    }
}

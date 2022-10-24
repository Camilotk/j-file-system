package br.edu.utfpr.sistemarquivos;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

public class Application {
    public static final String ROOT = "." + File.separator + "hd";

    public Application() {
        executar();
    }

    private void executar() {

        final Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the new file system!");

        var stop = false;
        var currentPath = Paths.get(ROOT);

        while (!stop) {
            try {
                System.out.print("$> ");
                final var command = Command.parseCommand(scanner.nextLine());
                currentPath = command.execute(currentPath);
                stop = command.shouldStop();
            } catch (UnsupportedOperationException | IOException ex) {
                System.out.printf("%s", ex.getMessage()).println();
            }
        }

        System.out.println("Exiting file system. Good bye!");
    }

    public static void main(String[] args) {
        new Application();
    }
}

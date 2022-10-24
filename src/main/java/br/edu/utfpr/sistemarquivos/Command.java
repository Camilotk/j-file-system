package br.edu.utfpr.sistemarquivos;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.stream.Stream;

public enum Command {

    LIST() {
        @Override
        boolean accept(String command) {
            final var commands = command.split(" ");
            return commands.length > 0 && commands[0].startsWith("LIST") || commands[0].startsWith("list");
        }

        @Override
        Path execute(Path path) throws IOException {
            File directory = new File(path.toUri());
            Stream<String> pathnames = Arrays.stream(directory.list());
            pathnames.forEach(System.out::println);
            return path;
        }
    },
    SHOW() {
        private String[] parameters = new String[]{};

        @Override
        void setParameters(String[] parameters) {
            this.parameters = parameters;
        }

        @Override
        boolean accept(String command) {
            final var commands = command.split(" ");
            return commands.length > 0 && commands[0].startsWith("SHOW") || commands[0].startsWith("show");
        }

        @Override
        Path execute(Path path) {
            FileReader reader = new FileReader();
            if(parameters.length > 1) {
                reader.read(Paths.get(path.toString(), parameters[1]));
            } else {
                System.out.println("Command can't be empty! You need to specify a file.");
            }

            return path;
        }
    },
    BACK() {
        @Override
        boolean accept(String command) {
            final var commands = command.split(" ");
            return commands.length > 0 && commands[0].startsWith("BACK") || commands[0].startsWith("back");
        }

        @Override
        Path execute(Path path) {
            Path newPath = null;

            if (!path.toString().equals(Application.ROOT)) {
                String[] segments = path.toString().split(File.separator);
                String[] newSegments = Arrays.copyOf(segments, segments.length - 1);

                String newURI = String.join(File.separator, newSegments);
                newPath = Paths.get(newURI);
            } else {
                System.out.println("Can't go beyond the root folder! Sorry.");
                newPath = path;
            }

            return (new File(newPath.toUri()).exists())? newPath : path;
        }
    },
    OPEN() {
        private String[] parameters = new String[]{};

        @Override
        void setParameters(String[] parameters) {
            this.parameters = parameters;
        }

        @Override
        boolean accept(String command) {
            final var commands = command.split(" ");
            return commands.length > 0 && commands[0].startsWith("OPEN") || commands[0].startsWith("open");
        }

        @Override
        Path execute(Path path) {
            Path newLocation;

            if(parameters.length < 2) {
                System.out.println("Command can't be empty! Please specify a folder.");
                return path;
            }

            if(parameters[1] != null && (parameters[1] instanceof String)) {
                 newLocation = Paths.get(path.toString(), parameters[1]);
            } else {
                System.out.println("Folder not found!");
                newLocation = path;
            }

            return (new File(newLocation.toUri()).exists())? newLocation : path;
        }
    },
    DETAIL() {
        private String[] parameters = new String[]{};

        @Override
        void setParameters(String[] parameters) {
            this.parameters = parameters;
        }

        @Override
        boolean accept(String command) {
            final var commands = command.split(" ");
            return commands.length > 0 && commands[0].startsWith("DETAIL") || commands[0].startsWith("detail");
        }

        @Override
        Path execute(Path path) {

            // TODO implementar conforme enunciado
            Path queryPath;

            if (parameters.length >= 2) {
                queryPath = Paths.get(path.toString(), parameters[1]);
            } else {
                System.out.println("Command can't be empty! Specify a file or folder.");
                return path;
            }

            BasicFileAttributeView view = Files.getFileAttributeView(queryPath, BasicFileAttributeView.class);

            try {
                BasicFileAttributes attributes = view.readAttributes();
                System.out.println("Size: " + attributes.size());
                System.out.println("Created at: " + attributes.creationTime());
                System.out.println("Last modified at: " + attributes.lastModifiedTime());
                System.out.println("Last access at: " + attributes.lastAccessTime());
            } catch (IOException e) {
                System.out.println("Reading error. Verify if the parameter is right.");
            }


            return path;
        }
    },
    EXIT() {
        @Override
        boolean accept(String command) {
            final var commands = command.split(" ");
            return commands.length > 0 && commands[0].startsWith("EXIT") || commands[0].startsWith("exit");
        }

        @Override
        Path execute(Path path) {
            System.out.print("Exiting...");
            return path;
        }

        @Override
        boolean shouldStop() {
            return true;
        }
    };

    abstract Path execute(Path path) throws IOException;

    abstract boolean accept(String command);

    void setParameters(String[] parameters) {
    }

    boolean shouldStop() {
        return false;
    }

    public static Command parseCommand(String commandToParse) {

        if (commandToParse.isBlank()) {
            throw new UnsupportedOperationException("Type something...");
        }

        final var possibleCommands = values();

        for (Command possibleCommand : possibleCommands) {
            if (possibleCommand.accept(commandToParse)) {
                possibleCommand.setParameters(commandToParse.split(" "));
                return possibleCommand;
            }
        }

        throw new UnsupportedOperationException("Can't parse command [%s]".formatted(commandToParse));
    }
}

package utils

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.FileVisitResult
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes

import static java.nio.file.FileVisitResult.*
import static java.nio.file.StandardCopyOption.*

class NioUtils {

    /**
     * Read file and return it as a string.
     */
    static String fileToString(Path file) {
        return new String(Files.readAllBytes(file), StandardCharsets.UTF_8)
    }

    /**
     * Writes the string to the specified file, truncating existing content.
     */
    static void stringToFile(String str, Path file) {
        Files.write(file, str.getBytes(StandardCharsets.UTF_8))
    }

    /**
     * Copies the source directory recursively to the target directory, preserving file attributes.
     */
    static void copyDirectory(Path source, Path target) {
        Files.walkFileTree(source, new SimpleFileVisitor<Path>() {

            @Override
            FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                Files.copy(dir, target.resolve(source.relativize(dir)), COPY_ATTRIBUTES)
                return CONTINUE
            }

            @Override
            FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.copy(file, target.resolve(source.relativize(file)), COPY_ATTRIBUTES)
                return CONTINUE
            }

        })
    }

    /**
     * Deletes the specified directory {@code Path} recursively.
     */
    static void deleteDirectory(Path directory) {
        if (Files.exists(directory)) {
            Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {

                @Override
                FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file)
                    return CONTINUE
                }

                @Override
                FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir)
                    return CONTINUE
                }

            })
        }
    }

    /**
     * Does simple string replacement in a directory, recursively
     */
    static void findAndReplaceInDir(String target, String replacement, Path directory) {
        Files.walk(directory).withCloseable { stream ->
            stream.filter { file ->
                return !Files.isDirectory(file)
            }.each { file ->
                String content = fileToString(file).replace(target, replacement)
                stringToFile(content, file)
            }
        }
    }

}

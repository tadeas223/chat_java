package org.messenger.fileCreation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

public class FileStore {
    FileCreator fileCreator;

    public FileStore(FileCreator creator) {
        this.fileCreator = creator;
    }

    public void store(String subdir, String filename, String base64) throws IOException {
        byte[] fileData = Base64.getDecoder().decode(base64);

        store(subdir, filename, fileData);
    }

    public void store(String subdir, String filename, byte[] bytes) throws IOException {
        Path subdirPath = Path.of(subdir);
        String relativeFilePath = subdirPath.resolve(filename).toString();
        if(!fileCreator.mkdir(subdir)) {
            throw new IOException("failed to create a directory: " + subdir);
        }
        if(!fileCreator.createFile(relativeFilePath)) {
            throw new IOException("failed to create a directory: " + subdir);
        }

        Path path = Path.of(fileCreator.getAbsolutePath(relativeFilePath));
        Files.write(path, bytes);
    }

    public byte[] readBytes(String subdir, String filename) throws IOException {
        Path subdirPath = Path.of(subdir);
        String relativeFilePath = subdirPath.resolve(filename).toString();

        Path path = Path.of(fileCreator.getAbsolutePath(relativeFilePath));
        return Files.readAllBytes(path);
    }

    public String readBase64(String subdir, String filename) throws IOException {
        byte[] bytes = readBytes(subdir, filename);
        return Base64.getEncoder().encodeToString(bytes);
    }
}

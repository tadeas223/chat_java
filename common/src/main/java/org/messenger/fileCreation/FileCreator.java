package org.messenger.fileCreation;

import javax.management.remote.JMXAddressable;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;

public class FileCreator {
    private String fileDir = "";

    public String getFileDir() {
        return fileDir;
    }

    public void setFileDir(String fileDir) {
        this.fileDir = fileDir;
    }

    public <T>FileCreator(Class<T> mainClass, String fileDir) {
        Path jarPath;
        try {
            jarPath =  Path.of(
                    mainClass
                            .getProtectionDomain()
                            .getCodeSource()
                            .getLocation()
                            .toURI()
            );
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        Path jarDir = jarPath.getParent();
        this.fileDir = jarDir.resolve(fileDir).toString();
        mkdir(this.fileDir);
    }

    public boolean mkdir(String relativePath){
        File file = new File(getAbsolutePath(relativePath));
        if(!file.exists()) {
            return file.mkdirs();
        } else {
            return true;
        }
    }

    public boolean createFile(String relativePath) throws IOException {
        File file = new File(getAbsolutePath(relativePath));
        return file.createNewFile();
    }

    public String getAbsolutePath(String relativePath){
        Path filePath = Path.of(fileDir );
        return filePath.resolve(relativePath).toString();
    }

}

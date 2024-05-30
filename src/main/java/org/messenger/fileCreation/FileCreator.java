package org.messenger.fileCreation;

import javax.management.remote.JMXAddressable;
import java.io.File;
import java.io.IOException;

public class FileCreator {
    private String fileDir = "";

    public String getFileDir() {
        return fileDir;
    }

    public void setFileDir(String fileDir) {
        this.fileDir = fileDir;
    }

    public FileCreator(String fileDir) {
        mkdir(fileDir);
        this.fileDir = fileDir;
    }

    public boolean mkdir(String relativePath){
        File file = new File(fileDir+relativePath);
        return file.mkdirs();
    }

    public boolean createFile(String relativePath) throws IOException {
        File file = new File(fileDir + relativePath);
        return file.createNewFile();
    }

    public String getAbsolutePath(String relativePath){
        return fileDir + relativePath;
    }

}

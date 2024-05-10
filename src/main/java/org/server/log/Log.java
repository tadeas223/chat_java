package org.server.log;

import java.io.PrintStream;

/**
 * Class used for logging messages.
 */
public class Log {
    private PrintStream fileStream = null;
    private PrintStream consoleStream = null;

    public Log(PrintStream fileStream, PrintStream consoleStream) {
        this.fileStream = fileStream;
        this.consoleStream = consoleStream;
    }

    public Log() {
    }

    /**
     * Prints the message to every {@link PrintStream}.
     * @param s
     */
    public void println(String s){
        if(fileStream != null) fileStream.println(s);
        if(consoleStream != null) consoleStream.println(s);
    }

    public PrintStream getFileStream() {
        return fileStream;
    }

    public void setFileStream(PrintStream fileStream) {
        this.fileStream = fileStream;
    }

    public PrintStream getConsoleStream() {
        return consoleStream;
    }

    public void setConsoleStream(PrintStream consoleStream) {
        this.consoleStream = consoleStream;
    }
}

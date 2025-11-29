import org.messenger.client.Client;
import org.messenger.protocol.ChatProtocolException;

import java.io.IOException;
import java.sql.SQLException;

public class TestMain {
    public static void main(String[] args) throws IOException, SQLException, ChatProtocolException {
        Client client = new Client("localhost");

        client.login("a", "");

        client.sendFile("/home/tad/Downloads/Java/chat_java/client/src/main/java/TestMain.java", "b");

        client.close();
    }
}

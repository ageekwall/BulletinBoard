import java.io.IOException;

public class TestClient {
    public static void main(String[] args) throws IOException {
        SocketConnection socketConnection = new SocketConnection(8000);
        String message = null;
        for(int i = 0; i < 7; i++) {
            message = "Post Articleposting" + i;
            ClientHelper.processMessage(socketConnection,message);
        }
        message = "Read";
        ClientHelper.processMessage(socketConnection, message);

        message = "Reply 1 ReplytoArticle0";
        ClientHelper.processMessage(socketConnection, message);

        message = "Reply 2 ReplyToArticle1";
        ClientHelper.processMessage(socketConnection,message);

        message = "Read";
        ClientHelper.processMessage(socketConnection, message);
    }

}

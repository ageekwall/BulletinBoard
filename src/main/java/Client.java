import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    //TODO: How do I connect to a particular server
    public static void main(String[] args) throws UnknownHostException {
        System.out.println("----******Welcome to the Bulletin Board******-----");
        System.out.println("Select any number to read more about the article");

        //TODO: Change the data sent based on the input from the user
        InetAddress host = InetAddress.getLocalHost();
        try {
            Socket socket = new Socket(host, 8000);
            String message = "Read";
            ClientHelper.sendMessageToServer(socket,message);
            ClientHelper.receiveMessageFromServer(socket);
            socket.close();

        } catch (IOException e) {
            System.out.println("Error occurred while communicating with the server");
        }

    }
}

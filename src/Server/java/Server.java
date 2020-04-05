import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

public class Server {

    Socket coordinatorSocket;
    static volatile HashMap<Integer, String> articleList;
    static volatile HashMap<Integer, ArrayList<Integer>> dependencyList;
    ServerSocket server;
    Consistency type;

    public Server(int port) {

        articleList = new HashMap<>();
        dependencyList = new HashMap<>();
        server = null;

        try {
            InetAddress host = InetAddress.getLocalHost();
            coordinatorSocket = new Socket(host, 8001);
            server = new ServerSocket(port);
            type = ServerHelper.getConsistencyType(coordinatorSocket);
            System.out.println(type.toString());
            while(true) {

                Socket client = null;
                try {
                    client = server.accept();

                    Thread clientResponder = new ClientResponder(client, coordinatorSocket);
                    clientResponder.start();

                } catch (IOException e) {
                    client.close();
                    System.out.println("Error in the server sockets while accepting clients");
                }
            }

        } catch (UnknownHostException e) {
            System.out.println("Couldn't get the host of the Server");
        } catch (IOException e) {
            System.out.println("Couldn't create connection to the Coordinator ");
        }
    }
    public static void main(String[] args)  {

        Server server = new Server(8000);
    }
}

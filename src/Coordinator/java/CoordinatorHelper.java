import javafx.util.Pair;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

public class CoordinatorHelper {

    public static ArrayList<String> getServerIPAndPort() throws IOException {
        ArrayList<String> listOfServers = new ArrayList<>();
        File file = new File("src/serverList.properties");
        FileInputStream fileInputStream = new FileInputStream(file);
        Properties prop = new Properties();
        prop.load(fileInputStream);
        Enumeration enumeration = prop.keys();
        listOfServers.add(prop.getProperty("server1").split(":")[1]);
        listOfServers.add(prop.getProperty("server2").split(":")[1]);
        listOfServers.add(prop.getProperty("server3").split(":")[1]);
        listOfServers.add(prop.getProperty("server4").split(":")[1]);

        return listOfServers;
    }

    public static Pair<String, HashMap<Integer, ArrayList<Integer>>> receiveMessageFromServer(Socket socket, int ID) {
        String result = null;
        int latestID;
        String[] messageReceived = null;
        HashMap<Integer, ArrayList<Integer>> dependencyList = null;
        ArrayList<Integer> childList = null;
        try {
            System.out.println("Server at: " + socket.getPort());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            dependencyList = (HashMap) objectInputStream.readObject();
            String message = (String) objectInputStream.readObject();

            // Gets the latest ID depending on whether it is post or reply.
            messageReceived = message.split("-");
            if(messageReceived.length >= 1){
                latestID = getLatestID(socket, messageReceived[0], ID);
                result = latestID + " " + messageReceived[0];
            }
            else{
                latestID = getLatestID(socket, messageReceived[0], ID);
                result = latestID + " " + messageReceived[0] + " " + messageReceived[1];
                childList = dependencyList.get(messageReceived[0]);
                childList.add(latestID);
                dependencyList.put(Integer.parseInt(messageReceived[0]), childList);
            }

            System.out.println("The latest ID is: " + latestID);

        } catch (IOException e) {
            System.out.println("Error while receiving message from Server");
        } catch (ClassNotFoundException e) {
            System.out.println("Couldn't read object from server");
        }

        Pair<String, HashMap<Integer, ArrayList<Integer>>> pair = new Pair<String, HashMap<Integer, ArrayList<Integer>>>(result, dependencyList);
        return pair;
    }

    public static int getLatestID(Socket socket, String message, int ID){
        int latestID = ID + 1;
        return latestID;
    }

    public static void broadcastMessageToServers(String message, HashMap<Integer, ArrayList<Integer>> dependencyList) {
        try {
            for(Socket server : Coordinator.serverSockets){
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(server.getOutputStream());
                objectOutputStream.writeObject(message);
                objectOutputStream.writeObject(dependencyList);
                System.out.println("Sent to Socket");
            }
        } catch (IOException e) {
            System.out.println("Problem broadcasting to servers from coordinator");
        }

    }

    public static HashMap<String,Integer> getReadAndWriteServers() throws IOException {
        HashMap<String,Integer> readWriteServers = new HashMap<String,Integer>();
        File file = new File("src/serverList.properties");
        FileInputStream fileInputStream = new FileInputStream(file);
        Properties prop = new Properties();
        prop.load(fileInputStream);
        Enumeration enumeration = prop.keys();
        readWriteServers.put("Read", Integer.parseInt(prop.getProperty("numberOfReadServers")));
        readWriteServers.put("Write", Integer.parseInt(prop.getProperty("numberOfWriteServers")));

        return readWriteServers;
    }

    public static boolean validReadWriteServerValues(Integer read, Integer write, Integer N) throws IOException {
        if((read + write > N) && (write > N/2))
            return true;
        return false;
    }

    public static void sendConsistencyTypeToServers(Socket socket, String consistency){
        System.out.println("Sending consistency to Server");
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(consistency);
        } catch (IOException e) {
            System.out.println("Couldn't send Consistency type to server at port " + socket.getPort());
        }

    }

    public static Consistency getConsistencyType(String consistency){
        switch (consistency) {
            case "Seq":
                return Consistency.SEQUENTIAL;
            case "Quo":
                return Consistency.QUORUM;
            case "RYW":
                return Consistency.READ_YOUR_WRITE;
            case "Exit":
                System.out.println("Bye Bye");
                return Consistency.ERROR;
            default:
                System.out.println("Invalid Input");
                return Consistency.EXIT;
        }
    }
}

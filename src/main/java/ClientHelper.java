import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class ClientHelper {
    public static void sendMessageToServer(SocketConnection socket, String[] message, int flag) throws IOException {
        socket.getOos().writeObject(message);
        System.out.println("Request Sent to Server");
        System.out.println("Please wait as your request is being processed.........");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            System.out.println("Thread is not waking up");
        }
        //Choose
        if(flag==1)
            receiveMessageFromServer(socket, 0);
        //Read
        else if(flag==2)
            receiveMessageFromServer(socket, 1);
    }

    public static void receiveMessageFromServer(SocketConnection socket, int flag) {

        HashMap<Integer, String> articleList = null;
        HashMap<Integer, ArrayList<Integer>> dependencyList = null;
        String choose;
        try {

            ObjectInputStream in = socket.getOis();
            if(flag==0){
                choose= (String) in.readObject();
                System.out.println("The article is: " + choose);
            }
            else if(flag==1){
                articleList = (HashMap) in.readObject();
                dependencyList = (HashMap) in.readObject();
                System.out.println("Succesfully received messages from Server");
                readArticles(articleList, dependencyList);
            }


        } catch (IOException e1) {
            System.out.println("Error while receiving message from Server");
        } catch ( ClassNotFoundException e){
            System.out.println("Another Exception this time");
        }

    }

    public static void readArticles(HashMap<Integer, String> articleList, HashMap<Integer, ArrayList<Integer>> dependencyList) {
        int i = 1;
        boolean[] visitedArray = new boolean[articleList.size()];
        result = "";
        System.out.println();
        System.out.println("***********BULLETIN BOARD CONTENTS*************");
        createString(articleList, dependencyList,visitedArray, i,0);
        System.out.println();
    }
    public static int printFive(int i) {
        String [] r = result.split("\n");
        if(i >= r.length) return -1;
        int c = i+5;
        for(int j = i; j < r.length; j++){
            System.out.println(r[j]);
            if(j == c-1) {
                return c;
            }

        }
        return -1;
    }

    static String result = "";
    private static void createString(HashMap<Integer, String> articleList, HashMap<Integer, ArrayList<Integer>> dependencyList, boolean[] visitedArray, int index, int spaces) {

        if(index > articleList.size()){
            return;
        }
        if(visitedArray[index - 1] == false){
            ///=   System.out.println(index +". " +articleList.get(index));
            result += index+". "+ articleList.get(index)+"\n";
        }
        ArrayList<Integer> childList = dependencyList.get(index);
        visitedArray[index - 1] = true;
        if(childList == null || childList.size() == 0){
            createString(articleList, dependencyList,visitedArray, index + 1, spaces);
        }
        else{
           // System.out.print("\t");
            for(int i = 0; i < childList.size(); i++){
                if(visitedArray[childList.get(i) - 1] == false){
                    for(int t = 0; t <=spaces; t++) {
                        //System.out.print("\t");
                        result += "\t"; }
                  //  System.out.print(childList.get(i) +". " +articleList.get(childList.get(i)));
                    result += childList.get(i)+". "+articleList.get(childList.get(i));
                 //   System.out.println();
                    result += "\n";
                    visitedArray[childList.get(i) - 1] = true;
                    if(dependencyList.get(childList.get(i)) == null) continue;
                    createString(articleList,dependencyList,visitedArray,childList.get(i), spaces+1);

                }
            }
            createString(articleList, dependencyList,visitedArray, index + 1, spaces);
        }
    }

    public static void processMessage(SocketConnection socket, String message ) throws IOException {
        String[] messageList = message.split(" ");
        switch (messageList[0]){
            case "Read":
                sendMessageToServer(socket, messageList, 2);
                int ind = printFive(0);

                if(ind == -1 ) break;
                while(ind > 0) { System.out.println("........");
                System.out.println("Enter 'Yes' for Next Five Articles or Enter 'No' \n");
                Scanner in = new Scanner(System.in);
                String ch = in.nextLine();
                if(ch.equals("Yes") )
                    ind = printFive(ind);
                else
                    ind = -1;
                }

            break;
            case "Choose": sendMessageToServer(socket, messageList, 1);break;
            case "Post": //sendMessageToServer(socket,messageList,0);
            case "Reply":
                sendMessageToServer(socket, messageList, 0);break;
            case "exit":
                System.out.println("Exiting from the program");
                socket.close(); break;
            default:
                System.out.println("Invalid");
        }
    }
}

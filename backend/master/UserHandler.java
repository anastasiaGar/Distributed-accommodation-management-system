package backend.master;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class UserHandler implements Runnable {

    @Override
    public void run() {
        try (ServerSocket userserverSocket = new ServerSocket(8888)) {
            System.out.println("Master > UserHandler | UserHandler is running...");

            while (true) {
                Socket userSocket = userserverSocket.accept();
                System.out.println("Master > UserHandler | Connection received from User: " + userSocket.getRemoteSocketAddress());


                // Start a new thread to handle the connection.
                Thread t = new Thread(new UserConnectionThread(userSocket));
                t.start();
                
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port: 8888.");
            e.printStackTrace();
            System.exit(1);
        }
    }
}

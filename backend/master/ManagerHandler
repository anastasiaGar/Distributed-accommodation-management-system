package backend.master;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class ManagerHandler implements Runnable {

    @Override
    public void run() {
        try (ServerSocket managerserverSocket = new ServerSocket(11111)) {
            System.out.println("Master > ManagerHandler | ManagerHandler is running...");

            while (true) {
                Socket managerSocket = managerserverSocket.accept();
                System.out.println("Master > ManagerHandler | Connection received from Manager: " + managerSocket.getRemoteSocketAddress());

                // Start a new thread to handle the connection.
                Thread t = new Thread(new ManagerConnectionThread(managerSocket));
                t.start();

            }
        } catch (IOException e) {
            System.err.println("Could not listen on port: 9003.");
            e.printStackTrace();
            System.exit(1);
        }
    }
}

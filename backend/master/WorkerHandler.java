package backend.master;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class WorkerHandler implements Runnable {
    private int workerCount=0;
    @Override
    public void run() {
        try (ServerSocket workerserverSocket = new ServerSocket(55555)) {

            System.out.println("Master > WorkerHandler | WorkerHandler is running...");
            while (true) {

                Socket workerSocket = workerserverSocket.accept();
                System.out.println("Master > WorkerHandler| Connection received from Worker: " + workerSocket.getRemoteSocketAddress());
                
                // Increment worker count
                workerCount++;

                    //Evgala to if
                    // Start a new thread to handle the connection after all workers have connected.
                    Thread t = new Thread(new WorkerConnectionThread(workerSocket));
                    t.start();
                
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port: 55555.");
            e.printStackTrace();
            System.exit(1);
        }
    }
}

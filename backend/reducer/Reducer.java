package backend.reducer;

import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;


public class Reducer implements Runnable {
    
    @Override
    public void run() {
        try (ServerSocket workerserverSocket = new ServerSocket(9011)) {

            System.out.println("ReducerInit > Reducer | Reducer is running...");
            
            while (true) {
                Socket workerSocket = workerserverSocket.accept();
                
                System.out.println("ReducerInit > Reducer | Connection received from Worker: " + workerSocket.getRemoteSocketAddress());

                // Start a new thread to handle the connection after all workers have connected.
                Thread t = new Thread(new ReducerThread(workerSocket));
                t.start();

            }
        } catch (IOException e) {
            System.err.println("Could not listen on port: 55555.");
            e.printStackTrace();
            System.exit(1);
        }
    }
}

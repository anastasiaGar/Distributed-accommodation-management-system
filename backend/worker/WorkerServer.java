package backend.worker;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.example.client_frontend.Room;
import com.example.client_frontend.Filter;

public class WorkerServer implements Runnable {

    private static final Object lock = new Object();
    private Filter sharedFilter; // Shared filter object

    @Override
    public void run() {
        try (ServerSocket userServerSocket2 = new ServerSocket(5003)) {
            System.out.println("WorkerServerInit > WorkerServer | WorkerServer  is running...");

            while (true) {
                synchronized (lock) {
                    Socket userSocket2 = userServerSocket2.accept();
                    ObjectOutputStream out = new ObjectOutputStream(userSocket2.getOutputStream());
                    ObjectInputStream in = new ObjectInputStream(userSocket2.getInputStream());
                    
                    sharedFilter = (Filter) in.readObject();

                    System.out.println("WorkerServerInit > WorkerServer | Filter received from User:");
                    System.out.println(sharedFilter);

                    ExecutorService executor = Executors.newFixedThreadPool(3); // Only one thread for processing

                    // Submit task to the executor
                    List<Future<List<Room>>> futures = new ArrayList<>();

                    // Submit three tasks to the executor
                    for (int i = 1; i <= 3; i++) {
                        futures.add(executor.submit(new WorkerServerThread(sharedFilter,i)));

                    }

                    // Collect results from each thread
                    List<List<Room>> results = new ArrayList<>();
                    for (Future<List<Room>> future : futures) {
                        try {
                            results.add(future.get());
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                    }

                    // Process results as needed
                    System.out.println("WorkerServerInit > WorkerServer | Workers lists of rooms, after searching for the given filter:");
                    int i=1;
                    for(List<Room> list: results){
                        System.out.println("Worker #"+i+": "+list);
                        i++;
                    }

                    // Shutdown executor
                    executor.shutdown();
                    Thread workerreducer = new Thread(new WorkerReducerThread(out,results));
                    workerreducer.start();
                    
                }

            }
        } catch (IOException e) {
            System.err.println("Could not listen on port: 5003.");
            e.printStackTrace();
            System.exit(1);
        } catch (ClassNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }
}

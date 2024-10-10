package backend.reducer;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import com.example.client_frontend.Room;
import com.example.client_frontend.Filter;
import backend.worker.*;

public class ReducerThread implements Runnable {

    private List<List<Room>>  filteredRooms;

    private Socket workerSocket;

    

    public ReducerThread(Socket workerSocket) {
        this.workerSocket = workerSocket;

    }

    @Override
    public void run() {
        try {
            ObjectInputStream in = new ObjectInputStream(workerSocket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(workerSocket.getOutputStream());
           
            //pairnoume to id
            filteredRooms = (List<List<Room>>) in.readObject();

            
            // Populate your nested list here
            
            List<Room> finalresults = flatten(filteredRooms);
            System.out.println("ReducerInit > Reducer > ReducerThread | Flattened the list of lists of rooms");
        

            out.writeObject(finalresults);
            out.flush();
            System.out.println("ReducerInit > Reducer > ReducerThread | Sent final flattened list to WorkerReducerThread");

            // Close streams and socket
            in.close();
            out.close();
            workerSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public static List<Room> flatten(List<List<Room>> nestedList) {
        List<Room> flattenedList = new ArrayList<>();
        flattenHelper(nestedList, flattenedList);
        return flattenedList;
    }
    private static void flattenHelper(List<List<Room>> nestedList, List<Room> result) {
        for (List<Room> innerList : nestedList) {
            result.addAll(innerList);
        }
    }

    
}

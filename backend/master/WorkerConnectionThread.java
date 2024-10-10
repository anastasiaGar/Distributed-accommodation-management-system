package backend.master;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.client_frontend.Room;

public class WorkerConnectionThread implements Runnable {

    private final Socket workerSocket;

    public WorkerConnectionThread(Socket workerSocket) {
        this.workerSocket = workerSocket;
    }

    @Override
    public void run() {
        try {
            ObjectInputStream in = new ObjectInputStream(workerSocket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(workerSocket.getOutputStream());
            
            int wid = (int) in.readObject();
            List<Room> recievedrooms = ManagerConnectionThread.getReceivedRooms();
            List<Room> workeRooms = getRoomsForWorker(recievedrooms,wid);
            
            
            out.writeObject(workeRooms);
            out.flush();


            // Close streams and socket
            in.close();
            out.close();
            workerSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            
            e.printStackTrace();
        }
    }

    
    private List<Room> getRoomsForWorker(List<Room> rooms, int targetWorkerId) {
        List<Room> workerRooms = new ArrayList<>();
        int numWorkers = 3; 
    
        for (Room room : rooms) {
            int workerId = Math.abs(room.getRoomName().hashCode()) % numWorkers + 1; // Calculate worker ID based on room name hash
            if (workerId == targetWorkerId) {
                workerRooms.add(room);
            }
        }
    
        return workerRooms;
    }

     
   
}

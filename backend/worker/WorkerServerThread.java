package backend.worker;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.example.client_frontend.Room;
import com.example.client_frontend.Filter;


public class WorkerServerThread implements Callable<List<Room>> {
    
   
    private List<Room> workerroom;
    private Filter filter;
    private int wid;
    private static final Object lock = new Object();

    public WorkerServerThread(Filter filter,int i) {
        this.filter = filter;
        this.wid = i;
        //this.id = i;
    }
    public List<Room> filterRooms( Filter filter, List<Room> workersroom) {
        List<Room> filteredRooms = new ArrayList<>();

        for (Room room : workersroom) {
            if ((filter.getArea().isEmpty() || room.getArea().equals(filter.getArea())) &&
                    room.getStars() >= filter.getStars() &&
                    (filter.getStartDate() == null || room.getStartDate().isEqual(filter.getStartDate()) || room.getStartDate().isAfter(filter.getStartDate())) &&
                    (filter.getEndDate() == null || room.getEndDate().isEqual(filter.getEndDate()) || room.getEndDate().isBefore(filter.getEndDate()))) {
                // Room matches the filter criteria, add it to the filtered list
                filteredRooms.add(room);
            }
        }

        return filteredRooms;
    }

    @Override
    public List<Room> call() throws Exception {
        

            
            // Read distributed rooms from master
            try (Socket workersocket = new Socket("localhost", 55555)) {
                ObjectOutputStream out1 = new ObjectOutputStream(workersocket.getOutputStream());
                ObjectInputStream in1 = new ObjectInputStream(workersocket.getInputStream());
               
              
                // Request distributed rooms from master
                out1.writeObject(wid);
                out1.flush();

                // Receive distributed rooms from master
                List<Room> recievedworkerroom = (List<Room>) in1.readObject();


                synchronized (lock){
                    System.out.println("WorkerServerInit > WorkerServer > WorkerServerThread | Worker with id #" + wid+" has been assigned the following rooms:");
                    for(Room room: recievedworkerroom){
                        System.out.println(room);
                    }
                }


                workerroom = filterRooms(filter, recievedworkerroom);


                in1.close();
                out1.close();
            } catch (ClassNotFoundException | IOException e) {
                throw new RuntimeException(e);
            }
            // Perform any necessary operations with distributed rooms

        // Return the result (List of rooms)
        return workerroom;
    }
}

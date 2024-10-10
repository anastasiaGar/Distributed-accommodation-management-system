package backend.worker;
import com.example.client_frontend.Room;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class WorkerReducerThread implements Runnable {

    
    private List<List<Room>> results;
    private List<Room> finaList;
    private ObjectOutputStream outred;
  

    public WorkerReducerThread(ObjectOutputStream outred, List<List<Room>> results) {
        this.outred = outred;
        this.results = results;
        
        
    }

    @Override
    public void run() {
        
        try (Socket reducersocket = new Socket("localhost", 9011);
                    
            ObjectOutputStream out = new ObjectOutputStream(reducersocket.getOutputStream());
            ObjectInputStream in= new ObjectInputStream(reducersocket.getInputStream())){
            
            out.writeObject(results);
            out.flush();

            try {
                finaList = (List<Room>) in.readObject();
                System.out.println("WorkerServerInit > WorkerServer > WorkerReducerThread | The final list, after reducing the results from each worker:");
                for(Room room: finaList){
                    System.out.println(room);
                }

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            out.close();
            in.close();
            reducersocket.close();

            outred.writeObject(finaList);
            
            

            
        }catch (IOException e) {
         e.printStackTrace();
        }
        
        

    }
}

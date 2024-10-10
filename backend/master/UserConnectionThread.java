package backend.master;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;


import com.example.client_frontend.Filter;
import com.example.client_frontend.Room;

public class UserConnectionThread implements Runnable {

    private final Socket userSocket;
    private List<Room> response;
    public static Filter filter;
   

    public UserConnectionThread(Socket userSocket) {
        this.userSocket = userSocket;
    }

    @Override
    public void run() {

        

        try {

            ObjectInputStream in = new ObjectInputStream(userSocket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(userSocket.getOutputStream());


            filter = (Filter) in.readObject();
            System.out.println("Master > UserHandler > UserConnectionThread | Filter:");
            System.out.println(filter);



                // Connect to Master server
                try (Socket socket = new Socket("localhost", 5003);
                    
                ObjectOutputStream outred = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream inred = new ObjectInputStream(socket.getInputStream()))
                {
                   

                  
                        
                    // Send rooms to Master
                    outred.writeObject(filter);
                    outred.flush();

                    System.out.println("Master > UserHandler > UserConnectionThread | Filter sent to Worker-Server");

                    // Receive response from Reducer
                    response = (List<Room>) inred.readObject();
                        
                    
                    inred.close();
                    outred.close();
                    socket.close();


                 } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                }
            
                out.writeObject(response);
                out.flush();


            


            // Close streams and socket
            in.close();
            out.close();
            userSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public static Filter getFilter() {
        return filter;
    }

    public static Filter getUserFilter() {
        return filter;
    }
}

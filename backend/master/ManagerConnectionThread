
package backend.master;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import com.example.client_frontend.Room;

public class ManagerConnectionThread implements Runnable {

    private final Socket managerSocket;
    private static List<Room> receivedRooms;

    private boolean roomsPrinted; // Flag to indicate whether rooms have been printed before
    private Object lock =new Object();
    private List<Room> managerrooms;

    public ManagerConnectionThread(Socket managerSocket) {
        this.managerSocket = managerSocket;
    }

    @Override
    public synchronized void run() {

        try (ObjectInputStream in = new ObjectInputStream(managerSocket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(managerSocket.getOutputStream())) {

            while (!managerSocket.isClosed()) {
                roomsPrinted = false;
                // Receive rooms from Manager
                synchronized(lock){
                    managerrooms = (List<Room>) in.readObject();
                    if(managerrooms!=null){
                        receivedRooms = managerrooms;
                    }else{
                        managerSocket.close();
                        break;

                    }

                }



                // Check if rooms have been printed before
                if (!roomsPrinted) {
                    // Print received rooms
                    System.out.println("Master > ManagerHandler > ManagerConnectionThread | Received rooms from Manager:");
                    System.out.println("{");
                    for (Room room : receivedRooms) {
                        System.out.println(room.toString());
                    }
                    System.out.println("}");

                    // Update the flag to indicate that rooms have been printed
                    roomsPrinted = true;

                    // Send response back to client
                    out.writeObject("Rooms received by Master !");
                } else {
                    // Send response indicating that rooms were already received
                    out.writeObject("These rooms were already received !");
                }
                out.flush();


            }
        } catch (EOFException e) {
            // Handle EOFException if necessary
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                // Close socket

                managerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Getter method for receivedRooms
    public static List<Room> getReceivedRooms() {
        return receivedRooms;
    }
}

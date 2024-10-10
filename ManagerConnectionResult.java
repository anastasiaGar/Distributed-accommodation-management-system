package backend.manager;
import java.io.*;
import java.net.Socket;

public class ManagerConnectionResult {


    private static ObjectOutputStream out;
    private static ObjectInputStream in;
    private static Socket socket;

    public ManagerConnectionResult(Socket socket, ObjectOutputStream out, ObjectInputStream in) {
        this.out = out;
        this.in = in;
        this.socket=socket;
    }

    public static ObjectOutputStream getOut() {
        return out;
    }

    public static ObjectInputStream getIn() {
        return in;
    }
    public static Socket getSocket(){
        return socket;
    }
}
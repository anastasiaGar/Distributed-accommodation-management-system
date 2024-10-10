package backend.worker;

public class WorkerServerInit  {
    public static void main(String[] args) {
       
        Thread t5 = new Thread(new WorkerServer());
        t5.start();
        try {
            t5.join();


        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

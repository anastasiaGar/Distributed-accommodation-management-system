package backend.master;




import backend.reducer.Reducer;
import backend.worker.WorkerServer;


public class Master {

    public Master() {
    }

    public void init() {

        // Start the three threads and wait for them to finish
        Thread t1 = new Thread(new ManagerHandler());
        Thread t2 = new Thread(new WorkerHandler());
        Thread t3 = new Thread(new UserHandler());
      
       

        t1.start();
        t2.start();
        t3.start();
        
        
        try {
            t1.join();
            t2.join();
            t3.join();
            
            

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        new Master().init();
    }
}

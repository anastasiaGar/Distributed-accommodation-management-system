package backend.reducer;

public class ReducerInit  {
    public static void main(String[] args) {
       
        Thread t5 = new Thread(new Reducer());
        t5.start();
        try {
            t5.join();


        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

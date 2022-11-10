public class Bakery extends Thread {


    public int thread_id;
    public static final int numberOfThreads = 7;
    public static volatile int meals = 3;
    public static volatile int count = meals;

    private static volatile boolean[] choosing = new boolean[numberOfThreads];
    private static volatile int[] ticket = new int[numberOfThreads]; // pentru prioritate


    public Bakery(int id) {
        thread_id = id;
    }

    public void run() {
        int scale = 2;
        lock(thread_id);
        // critical section
        if(count != 0)
            count = count - 1;
        else
            count += meals;
        if (count == meals)
            count -= 1;
        System.out.println("I am thread " + thread_id);
        System.out.println("there are  "+ count + " meals left");

        try {
            sleep((int) (Math.random() * scale));
        } catch (InterruptedException e) { }
        // End of critical section.
        unlock(thread_id);



    }

    public void lock(int id) {
        // thread-ul cu id-ul = id vrea sa intre in sectiunea critica
        choosing[id] = true;
        ticket[id] = findMax() + 1;
        choosing[id] = false;

        for (int j = 0; j < numberOfThreads; j++) {

            // daca j este thread-ul curent, mergem la urmatorul thread
            if (j == id)
                continue;
            while (choosing[j]) { }

            while (ticket[j] != 0 && (ticket[id] > ticket[j] || (ticket[id] == ticket[j] && id > j))) { }

        }
    }

    private void unlock(int id) {
        ticket[id] = 0;
    }


    private int findMax() {
        int m = ticket[0];
        for (int i = 1; i < ticket.length; i++) {
            if (ticket[i] > m)
                m = ticket[i];
        }
        return m;
    }

    public static void main(String[] args) {

        for (int i = 0; i < numberOfThreads; i++) {
            choosing[i] = false;
            ticket[i] = 0;
        }

        Bakery[] threads = new Bakery[numberOfThreads];

        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Bakery(i);
            threads[i].start();
        }

        for (int i = 0; i < threads.length; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }

}
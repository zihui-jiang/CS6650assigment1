package client1;
import io.swagger.client.ApiClient;

import java.util.concurrent.*;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author jiang
 * @create 6/3/24
 * @since 1.0.0
 */
public class Main {
    private static String PATH = "http://34.219.56.17:8080/server-1.0-SNAPSHOT";
    public static void main(String[] args) throws InterruptedException {
        int initialThreads = 32;
        int numberOfThreadsInPool = 10;
        int numRequests = 200000;
        int requestsPerThread = 1000;


        // ---------------- Experiment: stage 1 ------------------
        System.out.println("---------------- Stage 1 starts ----------------");
        BlockingQueue<SkierEvent> eventQueue = new LinkedBlockingQueue<>();
//        System.out.println(Runtime.getRuntime().availableProcessors());
        // generate 200000 post request using a single thread
        SkierEventGenerator generator = new SkierEventGenerator(eventQueue, numRequests);
        Thread generatorThread = new Thread(generator);
        generatorThread.start();
        generatorThread.join(); // Ensure the generator completes before proceeding

        System.out.println("Creating skier events completed: " + eventQueue.size());


        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreadsInPool);
        int remainingRequests = numRequests - (initialThreads * requestsPerThread);
        int additionalThreads = (remainingRequests + requestsPerThread - 1)/requestsPerThread;
        CountDownLatch latch1 = new CountDownLatch(1);

        long startTime = System.currentTimeMillis();
        // perform 32 threads first
        for (int i = 0; i < initialThreads; i++) {
            ApiClient apiClient = new ApiClient();
            apiClient.setBasePath(PATH);
            Client client1 = new Client(eventQueue, latch1, requestsPerThread, apiClient);
            executor.execute(client1);
        }

//        while(latch1.getCount() >= initialThreads) {}
        latch1.await();

        // ---------------- Experiment: stage 2 ------------------
        System.out.println("---------------- Stage 2 starts ----------------");

        CountDownLatch latch2 = new CountDownLatch(additionalThreads);
        for (int i = 0; i < additionalThreads; i++) {
            ApiClient apiClient = new ApiClient();
            apiClient.setBasePath(PATH);
            Client client1 = new Client(eventQueue, latch2, requestsPerThread, apiClient);
            executor.execute(client1);
        }


        latch2.await();
//        long endTime = System.currentTimeMillis();

        long endTime = System.currentTimeMillis();
        executor.shutdown();

        System.out.println("The number of threads in the fixed thread pool is " + numberOfThreadsInPool);
        System.out.println("Number of successful requests sent: " + Client.getSuccessfulRequests());
        System.out.println("Number of unsuccessful requests: " + Client.getFailedRequests());
        System.out.println("Wall time (ms): " + (endTime - startTime));
        double totalTP2 = (double)(numRequests) / ((double)(endTime - startTime) / 1000.0);
        System.out.println("Stage2: Total Throughput (requests/sec) is : " + totalTP2);

    }

}

package client2;

import client1.Client;
import client1.SkierEvent;
import client1.SkierEventGenerator;

import java.util.concurrent.*;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author jiang
 * @create 6/3/24
 * @since 1.0.0
 */
public class Main2 {
    public static void main(String[] args) throws InterruptedException {
        int initialThreads = 32;
        int numRequests = 200000;
        int requestsPerThread = 1000;

        // ---------------- Experiment: stage 1 ------------------
        System.out.println("---------------- Stage 1 starts ----------------");
        BlockingQueue<SkierEvent> eventQueue = new LinkedBlockingQueue<SkierEvent>();

        // generate 200000 post request using a single thread
        SkierEventGenerator generator = new SkierEventGenerator(eventQueue, numRequests);
        Thread generatorThread = new Thread(generator);
        generatorThread.start();
        generatorThread.join(); // Ensure the generator completes before proceeding

        System.out.println("Creating skier events completed: " + eventQueue.size());


        ExecutorService executor = Executors.newFixedThreadPool(initialThreads);
        int remainingRequests = numRequests - (initialThreads * requestsPerThread);
        int additionalThreads = (remainingRequests + requestsPerThread - 1)/requestsPerThread;
        CountDownLatch latch1 = new CountDownLatch(initialThreads);
        CountDownLatch latch2 = new CountDownLatch(additionalThreads);

        long startTime = System.currentTimeMillis();
        // perform 32 threads first
        for (int i = 0; i < initialThreads; i++) {
            Client2 client1 = new Client2(eventQueue, latch1, requestsPerThread);
            executor.execute(client1);
        }

        while(latch1.getCount() >= initialThreads) {}

        // ---------------- Experiment: stage 2 ------------------
        System.out.println("---------------- Stage 2 starts ----------------");

        for (int i = 0; i < additionalThreads; i++) {
            Client2 client1 = new Client2(eventQueue, latch2, requestsPerThread);
            executor.execute(client1);
        }

        latch1.await();
        latch2.await();
        long endTime = System.currentTimeMillis();


        executor.shutdown();
        System.out.println("Stage2 terminates, the number of threads is: " + additionalThreads);
        System.out.println("Number of successful requests sent: " + Client2.getSuccessfulRequests());
        System.out.println("Number of unsuccessful requests: " + Client2.getFailedRequests());
        System.out.println("Total run time (ms): " + (endTime - startTime));
        double totalTP2 = (double)(numRequests) / ((double)(endTime - startTime) / 1000.0);
        System.out.println("Stage2: Total Throughput (requests/sec) is : " + totalTP2 + ", average throughput is " + totalTP2/(initialThreads));

        // Write metrics to CSV
        Metric.writeMetricsToCSV(Client2.metrics, "metrics.csv");

        // Process metrics
        Metric.processMetrics(Client2.metrics);

    }

}

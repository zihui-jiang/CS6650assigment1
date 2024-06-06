package client2;

import client1.Client;
import client1.SkierEvent;
import client1.SkierEventGenerator;
import io.swagger.client.ApiClient;

import java.util.ArrayList;
import java.util.List;
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
    private static String PATH = "http://34.219.56.17:8080/server-1.0-SNAPSHOT";
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


        ExecutorService executor = Executors.newFixedThreadPool(10);
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        int remainingRequests = numRequests - (initialThreads * requestsPerThread);
        int additionalThreads = (remainingRequests + requestsPerThread - 1)/requestsPerThread;
        CountDownLatch latch1 = new CountDownLatch(initialThreads);
        CountDownLatch latch2 = new CountDownLatch(additionalThreads);

        long startTime = System.currentTimeMillis();
        List<Throughput> throughputs = new ArrayList<>();

        // Scheduled task to print throughput every second
        Runnable throughputTask = () -> {
            int sent = Client2.getSuccessfulRequests();
            long elapsed = System.currentTimeMillis() - startTime;
            double throughput = (double) sent / (elapsed / 1000.0);
            throughputs.add(Throughput.builder().currTime(elapsed).throughput(throughput).build());
            System.out.println("Time: " + elapsed + " ms, Requests Sent: " + sent + ", Throughput: " + throughput + " requests/sec");
        };
        scheduler.scheduleAtFixedRate(throughputTask, 1, 10, TimeUnit.SECONDS);


        // perform 32 threads first
        for (int i = 0; i < initialThreads; i++) {
            ApiClient apiClient = new ApiClient();
            apiClient.setBasePath(PATH);

            Client2 client1 = new Client2(eventQueue, latch1, requestsPerThread, apiClient);
            executor.execute(client1);
        }

        while(latch1.getCount() >= initialThreads) {}

        // ---------------- Experiment: stage 2 ------------------
        System.out.println("---------------- Stage 2 starts ----------------");

        for (int i = 0; i < additionalThreads; i++) {
            ApiClient apiClient = new ApiClient();
            apiClient.setBasePath(PATH);
            Client2 client1 = new Client2(eventQueue, latch2, requestsPerThread, apiClient);
            executor.execute(client1);
        }

        latch1.await();
        latch2.await();
        long endTime = System.currentTimeMillis();


        executor.shutdown();
        System.out.println("The number of threads in the fixed thread pool is " + 10);
        System.out.println("Number of successful requests sent: " + Client2.getSuccessfulRequests());
        System.out.println("Number of unsuccessful requests: " + Client2.getFailedRequests());
        System.out.println("Total run time (ms): " + (endTime - startTime));
        double totalTP2 = (double)(numRequests) / ((double)(endTime - startTime) / 1000.0);
        System.out.println("Stage2: Total Throughput (requests/sec) is : " + totalTP2);

        // Write metrics to CSV
        Metric.writeMetricsToCSV(Client2.metrics, "metrics.csv");

        // Process metrics
        Metric.processMetrics(Client2.metrics);

        Throughput.writeToCSV(throughputs);

    }

}

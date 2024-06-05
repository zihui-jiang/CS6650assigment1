/**
 * Copyright (C), 2015-2024, XXX有限公司
 * FileName: PredictClient
 * Author:   jiang
 * Date:     6/5/24 11:23 AM
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package client1;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author jiang
 * @create 6/5/24
 * @since 1.0.0
 */
public class PredictClient {
    public static void main(String[] args) throws InterruptedException {
        // ----------- Little's law prediction ------------
        int requestForPredict = 1000;
        BlockingQueue<SkierEvent> eventQueuePredict = new LinkedBlockingQueue<SkierEvent>();
        SkierEventGenerator generatorPredict = new SkierEventGenerator(eventQueuePredict, requestForPredict);
        Thread generatorThreadPredict = new Thread(generatorPredict);

        generatorThreadPredict.start();
        generatorThreadPredict.join(); // Ensure the generator completes before proceeding

        System.out.println("Creating skier events completed: " + eventQueuePredict.size());

        long startTimePredict = System.currentTimeMillis();
        CountDownLatch latch = new CountDownLatch(1);

        Client client = new Client(eventQueuePredict, latch, requestForPredict);
        Thread clientThread = new Thread(client);
        clientThread.start();
        clientThread.join(); // Wait for the client to finish all requests
        latch.await();

        long endTimePredict = System.currentTimeMillis();
        double averageLatency = (endTimePredict - startTimePredict) / (double)requestForPredict;

        System.out.println("Latency test completed");
        System.out.println("Total run time (ms): " + (endTimePredict - startTimePredict));
        System.out.println("Average latency per request : " + averageLatency);
        System.out.println("Number of successful requests sent: " + Client.getSuccessfulRequests());
        System.out.println("Number of unsuccessful requests: " + Client.getFailedRequests());

        // Calculate expected throughput using Little's Law
        double W = averageLatency/1000.0;
        double N = 1.0;
        double throughput =  N/W;
        System.out.println("Expected throughput (requests/sec): " + throughput);
    }

}

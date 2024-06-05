package client2; /**
 * Copyright (C), 2015-2024, XXX有限公司
 * FileName: client1.Client
 * Author:   jiang
 * Date:     6/2/24 5:59 PM
 * Description: Build multithread clients
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */

import client1.SkierEvent;
import com.google.gson.Gson;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SkiersApi;
import io.swagger.client.model.LiftRide;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 〈Build multithread clients〉
 *
 * @author jiang
 * @create 6/2/24
 * @since 1.0.0
 */
@Data
@ToString

public class Client2 implements Runnable {

    private Integer numRequests;

    private BlockingQueue<SkierEvent> eventQueue;
    private CountDownLatch latch;

    private SkiersApi skiersApi = new SkiersApi();

    private Integer retry = 5;

    public static AtomicInteger success = new AtomicInteger();

    public static AtomicInteger fail = new AtomicInteger();
    public static final Queue<Metric> metrics = new ConcurrentLinkedQueue<>();

    Client2(BlockingQueue<SkierEvent> eventQueue, CountDownLatch latch, Integer numRequests) {
        this.eventQueue = eventQueue;
        this.latch = latch;
        this.numRequests = numRequests;
    }


    Client2(BlockingQueue<SkierEvent> eventQueue, Integer numRequests) {
        this.eventQueue = eventQueue;
        this.numRequests = numRequests;
    }



    private boolean sendPostToServer(SkierEvent event) {
        for (int i = 1; i <= retry; i++) {
            try {
                skiersApi.setApiClient(event.getApiClient());
                LiftRide liftRide = new LiftRide();
                liftRide.setLiftID(event.getLiftID());
                liftRide.setTime(event.getTime());
                ApiResponse response = skiersApi.writeNewLiftRideWithHttpInfo(liftRide, event.getResortID(), String.valueOf(event.getSeasonID()), String.valueOf(event.getDayID()), event.getSkierID());
                if (response.getStatusCode() == 200) {
                    return true;
                }
            } catch (ApiException e) {
                System.out.println("send request failed for " + i + "time");
//                e.printStackTrace();
            }
        }
        return false;
    }

    public void run() {
        Gson gson = new Gson();
        try {
            for (int i = 0; i < numRequests; i++) {
                SkierEvent event = eventQueue.take();
                long startTime = System.currentTimeMillis();
                boolean res = sendPostToServer(event);
                long endTime = System.currentTimeMillis();
                long latency = endTime - startTime;
                if (res) {
                    incrementSuccess();
                    metrics.add(Metric.builder()
                            .startTime(startTime)
                            .latency(latency)
                            .requestType("POST")
                            .responseCode(200)
                            .build());
                } else {
                    incrementFail();
                }
            }
        } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
        }  finally {
            latch.countDown();
        }

    }

    private synchronized void incrementSuccess() {
        success.getAndIncrement();
    }

    private synchronized void incrementFail() {
        fail.getAndIncrement();
    }


    public static synchronized int getSuccessfulRequests() {
        return success.get();
    }

    public static synchronized int getFailedRequests() {
        return fail.get();
    }
}

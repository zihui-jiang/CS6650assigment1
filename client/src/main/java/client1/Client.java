package client1;

import com.google.gson.Gson;
import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SkiersApi;
import io.swagger.client.model.LiftRide;
import lombok.Data;
import lombok.ToString;

import java.util.concurrent.BlockingQueue;
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
public class Client implements Runnable {

    private Integer numRequests;

    private BlockingQueue<SkierEvent> eventQueue;
    private CountDownLatch latch;

    private SkiersApi skiersApi = new SkiersApi();

    private Integer retry = 5;

    public static AtomicInteger success = new AtomicInteger();

    public static AtomicInteger fail = new AtomicInteger();

    private ApiClient apiClient;

    Client(BlockingQueue<SkierEvent> eventQueue,CountDownLatch latch, Integer numRequests, ApiClient apiClient) {
        this.eventQueue = eventQueue;
        this.latch = latch;
        this.numRequests = numRequests;
        this.apiClient = apiClient;
    }


    private boolean sendPostToServer(SkierEvent event) {
        for (int i = 1; i <= retry; i++) {
            try {
                skiersApi.setApiClient(apiClient);
                LiftRide liftRide = new LiftRide();
                liftRide.setLiftID(event.getLiftID());
                liftRide.setTime(event.getTime());
                ApiResponse response = skiersApi.writeNewLiftRideWithHttpInfo(liftRide, event.getResortID(), String.valueOf(event.getSeasonID()), String.valueOf(event.getDayID()), event.getSkierID());
                if (response.getStatusCode() == 200) {
                    return true;
                }
            } catch (ApiException e) {
                System.out.println("send request failed for " + i + "time");
            }
        }
        return false;
    }

    public void run() {
        try {
            for (int i = 0; i < numRequests; i++) {
                SkierEvent event = eventQueue.take();
                if (sendPostToServer(event)) {
                    incrementSuccess();
                } else {
                    incrementFail();
                }
            }
        } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
        }
        latch.countDown();
    }

    private synchronized void incrementSuccess() {
        success.incrementAndGet();
    }

    private synchronized void incrementFail() {
        fail.incrementAndGet();
    }


    static  int getSuccessfulRequests() {
        return success.get();
    }

    static  int getFailedRequests() {
        return fail.get();
    }
}

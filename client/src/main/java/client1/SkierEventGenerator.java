package client1; /**
 * Copyright (C), 2015-2024, XXX有限公司
 * FileName: client1.SkierEventGenerator
 * Author:   jiang
 * Date:     6/3/24 2:46 PM
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */

import io.swagger.client.ApiClient;
import lombok.*;

import java.util.concurrent.BlockingQueue;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author jiang
 * @create 6/3/24
 * @since 1.0.0
 */
@Data
@ToString
@NoArgsConstructor
@Builder
public class SkierEventGenerator implements Runnable {
    private BlockingQueue<SkierEvent> eventQueue;
    private int eventCount;

    public SkierEventGenerator(BlockingQueue<SkierEvent> eventQueue, int eventCount) {
        this.eventQueue = eventQueue;
        this.eventCount = eventCount;
    }

    public void run() {
        for (int i = 0; i < eventCount; i++) {
            SkierEvent event = new SkierEvent(
                    (int) (Math.random() * 100000) + 1,
                    (int) (Math.random() * 10) + 1,
                    (int) (Math.random() * 40) + 1,
                    2024,
                    1,
                    (int) (Math.random() * 360) + 1
            );
            try {
                eventQueue.put(event);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

    }
}

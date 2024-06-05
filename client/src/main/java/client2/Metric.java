/**
 * Copyright (C), 2015-2024, XXX有限公司
 * FileName: Metric
 * Author:   jiang
 * Date:     6/5/24 1:51 PM
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package client2;

import lombok.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author jiang
 * @create 6/5/24
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@Builder
@ToString
public class Metric {
    private final long startTime;
    private final String requestType;
    private final long latency;
    private final int responseCode;

    public static void processMetrics(Queue<Metric> metrics) {
        List<Long> latencies = metrics.stream()
                .map(Metric::getLatency)
                .sorted()
                .collect(Collectors.toList());

        long totalLatency = latencies.stream().mapToLong(Long::longValue).sum();
        double meanResponseTime = totalLatency / (double) latencies.size();
        double medianResponseTime = latencies.size() % 2 == 0 ?
                (latencies.get(latencies.size() / 2 - 1) + latencies.get(latencies.size() / 2)) / 2.0 :
                latencies.get(latencies.size() / 2);
        double p99ResponseTime = latencies.get((int) (latencies.size() * 0.99) - 1);
        long minResponseTime = Collections.min(latencies);
        long maxResponseTime = Collections.max(latencies);

        System.out.println("Mean response time (ms): " + meanResponseTime);
        System.out.println("Median response time (ms): " + medianResponseTime);
        System.out.println("99th percentile response time (ms): " + p99ResponseTime);
        System.out.println("Min response time (ms): " + minResponseTime);
        System.out.println("Max response time (ms): " + maxResponseTime);
    }

    public static void writeMetricsToCSV(Queue<Metric> metrics, String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write("startTime,requestType,latency,responseCode\n");
            for (Metric metric : metrics) {
                writer.write(String.format("%d,%s,%d,%d\n", metric.getStartTime(), metric.getRequestType(), metric.getLatency(), metric.getResponseCode()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

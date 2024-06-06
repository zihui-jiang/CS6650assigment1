/**
 * Copyright (C), 2015-2024, XXX有限公司
 * FileName: Throughput
 * Author:   jiang
 * Date:     6/5/24 11:11 PM
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package client2;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

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
public class Throughput {
    private final long currTime;

    private final double throughput;

    public static void writeToCSV(List<Throughput> list) {
        try (FileWriter writer = new FileWriter("throughput.csv")) {
            writer.write("currTime,throughput\n");
            for (Throughput tp : list) {
                writer.write(String.format("%d,%f\n", tp.getCurrTime(), tp.getThroughput()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

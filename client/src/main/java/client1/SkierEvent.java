package client1; /**
 * Copyright (C), 2015-2024, XXX有限公司
 * FileName: Rider
 * Author:   jiang
 * Date:     6/3/24 2:22 PM
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */

import io.swagger.client.ApiClient;
import lombok.*;

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
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SkierEvent {

    private int skierID;
    private int resortID;
    private int liftID;
    private int seasonID;
    private int dayID;
    private int time;

    private ApiClient apiClient;
}

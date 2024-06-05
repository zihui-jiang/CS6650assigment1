/**
 * Copyright (C), 2015-2024, XXX有限公司
 * FileName: LiftRideDTO
 * Author:   jiang
 * Date:     6/3/24 11:36 AM
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package DTO;

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
public class LiftRideDTO {
    private Integer time;

    private Integer liftID;
}

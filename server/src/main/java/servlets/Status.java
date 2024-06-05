/**
 * Copyright (C), 2015-2024, XXX有限公司
 * FileName: Status
 * Author:   jiang
 * Date:     6/2/24 1:02 PM
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package servlets;

import lombok.*;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author jiang
 * @create 6/2/24
 * @since 1.0.0
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Status {
    private Integer status;


    private String description;

    private String data;

}

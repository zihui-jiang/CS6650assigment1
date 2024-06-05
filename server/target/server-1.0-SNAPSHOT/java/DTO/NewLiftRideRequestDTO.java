/**
 * Copyright (C), 2015-2024, XXX有限公司
 * FileName: NewLiftRideVO
 * Author:   jiang
 * Date:     6/2/24 8:45 AM
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package DTO;

import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

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
public class NewLiftRideRequestDTO {
    // todo dto interface
    @NotNull
    private LiftRideDTO liftRide;

    @NotNull
    private Integer resortID;

    @NotNull
    private String seasonID;

    @NotNull
    @Min(1)
    @Max(366)
    private Integer dayID;

    @NotNull
    private Integer skierID;

    public NewLiftRideRequestDTO create(Integer resortID, String seasonID, Integer dayID, Integer skierID) {
        return  NewLiftRideRequestDTO.builder()
                .resortID(resortID)
                .dayID(dayID)
                .seasonID(seasonID)
                .skierID(skierID).build();
    }

}

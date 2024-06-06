package client1;

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
}

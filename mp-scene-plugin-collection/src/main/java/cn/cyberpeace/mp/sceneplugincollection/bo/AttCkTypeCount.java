package cn.cyberpeace.mp.sceneplugincollection.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName AttCkTypeCount
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttCkTypeCount {
    private String typeName;
    private String type;
    private Long times;

    public AttCkTypeCount(String type, Long times) {
        this.type = type;
        this.times = times;
    }
}

package cn.cyberpeace.mp.sceneplugincollection.bo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @ClassName FlowMirrorCreateRes
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@Accessors(chain = true)
public class FlowMirrorCreateRes {
    private Integer optionKey;
    private String flowMirrorId;
}
